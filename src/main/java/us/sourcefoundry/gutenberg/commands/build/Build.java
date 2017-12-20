package us.sourcefoundry.gutenberg.commands.build;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.BuildContext;
import us.sourcefoundry.gutenberg.models.BuildLocation;
import us.sourcefoundry.gutenberg.models.FormeLocation;
import us.sourcefoundry.gutenberg.models.forme.Forme;
import us.sourcefoundry.gutenberg.models.forme.VarPrompt;
import us.sourcefoundry.gutenberg.models.operations.DirectoryCopy;
import us.sourcefoundry.gutenberg.models.operations.DirectoryCreation;
import us.sourcefoundry.gutenberg.models.operations.FileCopy;
import us.sourcefoundry.gutenberg.models.operations.FileCreation;
import us.sourcefoundry.gutenberg.models.templates.AnswersFileTemplate;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.UserPromptService;
import us.sourcefoundry.gutenberg.services.console.Console;
import us.sourcefoundry.gutenberg.utils.DependencyInjector;
import us.sourcefoundry.gutenberg.utils.Pair;

import javax.inject.Inject;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class will execute the BUILD action for Gutenberg.
 */
public class Build implements Command {

    //The application context.
    private ApplicationContext applicationContext;
    //The CLI.
    private Cli cli;
    //The console.
    private Console console;


    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cli                The cli service.
     * @param console            The console service.
     */
    @Inject
    public Build(ApplicationContext applicationContext, Cli cli, Console console) {
        this.applicationContext = applicationContext;
        this.cli = cli;
        this.console = console;
    }

    /**
     * Executes the BUILD action.
     */
    @Override
    public void execute() {
        try {

            //Get the forme location.
            FormeLocation formeLocation = FormeLocation.fromCli(this.cli, this.applicationContext, this.console);

            //If forme was not found, then just bounce out.
            if (formeLocation == null || formeLocation.getPath() == null)
                return;

            //Get the forme file and make sure it exists.
            Forme forme = Forme.fromLocation(formeLocation, this.console);

            //Only continue if the forme was found.
            if (forme == null)
                return;

            //Get the build path for the generated files.
            BuildLocation buildLocation = BuildLocation.fromCli(this.cli, this.applicationContext);

            //Check to newInstance sure the output directory is available.
            BuildPathChecker buildPathChecker = new BuildPathChecker();
            boolean buildPathCheck = buildPathChecker.check(buildLocation, this.cli.hasOption("f"));

            //Check to see if there were in any errors or warnings.
            if (buildPathChecker.hasErrors())
                buildPathChecker.getErrors().forEach(e -> this.console.error(e.getDescription(), buildLocation.getPath()));
            if (buildPathChecker.hasWarnings())
                buildPathChecker.getWarnings().forEach(e -> this.console.warning(e.getDescription(), buildLocation.getPath()));

            //If the check failed, meaning the output directly is not available, then bounce out.
            if (!buildPathCheck)
                return;

            //Run any prompts the forme may require.
            Map<String, Object> userResponses = this.getUserResponseToPrompts(forme, cli);

            //Create a build context to reduce the number of parameters getting passed around.
            BuildContext buildContext = new BuildContext(buildLocation, forme, formeLocation, userResponses);

            /*
             * Run the process bellow.
             */
            //Show the message that the build is starting.
            this.console.message("Building Forme \"{0}\"", forme.getName());
            //Make the directories.
            forme.getDirectories().forEach(d -> (DependencyInjector.getInstance(DirectoryCreation.class)).execute(d, buildContext));
            //Make the files from the templates.
            forme.getFiles().forEach(f -> (DependencyInjector.getInstance(FileCreation.class)).execute(f, buildContext));
            //Perform the static directory copy.  This is the same data but we do the directory entries first, since
            //they will need to exist in case a file copy needs it.
            forme.getCopy().stream().filter(c -> c.getType().equals("directory")).forEach(c -> (DependencyInjector.getInstance(DirectoryCopy.class)).execute(c, buildContext));
            //Perform the static file copy.
            forme.getCopy().stream().filter(c -> c.getType().equals("file")).forEach(c -> (DependencyInjector.getInstance(FileCopy.class)).execute(c, buildContext));

            //If the user wants their answers saved, then this will save those answers for use in later runs.
            //Also if there is an autosave enabled.
            if (cli.hasOption("s") || forme.shouldAutoSaveAnswers()) {
                //Get the prompts which are allowed to be saved.
                List<String> allowed = forme.getPrompts().stream().filter(VarPrompt::isAllowSave).map(VarPrompt::getName).collect(Collectors.toList());
                this.saveUserAnswersFromPrompts(cli, userResponses, allowed);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This will use the user prompt service to get any required information from the user.  This also will use an answer
     * file if its provided in the answersfile option.
     *
     * @param forme The forme file.
     * @param cli   The command line.
     * @return Map of variable name to response.
     */
    private Map<String, Object> getUserResponseToPrompts(Forme forme, Cli cli) throws FileNotFoundException {
        Map<String, Object> answers = new HashMap<>();

        if (cli.hasOption("a")) {
            String answersFile = cli.getOptionValue("a");
            InputStream answerFileIS = new FileInputStream((new FileSystemService()).getByLocation(answersFile));
            Yaml parser = new Yaml(new Constructor(HashMap.class));
            answers = (Map<String, Object>) parser.load(answerFileIS);
        }

        return new UserPromptService(forme, this.console).requestAnswers(answers);

    }

    /**
     * This will save user's answers to the anwser file.
     *
     * @param cli           The command line.
     * @param userResponses The user responses to prompts.
     */
    private void saveUserAnswersFromPrompts(Cli cli, Map<String, Object> userResponses, List<String> allowed) throws FileNotFoundException {
        List<Pair<Object, Object>> answers = new ArrayList<>();
        String answersFilePath = (cli.hasOption("s") ? cli.getOptionValue("s") : "auto-save.yml");

        //Loop through the answers and only save those that are allowed to be saved.
        for (Map.Entry<String, Object> entry : userResponses.entrySet())
            if (allowed.contains(entry.getKey()))
                answers.add(new Pair<>(entry.getKey(), entry.getValue()));

        this.console.message("\nCreating Answer File... {0}", answersFilePath);
        (new AnswersFileTemplate()).create(answersFilePath, answers);
    }
}
