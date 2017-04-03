package us.sourcefoundry.gutenberg;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.factories.TemplateContextFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.CopyTemplateEntry;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.DirectoryService;
import us.sourcefoundry.gutenberg.services.FileService;
import us.sourcefoundry.gutenberg.services.UserPromptService;
import us.sourcefoundry.gutenberg.utils.Pair;
import us.sourcefoundry.gutenberg.utils.SystemPathGenerator;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        /*
         * Setup some shit.
         */

        Cli cli = new Cli(args);

        if (cli.hasBlockingOption()) {
            cli.printBlockingOption();
            return;
        }

        List remainingArgs = cli.getArgList();

        //Get some details about the system.
        String workingDir = System.getProperty("user.dir");
        String sourceDir = workingDir;
        String outputDir = workingDir;

        if (remainingArgs.size() == 1) {
            outputDir = remainingArgs.get(0).toString();
        }

        if (remainingArgs.size() == 2) {
            sourceDir = (remainingArgs.get(0).toString().equals(".") ? workingDir : remainingArgs.get(0).toString());
            outputDir = remainingArgs.get(1).toString();
        }

        //Get the template file.
        InputStream templateFile = new FileInputStream(new File(MessageFormat.format("{0}/forme.yml", sourceDir)));
        //Parse it into a context object.
        FormeContext formeContext = (new TemplateContextFactory()).make(templateFile);
        //Run any prompts the forme may require.
        HashMap<String, Object> userResponses = new HashMap<>();

        if (!cli.hasOption("answersfile"))
            userResponses = (new UserPromptService(formeContext).requestAnswers());

        if (cli.hasOption("answersfile")) {
            String answersFile = cli.getOptionValue("answersfile");
            InputStream answerFileIS = new FileInputStream(new File(MessageFormat.format("{0}/{1}", sourceDir, answersFile)));

            Yaml parser = new Yaml(
                    new Constructor(HashMap.class)
            );
            userResponses = (HashMap<String, Object>) parser.load(answerFileIS);
        }

        if (cli.hasOption("saveanswers")) {
            List<Pair<Object, Object>> answers = new ArrayList<>();
            String answersFile = cli.getOptionValue("saveanswers");

            for (Map.Entry entry : userResponses.entrySet())
                answers.add(new Pair<>(entry.getKey(), entry.getValue()));

            String answersFilePath = sourceDir + "/" + answersFile;

            System.out.println(MessageFormat.format("Creating Answer File... {0}", answersFilePath));

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

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.setWorkingDirectory(workingDir);
        applicationContext.setSourceDirectory(sourceDir);
        applicationContext.setOutputDirectory(outputDir);
        applicationContext.setUserResponses(userResponses);

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
    }

    private static void copyDir(CopyTemplateEntry copy, FormeContext formeContext, ApplicationContext applicationContext) {
        try {
            String sorucePath = (new SystemPathGenerator(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), copy.getSource());
            String destPath = (new SystemPathGenerator(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), copy.getDest());

            System.out.println(MessageFormat.format("Copying Directory... {0}", destPath));

            FileUtils.copyDirectory(
                    new File(sorucePath), new File(destPath)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void copyFile(CopyTemplateEntry copy, FormeContext formeContext, ApplicationContext applicationContext) {
        try {
            String sorucePath = (new SystemPathGenerator(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), copy.getSource());
            String destPath = (new SystemPathGenerator(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), copy.getDest());

            System.out.println(MessageFormat.format("Copying File... {0}", destPath));

            FileUtils.copyFile(
                    new File(sorucePath), new File(destPath)
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
