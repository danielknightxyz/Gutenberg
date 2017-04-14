package us.sourcefoundry.gutenberg.models.forme;

import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.Console;

import java.util.HashMap;
import java.util.Map;

/**
 * This represents a form entry to create a file from a Mustache template.
 */
public class FileFormeEntry {

    //The destination file dest.
    private String dest;
    //The source Mustache template.
    private String source;
    //The variables provided in the form file for this entry.
    private Map<String, Object> variables = new HashMap<>();

    /**
     * Gets the dest.
     *
     * @return String
     */
    public String getDest() {
        return dest;
    }

    /**
     * Sets the dest.
     *
     * @param dest String
     */
    public void setDest(String dest) {
        this.dest = dest;
    }

    /**
     * Gets the source.
     *
     * @return String
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source.
     *
     * @param source String
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Gets the entry variables.
     *
     * @return Map
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Sets the variables.
     *
     * @param variables Map
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * This will create the file from a source Mustache template.
     *
     * @param formeLocation   The source directory.
     * @param destinationPath The path to create the file in.
     * @param forme           The forme.  This only included so that it can be used in Mustache templates.
     * @param userResponses   The user response to prompts.
     */
    public void create(String formeLocation, String destinationPath, Forme forme, HashMap<String, Object> userResponses) {

        //Create the variable map.
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", forme);
            put("user", userResponses);
            put("variables", getVariables());
        }};

        //Build the full path to the Mustache template.
        String sourceFilePath =
                (new FormattedStringTemplate("{0}/{1}", formeLocation, this.getSource())).create(variables);
        //Build the full path to the destination.
        String destFilePath =
                (new FormattedStringTemplate("{0}/{1}", destinationPath, this.getDest())).create(variables);

        //Since this is a event, tell the user.
        (new Console()).info("+ Creating File... {0}", destFilePath);
        //Create the file.
        (new FileTemplate()).create(sourceFilePath, destFilePath, variables);
    }
}
