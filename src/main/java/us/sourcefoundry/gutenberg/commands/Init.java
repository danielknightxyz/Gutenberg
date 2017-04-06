package us.sourcefoundry.gutenberg.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.services.*;
import us.sourcefoundry.gutenberg.services.Console;

import java.io.*;
import java.util.HashMap;

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
        (new Console()).message("Initializing Forme...");

        String destFilePath = this.applicationContext.getSourceDirectory() + "/forme.yml";

        if(!this.checkOutputLocation(destFilePath))
            return;

        InputStream templateFileStream = Init.class.getClassLoader().getResourceAsStream("forme.yml.mustache");
        (new FileTemplate()).create(new InputStreamReader(templateFileStream),destFilePath,new HashMap<>());
    }

    private boolean checkOutputLocation(String destFilePath){
        File formeFile = new File(destFilePath);

        if(formeFile.exists() && !cli.hasOption("force")){
            (new Console()).error("! Already initialized.");
            return false;
        }

        if(formeFile.exists() && cli.hasOption("force"))
            (new Console()).warning("# Already initialized. Continuing anyways.");

        return true;
    }
}
