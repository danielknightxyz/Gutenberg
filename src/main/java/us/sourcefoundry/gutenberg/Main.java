package us.sourcefoundry.gutenberg;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.config.ApplicationProperties;
import us.sourcefoundry.gutenberg.factories.ApplicationContextFactory;
import us.sourcefoundry.gutenberg.factories.CliFactory;
import us.sourcefoundry.gutenberg.factories.CommandFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.utils.DependencyInjector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

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

        try {
            InputStream is = Main.class.getClassLoader().getResourceAsStream("application.properties");
            ApplicationProperties props = DependencyInjector.getInstance(ApplicationProperties.class);
            props.load(is);
            is.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        //Create the command.
        Command command = (new CommandFactory()).newInstance(applicationContext.getCommand());
        command.execute();
    }
}
