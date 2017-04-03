package us.sourcefoundry.gutenberg.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.UUID;

public class StringGenerator {

    public String create(final String template, HashMap<String, Object> variables) {
        StringWriter writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new StringReader(template), UUID.randomUUID().toString());
        mustache.execute(writer, variables);
        return writer.toString();
    }
}
