package us.sourcefoundry.gutenberg.services.console.types;

public class Info extends Message implements IConsoleOut {

    /**
     * A new console message.
     *
     * @param formatColor   The color of the message.
     * @param messageFormat The overall format the message.
     * @param pattern       The message pattern.
     * @param args          Argument to replace in the pattern.
     */
    public Info(String formatColor, String messageFormat, String pattern, Object... args) {
        super(formatColor, messageFormat, pattern, args);
    }
}
