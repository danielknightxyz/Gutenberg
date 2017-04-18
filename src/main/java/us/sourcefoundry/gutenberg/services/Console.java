package us.sourcefoundry.gutenberg.services;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * This class will write to the console different types of user information.
 */
public class Console {

    //The colors for the different types of information.
    final private HashMap<String, String> colorCodes = new HashMap<String, String>() {{
        put("message", "32m");
        put("info", "36m");
        put("warning", "33m");
        put("error", "31m");
    }};

    /**
     * Prints a message to the command line for the user.
     *
     * @param pattern The message as a pattern.
     * @param args    The arguments to inject into the message pattern.
     */
    public void message(String pattern, Object... args) {
        this.print(
                this.colorCodes.get("message"), pattern, args
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
                this.colorCodes.get("info"), pattern, args
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
                this.colorCodes.get("warning"), pattern, args
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
                this.colorCodes.get("error"), pattern, args
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
        String preparedMessage = MessageFormat.format(pattern, args);
        String finalMessage = MessageFormat.format("\033[{0}{1}\033[0m", colorCode, preparedMessage);
        System.out.println(finalMessage);
    }
}
