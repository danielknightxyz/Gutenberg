package us.sourcefoundry.gutenberg.commands;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.google.gson.Gson;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.factories.TemplateContextFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.UserPromptService;
import us.sourcefoundry.gutenberg.utils.Pair;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class Build implements Command {

    private ApplicationContext applicationContext;
    private Cli cli;

    public Build(ApplicationContext applicationContext, Cli cli) {
        this.applicationContext = applicationContext;
        this.cli = cli;
    }

    @Override
    public void execute() {
        try {
            String installDir = System.getProperty("user.home") + "/.gutenberg";

            HashMap<String, FormeInventoryItem> inventory = new HashMap<>();

            try {
                inventory = (new Gson()).fromJson(new FileReader((new FileSystemService()).getByLocation(installDir + "/inventory.json")),HashMap.class);
            } catch (FileNotFoundException e) {
                (new Console()).info("! No inventory found.");
                return;
            }

            String sourceDirectory = installDir + "/formes/" + (cli.getArgList().size() > 1 ? inventory.get(cli.getArgList().get(1).toString()) : "");
            String userOutputDirectory = this.applicationContext.getWorkingDirectory();

            if(this.cli.hasOption("local"))
                sourceDirectory = this.cli.getOptionValue("local");

            if(this.cli.hasOption("o"))
                userOutputDirectory = this.cli.getOptionValue("o");

            //Get the forme file and make sure it exists.
            File formeFile = (new FileSystemService()).getByLocation(MessageFormat.format("{0}/forme.yml", sourceDirectory));
            if (!formeFile.exists()) {
                (new Console()).error("! Could not locate a forme file in source location.  Does it needss to be initialized?");
                return;
            }

            //Parse it into a context object.
            FormeContext formeContext = (new TemplateContextFactory()).make(
                    (new FileSystemService()).streamFile(formeFile)
            );

            (new Console()).message("Building Template \"{0}\"", formeContext.getName());

            //Check to make sure the output directory is available.
            if (!this.checkOutputDir(userOutputDirectory, this.cli.hasOption("f")))
                return;

            //Set the application source directory.
            this.applicationContext.setSourceDirectory(sourceDirectory);
            //Set the application context with the output directory.
            this.applicationContext.setOutputDirectory(userOutputDirectory);

            //Run any prompts the forme may require.
            HashMap<String, Object> userResponses = getUserResponseToPrompts(formeContext, sourceDirectory, cli);
            this.applicationContext.setUserResponses(userResponses);

            /*
             * Run the process bellow.
             */
            //Make the directories.
            formeContext.getDirectories().forEach(d -> d.create(applicationContext, formeContext));
            //Make the files from the templates.
            formeContext.getFiles().forEach(f -> f.create(applicationContext, formeContext));
            //Perform the static directory copy.
            formeContext.getCopy().stream().filter(c -> c.getType().equals("directory")).forEach(c -> c.copy(formeContext, applicationContext));
            //Perform the static file copy.
            formeContext.getCopy().stream().filter(c -> c.getType().equals("file")).forEach(c -> c.copy(formeContext, applicationContext));

            //If the user wants their answers saved, then this will save those answers for use in later runs.
            if (cli.hasOption("s"))
                saveUserAnswersFromPrompts(cli, sourceDirectory, userResponses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Object> getUserResponseToPrompts(FormeContext formeContext, String sourceDir, Cli cli) throws FileNotFoundException {
        if (!cli.hasOption("a"))
            return (new UserPromptService(formeContext).requestAnswers());

        String answersFile = cli.getOptionValue("a");
        InputStream answerFileIS = new FileInputStream(new File(MessageFormat.format("{0}/{1}", sourceDir, answersFile)));
        Yaml parser = new Yaml(new Constructor(HashMap.class));
        return (HashMap<String, Object>) parser.load(answerFileIS);
    }

    private static void saveUserAnswersFromPrompts(Cli cli, String sourceDir, HashMap<String, Object> userResponses) throws FileNotFoundException {
        List<Pair<Object, Object>> answers = new ArrayList<>();
        String answersFile = cli.getOptionValue("s");

        for (Map.Entry entry : userResponses.entrySet())
            answers.add(new Pair<>(entry.getKey(), entry.getValue()));

        String answersFilePath = sourceDir + "/" + answersFile;

        (new Console()).info("+ Creating Answer File... {0}", answersFilePath);

        PrintWriter writer = new PrintWriter(answersFilePath);
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader("---\n{{#answers}}{{key}}: {{value}}\n{{/answers}}"), UUID.randomUUID().toString());
        mustache.execute(
                writer,
                new HashMap<String, Object>() {{
                    put("answers", answers);
                }}
        );
        writer.flush();
    }

    private boolean checkOutputDir(String outputDirectory, boolean force) {
        File existingOutputDirectory = new File(outputDirectory);
        boolean outputDirectoryExists = existingOutputDirectory.exists();
        boolean isDirectory = existingOutputDirectory.isDirectory();

        if (outputDirectoryExists && !force) {
            boolean isEmptyDirectory = existingOutputDirectory.list().length == 0;

            if (isDirectory && !isEmptyDirectory) {
                (new Console()).error("! Could not build. {0} exists and is not empty.", outputDirectory);
                return false;
            }

            if (!isDirectory) {
                (new Console()).error("! Could not build. {0} exists and is not a directory.", outputDirectory);
                return false;
            }
        }

        if (outputDirectoryExists && force && !isDirectory) {
            (new Console()).error("! Could not force build. {0} exists and is not a directory.", outputDirectory);
            return false;
        }

        if (outputDirectoryExists && force && isDirectory)
            (new Console()).warning("# {0} already exists. Building anyways.", outputDirectory);

        return true;

    }
}
