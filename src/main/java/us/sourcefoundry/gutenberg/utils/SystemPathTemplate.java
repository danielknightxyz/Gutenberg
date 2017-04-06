package us.sourcefoundry.gutenberg.utils;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.models.templates.StringTemplate;

import java.text.MessageFormat;
import java.util.HashMap;

/**
 * This will generate a system path taking into template variables.  This class, essentially, is a mash up of message
 * formatting with Mustache template framework.
 */
public class SystemPathTemplate extends StringTemplate {

    private ApplicationContext applicationContext;
    private FormeContext formeContext;

    public SystemPathTemplate(ApplicationContext applicationContext, FormeContext formeContext) {
        this.applicationContext = applicationContext;
        this.formeContext = formeContext;
    }

    /**
     * Create a system path using message formatting and mustache templating.
     *
     * @param templateString The system path template.
     * @param arguments      The replacement arguments.
     * @return String
     */
    public String create(final String templateString, Object... arguments) {
        return this.create(
                MessageFormat.format(templateString, arguments),
                new HashMap<String, Object>() {{
                    put("forme", formeContext);
                    put("user", applicationContext.getUserResponses());
                }}
        );
    }
}
