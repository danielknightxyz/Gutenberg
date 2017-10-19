package us.sourcefoundry.gutenberg.services;

import us.sourcefoundry.gutenberg.config.ApplicationProperties;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.MessageFormat;

/**
 * This class will write to the console different types of user information.
 */
@Singleton
public class Console {

    @Inject
    private ApplicationProperties properties = new ApplicationProperties();

    /**
     * Prints a message to the command line for the user.
     *
     * @param pattern The message as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void message(String pattern, Object... args) {
        this.print(
                this.properties.getProperty("console.message.color"), pattern, args
        );
    }

    /**
     * Prints a info message to the command line for the user.
     *
     * @param pattern The message as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void info(String pattern, Object... args) {
        this.print(
                this.properties.getProperty("console.info.color"), pattern, args
        );
    }

    /**
     * Prints a warning to the command line for the user.
     *
     * @param pattern The warning as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void warning(String pattern, Object... args) {
        this.print(
                this.properties.getProperty("console.warning.color"), pattern, args
        );
    }

    /**
     * Prints a error to the command line for the user.
     *
     * @param pattern The error as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void error(String pattern, Object... args) {
        this.print(
                this.properties.getProperty("console.error.color"), pattern, args
        );
    }

    /**
     * Prints a informative message to the command line for the user.
     *
     * @param colorCode The color to use for the message.
     * @param pattern   The message as a pattern.
     * @param args      The arguments to inject into the message pattern.
     */
    private void print(String colorCode, String pattern, Object... args) {
        if (colorCode == null)
            colorCode = "00m";
        String preparedMessage = MessageFormat.format(pattern, args);
        String finalMessage = MessageFormat.format("\033[{0}{1}\033[0m", colorCode, preparedMessage);
        System.out.println(finalMessage);
    }
}
