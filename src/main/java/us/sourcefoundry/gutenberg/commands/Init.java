package us.sourcefoundry.gutenberg.commands;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.Console;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Init implements Command {

    private final static Logger logger = LoggerFactory.getLogger(Init.class);

    private ApplicationContext applicationContext;
    private Cli cli;

    @Inject
    public Init(ApplicationContext applicationContext, Cli cli) {
        this.applicationContext = applicationContext;
        this.cli = cli;
    }

    @Override
    public void execute() {
        (new Console()).message("Initializing Forme...");

        String destFilePath = this.applicationContext.getWorkingDirectory() + "/forme.yml";

        if (!this.checkOutputLocation(destFilePath))
            return;

        InputStream templateFileStream = Init.class.getClassLoader().getResourceAsStream("templates/forme.yml.mustache");
        (new FileTemplate()).create(new InputStreamReader(templateFileStream), destFilePath, new HashMap<>());
    }

    private boolean checkOutputLocation(String destFilePath) {
        File formeFile = new File(destFilePath);

        if (formeFile.exists() && !cli.hasOption("force")) {
            (new Console()).error("! Already initialized.");
            return false;
        }

        if (formeFile.exists() && cli.hasOption("force"))
            (new Console()).warning("# Already initialized. Continuing anyways.");

        return true;
    }
}
