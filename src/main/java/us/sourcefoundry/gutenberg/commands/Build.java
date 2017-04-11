package us.sourcefoundry.gutenberg.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.factories.TemplateContextFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.models.templates.AnswersFileTemplate;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.UserPromptService;
import us.sourcefoundry.gutenberg.utils.Pair;

import java.io.*;
import java.lang.reflect.Type;
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
            String installDir = this.applicationContext.getInstallDirectory();

            Map<String, FormeInventoryItem> inventory = new HashMap<>();
            Type type = new TypeToken<Map<String, FormeInventoryItem>>(){}.getType();

            try {
                inventory = (new Gson()).fromJson(new FileReader((new FileSystemService()).getByLocation(installDir + "/inventory.json")),type);
            } catch (FileNotFoundException e) {
                if(!this.cli.hasOption("local")) {
                    (new Console()).info("! No inventory found.");
                    return;
                }
            }

            String sourceDirectoryPath = installDir + "/formes/" + (cli.getArgList().size() > 1 ? inventory.get(cli.getArgList().get(1).toString()).getInstallPath() : "");
            String outputDirectoryPath = this.applicationContext.getWorkingDirectory();

            if(this.cli.hasOption("local"))
                sourceDirectoryPath = this.cli.getOptionValue("local");

            if(this.cli.hasOption("o"))
                outputDirectoryPath = this.cli.getOptionValue("o");

            System.out.println(sourceDirectoryPath);

            //Get the forme file and make sure it exists.
            File formeFile = (new FileSystemService()).getByLocation("{0}/forme.yml", sourceDirectoryPath);
            if (!formeFile.exists()) {
                (new Console()).error("! Could not locate a forme file in source location.  Does it needs to be initialized?");
                return;
            }

            //Parse it into a context object.
            FormeContext formeContext = (new TemplateContextFactory()).make(
                    (new FileSystemService()).streamFile(formeFile)
            );

            (new Console()).message("Building Template \"{0}\"", formeContext.getName());

            //Check to make sure the output directory is available.
            if (!this.checkOutputDir((new FileSystemService()).getByLocation(outputDirectoryPath), this.cli.hasOption("f")))
                return;

            //Set the application source directory.
            this.applicationContext.setSourceDirectory(sourceDirectoryPath);
            //Set the application context with the output directory.
            this.applicationContext.setOutputDirectory(outputDirectoryPath);

            //Run any prompts the forme may require.
            HashMap<String, Object> userResponses = getUserResponseToPrompts(formeContext, cli);
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
                saveUserAnswersFromPrompts(cli, userResponses);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static HashMap<String, Object> getUserResponseToPrompts(FormeContext formeContext, Cli cli) throws FileNotFoundException {
        if (!cli.hasOption("a"))
            return (new UserPromptService(formeContext).requestAnswers());

        String answersFile = cli.getOptionValue("a");
        InputStream answerFileIS = new FileInputStream((new FileSystemService()).getByLocation(answersFile));
        Yaml parser = new Yaml(new Constructor(HashMap.class));
        return (HashMap<String, Object>) parser.load(answerFileIS);
    }

    private static void saveUserAnswersFromPrompts(Cli cli, HashMap<String, Object> userResponses) throws FileNotFoundException {
        List<Pair<Object, Object>> answers = new ArrayList<>();
        String answersFilePath = cli.getOptionValue("s");

        for (Map.Entry entry : userResponses.entrySet())
            answers.add(new Pair<>(entry.getKey(), entry.getValue()));

        (new Console()).info("+ Creating Answer File... {0}", answersFilePath);
        (new AnswersFileTemplate()).create(answersFilePath,userResponses);
    }

    private boolean checkOutputDir(File outputDirectory, boolean force) {
        boolean outputDirectoryExists = outputDirectory.exists();
        boolean isDirectory = outputDirectory.isDirectory();

        if (outputDirectoryExists && !force) {
            boolean isEmptyDirectory = outputDirectory.list().length == 0;

            if (isDirectory && !isEmptyDirectory) {
                (new Console()).error("! Could not build. {0} exists and is not empty.", outputDirectory.getAbsolutePath());
                return false;
            }

            if (!isDirectory) {
                (new Console()).error("! Could not build. {0} exists and is not a directory.", outputDirectory.getAbsolutePath());
                return false;
            }
        }

        if (outputDirectoryExists && force && !isDirectory) {
            (new Console()).error("! Could not force build. {0} exists and is not a directory.", outputDirectory.getAbsolutePath());
            return false;
        }

        if (outputDirectoryExists && force && isDirectory)
            (new Console()).warning("# {0} already exists. Building anyways.", outputDirectory.getAbsolutePath());

        return true;

    }
}
