package us.sourcefoundry.gutenberg.models.templates;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.UUID;

/**
 * This will process a string as a template.
 */
public class StringTemplate {

    /**
     * Create a new string using a string and variables as variables.
     *
     * @param template  The string template.
     * @param variables The variables for the template.
     * @return String
     */
    public String create(final String template, HashMap<String, Object> variables) {
        StringWriter writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(template), UUID.randomUUID().toString());
        mustache.execute(writer, variables);
        return writer.toString();
    }
}
