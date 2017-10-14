package us.sourcefoundry.gutenberg.models.templates;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.UUID;

/**
 * This will process a string as a template.
 */
public class StringTemplate {

    //The string template.
    private String template;

    /**
     * Constructor
     *
     * @param template The template string.
     */
    public StringTemplate(final String template) {
        this.template = template;
    }

    /**
     * Create a new string using a string and variables as variables.
     *
     * @param variables The variables for the template.
     * @return String
     */
    public String create(Map<String, Object> variables) {
        StringWriter writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(this.template), UUID.randomUUID().toString());
        mustache.execute(writer, variables);
        return writer.toString();
    }
}
