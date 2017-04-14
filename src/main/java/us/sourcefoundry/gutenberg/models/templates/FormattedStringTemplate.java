package us.sourcefoundry.gutenberg.models.templates;

import java.text.MessageFormat;

/**
 * This will generate a formatted string taking into template variables.  This class, essentially, is a mash up of message
 * formatting with Mustache template framework.
 */
public class FormattedStringTemplate extends StringTemplate {

    /**
     * Constructor with Arguments.
     *
     * @param template  The template as a pre-formatted string.
     * @param arguments The replacement arguments.
     */
    public FormattedStringTemplate(final String template, Object... arguments) {
        super(MessageFormat.format(template, arguments));
    }
}
