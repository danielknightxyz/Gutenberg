package us.sourcefoundry.gutenberg.models.forme;

import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.models.templates.StringTemplate;
import us.sourcefoundry.gutenberg.services.Console;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

/**
 * This represents a form entry to create a file from a Mustache template.
 */
public class FileEntry {

    //The destination file dest.
    private String dest;
    //The source Mustache template.
    private String source;
    //The raw Mustache template content.
    private String content;
    //The permissions of the file being created.
    private Permissions permissions = new Permissions();
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
     * Gets the content.
     *
     * @return String
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content.
     *
     * @param content String
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets Permissions
     *
     * @return Permissions
     */
    public Permissions getPermissions() {
        return permissions;
    }

    /**
     * Sets Permissions
     *
     * @param permissions Permissions
     */
    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
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
     * Is this file entry sourced from another file.
     *
     * @return boolean
     */
    public boolean isFileSourced() {
        return this.getSource() != null;
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

        //Build the full path to the destination.
        String destFilePath =
                (new FormattedStringTemplate("{0}/{1}", destinationPath, this.getDest())).create(variables);

        //Create the file from a source file template.
        if (this.isFileSourced()) {
            this.createFromSourceFile(formeLocation, destFilePath, variables);
            return;
        }
        //Create the file from the content provided in the forme file.
        this.createFromSourceContent(destFilePath, variables);
    }

    /**
     * Creates a new file from a source file template.
     *
     * @param formeLocation The location of the forme file used for determining the location of the form source file.
     * @param destFilePath  The location to build the file to.
     * @param variables     Any variables to use for templating.
     */
    private void createFromSourceFile(String formeLocation, String destFilePath, Map<String, Object> variables) {
        //Build the full path to the Mustache template.
        String sourceFilePath =
                (new FormattedStringTemplate("{0}/{1}", formeLocation, this.getSource())).create(variables);

        //Since this is a event, tell the user.
        (new Console()).info("+ Creating File... {0}", destFilePath);
        //Create the file.
        (new FileTemplate()).create(sourceFilePath, destFilePath, this.permissions, variables);
    }

    /**
     * Creates a new file from the content of the file entry in the forme file.
     *
     * @param destFilePath The location to build the file to.
     * @param variables    Any variables to use for templating.
     */
    private void createFromSourceContent(String destFilePath, Map<String, Object> variables) {
        //Build the string template from the specified content.
        StringReader reader = new StringReader((new StringTemplate(this.getContent())).create(variables));

        //Since this is a event, tell the user.
        (new Console()).info("+ Creating File... {0}", destFilePath);
        //Create the file.
        (new FileTemplate()).create(reader, this.permissions, destFilePath, variables);
    }
}
