package us.sourcefoundry.gutenberg.commands;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.*;
import us.sourcefoundry.gutenberg.services.Console;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class Init implements Command {

    private final static Logger logger = LoggerFactory.getLogger(Init.class);

    private ApplicationContext applicationContext;
    private Cli cli;

    public Init(ApplicationContext applicationContext, Cli cli){
        this.applicationContext = applicationContext;
        this.cli = cli;
    }

    @Override
    public void execute() {

        try {
            String destFilePath = this.applicationContext.getSourceDirectory() + "/forme.yml";
            InputStream templateFileStream = Init.class.getClassLoader().getResourceAsStream("forme.yml.mustache");

            (new Console()).message("Initializing Forme...");

            File formeFile = new File(destFilePath);

            if(formeFile.exists() && !cli.hasOption("force")){
                (new Console()).error("! Already initialized.");
                return;
            }

            if(formeFile.exists() && cli.hasOption("force")){
                (new Console()).warning("# Already initialized. Continuing anyways.");
            }

            PrintWriter writer = new PrintWriter(destFilePath);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new InputStreamReader(templateFileStream), UUID.randomUUID().toString());
            mustache.execute(writer, new HashMap<String, Object>());
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }
}
