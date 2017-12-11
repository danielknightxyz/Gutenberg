package us.sourcefoundry.gutenberg.services.console.types;

import lombok.Getter;
import lombok.Setter;

import java.text.MessageFormat;

@Getter
@Setter
public class Message implements IConsoleOut {

    private String pattern;
    private Object[] args;
    private String formatColor;
    private String messageFormat;

    /**
     * A new console message.
     *
     * @param formatColor   The color of the message.
     * @param messageFormat The overall format the message.
     * @param pattern       The message pattern.
     * @param args          Arguments to replace in the pattern.
     */
    public Message(String formatColor, String messageFormat, String pattern, Object... args) {
        this.formatColor = formatColor;
        this.messageFormat = messageFormat;
        this.pattern = pattern;
        this.args = args;
    }

    /**
     * Format the message.
     *
     * @return String
     */
    public String format() {
        return this.format(
                this.formatColor,
                this.messageFormat,
                this.pattern, args
        );
    }

    /**
     * Prints a informative message to the command line for the user.
     *
     * @param colorCode The color to use for the message.
     * @param pattern   The message as a pattern.
     * @param args      The arguments to inject into the message pattern.
     * @return String
     */
    private String format(String colorCode, String finalPattern, String pattern, Object... args) {
        if (colorCode == null)
            colorCode = "00m";
        String preparedMessage = MessageFormat.format(pattern, args);
        String finalMessage = MessageFormat.format(finalPattern, preparedMessage);
        return MessageFormat.format("\033[{0}{1}\033[0m", colorCode, finalMessage);
    }
}
