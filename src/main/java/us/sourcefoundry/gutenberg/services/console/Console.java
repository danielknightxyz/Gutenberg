package us.sourcefoundry.gutenberg.services.console;

import us.sourcefoundry.gutenberg.config.ApplicationProperties;
import us.sourcefoundry.gutenberg.services.console.types.Error;
import us.sourcefoundry.gutenberg.services.console.types.Info;
import us.sourcefoundry.gutenberg.services.console.types.Message;
import us.sourcefoundry.gutenberg.services.console.types.Warning;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class will write to the console different types of user information.
 */
@Singleton
public class Console {

    private ApplicationProperties properties;

    @Inject
    public Console(ApplicationProperties properties){
        this.properties = properties;
    }

    /**
     * Prints a message to the command line for the user.
     *
     * @param pattern The message as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void message(String pattern, Object... args) {
        String formattedMessage = new Message(
                this.properties.getProperty("console.message.color"),
                this.properties.getProperty("console.message.format"),
                pattern, args).format();
        this.print(formattedMessage);
    }

    /**
     * Prints a info message to the command line for the user.
     *
     * @param pattern The message as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void info(String pattern, Object... args) {
        String formattedMessage = new Info(
                this.properties.getProperty("console.info.color"),
                this.properties.getProperty("console.info.format"),
                pattern, args).format();
        this.print(formattedMessage);
    }

    /**
     * Prints a warning to the command line for the user.
     *
     * @param pattern The warning as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void warning(String pattern, Object... args) {
        String formattedMessage = new Warning(
                this.properties.getProperty("console.warning.color"),
                this.properties.getProperty("console.warning.format"),
                pattern, args).format();
        this.print(formattedMessage);
    }

    /**
     * Prints a error to the command line for the user.
     *
     * @param pattern The error as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void error(String pattern, Object... args) {
        String formattedMessage = new Error(
                this.properties.getProperty("console.error.color"),
                this.properties.getProperty("console.error.format"),
                pattern, args).format();
        this.print(formattedMessage);
    }

    /**
     * Prints a informative message to the command line for the user.
     *
     * @param message The message to print.
     */
    private void print(String message) {
        System.out.println(message);
    }
}
