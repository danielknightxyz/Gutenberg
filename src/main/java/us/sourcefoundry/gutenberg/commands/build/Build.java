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
import us.sourcefoundry.gutenberg.models.operations.CopyOperation;
import us.sourcefoundry.gutenberg.models.operations.DirectoryCreation;
import us.sourcefoundry.gutenberg.models.operations.FileCreation;
import us.sourcefoundry.gutenberg.models.templates.AnswersFileTemplate;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.console.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.UserPromptService;
import us.sourcefoundry.gutenberg.utils.DependencyInjector;
import us.sourcefoundry.gutenberg.utils.Pair;

import javax.inject.Inject;
import java.io.File;
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

            this.console.message("\nBuilding...\n");

            //Get the forme location.
            FormeLocation formeLocation = FormeLocation.fromCli(this.cli, this.applicationContext);

            //If forme was not found, then just bounce out.
            if (formeLocation == null || formeLocation.getPath() == null)
                return;

            //Get the forme file and make sure it exists.
            Forme forme = Forme.fromLocation(formeLocation);

            //Only continue if the forme was found.
            if (forme == null)
                return;

            //Get the build path for the generated files.
            BuildLocation buildLocation = BuildLocation.fromCli(this.cli, this.applicationContext);

            //Check to newInstance sure the output directory is available.
            if (!this.checkBuildPath(buildLocation, this.cli.hasOption("f")))
                return;

            //Run any prompts the forme may require.
            HashMap<String, Object> userResponses = this.getUserResponseToPrompts(forme, cli);

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
            forme.getCopy().stream().filter(c -> c.getType().equals("directory")).forEach(c -> (DependencyInjector.getInstance(CopyOperation.class)).execute(c, buildContext));
            //Perform the static file copy.
            forme.getCopy().stream().filter(c -> c.getType().equals("file")).forEach(c -> (DependencyInjector.getInstance(CopyOperation.class)).execute(c, buildContext));

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
    private HashMap<String, Object> getUserResponseToPrompts(Forme forme, Cli cli) throws FileNotFoundException {
        Map<String, Object> answers = new HashMap<>();

        if (cli.hasOption("a")) {
            String answersFile = cli.getOptionValue("a");
            InputStream answerFileIS = new FileInputStream((new FileSystemService()).getByLocation(answersFile));
            Yaml parser = new Yaml(new Constructor(HashMap.class));
            answers = (HashMap<String, Object>) parser.load(answerFileIS);
        }

        return new UserPromptService(forme).requestAnswers(answers);

    }

    /**
     * This will save user's answers to the anwser file.
     *
     * @param cli           The command line.
     * @param userResponses The user responses to prompts.
     */
    private void saveUserAnswersFromPrompts(Cli cli, HashMap<String, Object> userResponses, List<String> allowed) throws FileNotFoundException {
        List<Pair<Object, Object>> answers = new ArrayList<>();
        String answersFilePath = (cli.hasOption("s") ? cli.getOptionValue("s") : "auto-save.yml");

        //Loop through the answers and only save those that are allowed to be saved.
        for (Map.Entry<String, Object> entry : userResponses.entrySet())
            if (allowed.contains(entry.getKey()))
                answers.add(new Pair<>(entry.getKey(), entry.getValue()));

        this.console.message("\nCreating Answer File... {0}", answersFilePath);
        (new AnswersFileTemplate()).create(answersFilePath, answers);
    }

    /**
     * This function will check the build path and make sure its available for use as a build location.
     *
     * @param buildLocation The location to build.
     * @param force         Should the build location be used regardless of readiness.
     * @return boolean
     */
    private boolean checkBuildPath(BuildLocation buildLocation, boolean force) {
        File buildLocationObj = (new FileSystemService()).getByLocation(buildLocation.getPath());

        boolean outputDirectoryExists = buildLocationObj.exists();
        boolean isDirectory = buildLocationObj.isDirectory();

        if (outputDirectoryExists && !force) {
            boolean isEmptyDirectory = buildLocationObj.list().length == 0;

            if (isDirectory && !isEmptyDirectory) {
                this.console.error("Could not build. {0} exists and is not empty.\n", buildLocationObj.getAbsolutePath());
                return false;
            }

            if (!isDirectory) {
                this.console.error("Could not build. {0} exists and is not a directory.\n", buildLocationObj.getAbsolutePath());
                return false;
            }
        }

        if (outputDirectoryExists && force && !isDirectory) {
            this.console.error("Could not force build. {0} exists and is not a directory.\n", buildLocationObj.getAbsolutePath());
            return false;
        }

        if (outputDirectoryExists && force)
            this.console.warning("{0} already exists. Building anyways.\n", buildLocationObj.getAbsolutePath());

        if (!outputDirectoryExists && !buildLocationObj.mkdir()) {
            this.console.warning("{0} did not exist and could not be created.\n", buildLocationObj.getAbsolutePath());
            return false;
        }

        return true;
    }
}
