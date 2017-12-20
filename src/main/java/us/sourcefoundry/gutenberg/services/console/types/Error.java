package us.sourcefoundry.gutenberg.services.console.types;

public class Error extends Message implements IConsoleOut {

    /**
     * A new console message.
     *
     * @param formatColor   The color of the message.
     * @param messageFormat The overall format the message.
     * @param pattern       The message pattern.
     * @param args          Arguments to replace in the pattern.
     */
    public Error(String formatColor, String messageFormat, String pattern, Object... args) {
        super(formatColor, messageFormat, pattern, args);
    }
}
