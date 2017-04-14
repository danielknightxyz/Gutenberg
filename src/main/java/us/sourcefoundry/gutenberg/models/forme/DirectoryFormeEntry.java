package us.sourcefoundry.gutenberg.models.forme;

import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.util.HashMap;

/**
 * This class represents a forme entry to create a directory.  This allows the use of Mustache templates.
 */
public class DirectoryFormeEntry {

    //The name of the directory.
    private String name;

    /**
     * Gets the name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name String.
     */
    public void setName(String name) {
        this.name = name;
    }

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
                        "{0}/{1}", destinationPath, this.getName()))
                        .create(variables);

        //Since this is a event, tell the user.
        (new Console()).info("+ Creating Directory... {0}", destination);
        //Create the directory use the file system service.
        (new FileSystemService()).createDirectory(destination);
    }
}
