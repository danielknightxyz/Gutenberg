package us.sourcefoundry.gutenberg;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.ApplicationContextFactory;
import us.sourcefoundry.gutenberg.factories.CliFactory;
import us.sourcefoundry.gutenberg.factories.CommandFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.utils.DependencyInjector;

import java.io.IOException;

/**
 * This is this main class and entry point into the application.
 */
public class Main {

    /**
     * This will run the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) throws IOException {
        //Initialize the dependency injection.
        DependencyInjector.init();

        //Start by getting the CLI args.
        Cli cli = (new CliFactory()).newInstance(args);

        //If we need to show a blocking option, do it.
        if (cli.hasBlockingOption()) {
            cli.printBlockingOption();
            return;
        }

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = (new ApplicationContextFactory()).newInstance(cli);

        //Create the command.
        Command command = (new CommandFactory().newInstance(applicationContext.getCommand()));
        command.execute();
    }
}
