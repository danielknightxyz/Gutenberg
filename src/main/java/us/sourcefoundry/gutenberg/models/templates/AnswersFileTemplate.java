package us.sourcefoundry.gutenberg.models.templates;

import us.sourcefoundry.gutenberg.models.forme.Permissions;
import us.sourcefoundry.gutenberg.utils.Pair;

import java.io.FileNotFoundException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

/**
 * This is the template writer for an answers file.
 */
public class AnswersFileTemplate extends FileTemplate {

    //The template string.
    final private String TEMPLATE = "---\n{{#answers}}{{key}}: {{value}}\n{{/answers}}";

    /**
     * Creates a file with the answers template.
     *
     * @param answersFilePath The location to save the answer file.
     * @param answers         The user's answers to any prompts.
     */
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
