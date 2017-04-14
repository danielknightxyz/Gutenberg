package us.sourcefoundry.gutenberg.models.templates;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashMap;

public class AnswersFileTemplate extends FileTemplate {

    final private String TEMPLATE = "---\n{{#answers}}{{key}}: {{value}}\n{{/answers}}";

    public void create(String answersFilePath, HashMap<String, Object> answers) throws FileNotFoundException {
        this.create(
                new StringReader(TEMPLATE),
                answersFilePath,
                new HashMap<String, Object>() {{
                    put("answers", answers);
                }}
        );
    }
}
