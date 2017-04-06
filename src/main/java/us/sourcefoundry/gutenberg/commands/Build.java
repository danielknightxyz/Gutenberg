package us.sourcefoundry.gutenberg.commands;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.io.FileUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.factories.TemplateContextFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.CopyEntry;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.services.*;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.utils.Pair;
import us.sourcefoundry.gutenberg.utils.SystemPathTemplate;

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
            String sourceDirectory = this.applicationContext.getSourceDirectory();
            String userOutputDirectory = cli.getArgList().get(1).toString();

            //Get the forme file and make sure it exists.
            File formeFile = new File(MessageFormat.format("{0}/forme.yml",sourceDirectory));
            if(!formeFile.exists()) {
                (new Console()).error("! Could not locate a forme file in source location.  Does it needss to be initialized?");
                return;
            }

            //Get the template file.
            InputStream templateFile = new FileInputStream(formeFile);
            //Parse it into a context object.
            FormeContext formeContext = (new TemplateContextFactory()).make(templateFile);

            //Set the application context with the output directory.
            this.applicationContext.setOutputDirectory(userOutputDirectory);

            (new Console()).message("Building Template \"{0}\"", formeContext.getName());

            //Check to make sure the output directory is available.
            if (!this.checkOutputDir(userOutputDirectory,this.cli.hasOption("f")))
                return;

            //Run any prompts the forme may require.
            HashMap<String, Object> userResponses = getUserResponseToPrompts(formeContext, sourceDirectory, cli);
            this.applicationContext.setUserResponses(userResponses);

            //If the user wants their answers saved, then this will save those answers for use in later runs.
            if (cli.hasOption("s"))
                saveUserAnswersFromPrompts(cli, sourceDirectory, userResponses);

            /*
             * Run the process bellow.
             */
            //Make the directories.
            formeContext.getDirectories().forEach(d -> (new DirectoryService(applicationContext, formeContext)).createFromTemplate(d));
            //Make the files from the templates.
            formeContext.getFiles().forEach(f -> (new FileService(applicationContext, formeContext)).createFromTemplate(f));
            //Perform the static directory copy.
            formeContext.getCopy().stream().filter(c -> c.getType().equals("directory")).forEach(c -> copyDir(c, formeContext, applicationContext));
            //Perform the static file copy.
            formeContext.getCopy().stream().filter(c -> c.getType().equals("file")).forEach(c -> copyFile(c, formeContext, applicationContext));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void copyDir(CopyEntry copy, FormeContext formeContext, ApplicationContext applicationContext) {
        try {
            String sorucePath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), copy.getSource());
            String destPath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), copy.getDest());

            (new Console()).info("+ Copying Directory... {0}", destPath);

            FileUtils.copyDirectory(
                    new File(sorucePath), new File(destPath)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(CopyEntry copy, FormeContext formeContext, ApplicationContext applicationContext) {
        try {
            String sorucePath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), copy.getSource());
            String destPath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), copy.getDest());

            (new Console()).info("+ Copying File... {0}", destPath);

            FileUtils.copyFile(
                    new File(sorucePath), new File(destPath)
            );
        } catch (IOException e) {
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
