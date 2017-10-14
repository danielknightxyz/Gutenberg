package us.sourcefoundry.gutenberg.models.forme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.util.HashMap;

/**
 * This class represents a forme entry to create a directory.  This allows the use of Mustache templates.
 */
@Getter
@Setter
@NoArgsConstructor
public class DirectoryEntry {

    //The dest of the directory.
    private String dest;

    /**
     * This will create the directory.
     *
     * @param destinationPath The path to create the directory in.
     * @param forme           The forme.  This only included so that it can be used in Mustache templates.
     * @param userResponses   The user response to prompts.
     */
    public void create(String destinationPath, Forme forme, HashMap<String, Object> userResponses) {

        //Create the variable map.
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", forme);
            put("user", userResponses);
        }};

        //Build the expected destination the directory.
        String destination =
                (new FormattedStringTemplate(
                        "{0}/{1}", destinationPath, this.getDest()))
                        .create(variables);

        //Since this is a event, tell the user.
        (new Console()).info("\t+ Creating Directory... {0}", destination);
        //Create the directory use the file system service.
        (new FileSystemService()).createDirectory(destination);
    }
}
