package us.sourcefoundry.gutenberg.commands;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.Console;

import javax.inject.Inject;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 * This will create a new forme.yml file which is ready to be populated with instructions.
 */
public class Init implements Command {

    //The application context.
    private ApplicationContext applicationContext;
    //The command line.
    private Cli cli;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cli                The cli service.
     */
    @Inject
    public Init(ApplicationContext applicationContext, Cli cli) {
        this.applicationContext = applicationContext;
        this.cli = cli;
    }

    /**
     * This will run the init action.
     */
    @Override
    public void execute() {
        //Get the destination file path.
        String destFilePath = this.applicationContext.getWorkingDirectory() + "/forme.yml";

        //Check that the expected output location is ready and available.
        if (!this.checkOutputLocation(destFilePath, this.cli.hasOption("f")))
            return;

        //Tell the user what we are doing.
        (new Console()).message("Initializing Forme...");

        //Create the file using the template in the resource directory.
        InputStream templateFileStream = Init.class.getClassLoader().getResourceAsStream("templates/forme.yml.mustache");
        //Create the file in the output path.
        (new FileTemplate()).create(new InputStreamReader(templateFileStream), destFilePath, new HashMap<>());
    }

    /**
     * This will check to make sure the output location as not already been initialized.
     *
     * @param destFilePath The expected location to be initialized.
     * @return boolean
     */
    private boolean checkOutputLocation(String destFilePath, boolean force) {
        File formeFile = new File(destFilePath);

        if (formeFile.exists() && !force) {
            (new Console()).error("! Already initialized.");
            return false;
        }

        if (formeFile.exists() && force)
            (new Console()).warning("# Already initialized. Continuing anyways.");

        return true;
    }
}
