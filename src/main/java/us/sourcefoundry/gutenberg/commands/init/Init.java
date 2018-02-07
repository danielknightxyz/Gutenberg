package us.sourcefoundry.gutenberg.commands.init;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.forme.Permissions;
import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.services.CliService;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;
import us.sourcefoundry.gutenberg.services.console.Console;

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
    private CliCommand cli;
    //The console.
    private Console console;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cliService         The cliService service.
     * @param console            The console service.
     */
    @Inject
    public Init(ApplicationContext applicationContext, CliService cliService, Console console) {
        this.applicationContext = applicationContext;
        this.cli = cliService.getRootCommand().getSubCommand();
        this.console = console;
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
        this.console.message("Creating Forme...");

        //Create the file using the template in the resource directory.
        InputStream templateFileStream = Init.class.getClassLoader().getResourceAsStream("templates/forme.yml.mustache");
        //Create the file in the output path.
        (new FileTemplate()).create(new InputStreamReader(templateFileStream), new Permissions(), destFilePath, new HashMap<>());
    }

    /**
     * Prints the help for the command.
     */
    @Override
    public void help() {
        System.out.println("init usage: gutenberg init [-h] [-f] \n");

        System.out.println("Options:");

        for (Option option : this.cli.getReference().getOptions()) {
            String shortOption = (option.getName() != null ? "-" + option.getName() + "," : "");
            String longOption = (option.getLongName() != null ? "--" + option.getLongName() : "");
            String description = option.getDescription();

            System.out.format("%-1s %-13s %-60s %n", shortOption, longOption, (description != null ? description : ""));
        }

    }

    /**
     * Is the help been requested.
     *
     * @return boolean
     */
    @Override
    public boolean hasHelp() {
        return this.cli.hasOption("h");
    }

    /**
     * This will check to make sure the output location as not already been initialized.
     *
     * @param destFilePath The expected location to be initialized.
     * @return boolean
     */
    private boolean checkOutputLocation(String destFilePath, boolean force) {
        File formeFile = new File(destFilePath);

        //If the forme exists, and it the force option is not set, then don't do anything.
        if (formeFile.exists() && !force) {
            this.console.error("Already initialized.");
            return false;
        }

        //If it exists and the force option is set, then initialize over the existing forme.
        if (formeFile.exists() && force)
            this.console.warning("Already initialized. Continuing anyways;  this is going to overwrite the existing forme.");

        return true;
    }
}
