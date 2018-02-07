package us.sourcefoundry.gutenberg;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.ApplicationContextFactory;
import us.sourcefoundry.gutenberg.factories.ApplicationPropertiesFactory;
import us.sourcefoundry.gutenberg.factories.CliFactory;
import us.sourcefoundry.gutenberg.factories.CommandFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.CliService;
import us.sourcefoundry.gutenberg.utils.DependencyInjector;

/**
 * This is this main class and entry point into the application.
 */
public class Main {

    /**
     * This will run the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        //Initialize the dependency injection.
        DependencyInjector.init();

        //Start by getting the CLI args.
        CliService cliService = (new CliFactory()).newInstance(args);

        //Check to see if the version was requested.
        if (cliService.hasVersion()) {
            cliService.printVersion();
            return;
        }

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = (new ApplicationContextFactory()).newInstance(cliService);
        //Get the application properties.
        (new ApplicationPropertiesFactory()).newInstance("application.properties");

        //Create the command.
        Command command = (new CommandFactory()).newInstance(applicationContext.getCommand());

        //If the help as been requested, then print the command help.
        if (command.hasHelp()) {
            command.help();
            return;
        }

        //Execute the command.
        command.execute();
    }
}
