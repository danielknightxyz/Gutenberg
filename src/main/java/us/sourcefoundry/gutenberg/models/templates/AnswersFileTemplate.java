package us.sourcefoundry.gutenberg.models.templates;

import us.sourcefoundry.gutenberg.models.forme.Permissions;
import us.sourcefoundry.gutenberg.utils.Pair;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

public class AnswersFileTemplate extends FileTemplate {

    final private String TEMPLATE = "---\n{{#answers}}{{key}}: {{value}}\n{{/answers}}";

    public void create(String answersFilePath, List<Pair<Object, Object>> answers) throws FileNotFoundException {
        this.create(
                new StringReader(TEMPLATE),
                new Permissions(),
                answersFilePath,
                new HashMap<String, Object>() {{
                    put("answers", answers);
                }}
        );
    }
}
