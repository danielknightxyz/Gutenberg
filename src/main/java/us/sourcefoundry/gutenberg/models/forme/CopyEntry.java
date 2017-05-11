package us.sourcefoundry.gutenberg.models.forme;

import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.IOException;
import java.util.HashMap;

/**
 * This class represents an instruction to copy either a file or directory(recursively) into the build path.
 */
public class CopyEntry {

    //The source file or directory.
    private String source;
    //The expected destination in the build path.
    private String dest;
    //The type of entry it is: file or directory.
    private String type;
    //The permissions of the file or directory being created.
    private Permissions permissions = new Permissions();

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
     * Gets the destination.
     *
     * @return String
     */
    public String getDest() {
        return dest;
    }

    /**
     * s
     * Sets the destination.
     *
     * @param dest String
     */
    public void setDest(String dest) {
        this.dest = dest;
    }

    /**
     * Gets the type.
     *
     * @return String
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the type.
     *
     * @param type String
     */
    public void setType(String type) {
        this.type = type;
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
     * Executes the appropriate copy of this resource.
     *
     * @param formeLocation The location of the forme.
     * @param buildPath     The location to build the files or directories.
     * @param forme         The forme.
     * @param userResponses The user responses to prompts.
     */
    public void copy(String formeLocation, String buildPath, Forme forme, HashMap<String, Object> userResponses) {
        //Prepare the variables map.
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", forme);
            put("user", userResponses);
        }};

        //Generate the paths to the for the source and destination.
        String sourcePath = (new FormattedStringTemplate("{0}/{1}", formeLocation, this.getSource())).create(variables);
        String destinationPath = (new FormattedStringTemplate("{0}/{1}", buildPath, this.getDest())).create(variables);

        //If this is type directory.
        if (this.getType().equals("directory")) {
            this.copyDir(sourcePath, destinationPath);
            return;
        }

        //If this is a file that needs to be copied.
        this.copyFile(sourcePath, destinationPath);
    }

    /**
     * Recursively copies a directory.
     *
     * @param sourcePath      The location of the source directory.
     * @param destinationPath The location to copy the directory.
     */
    private void copyDir(String sourcePath, String destinationPath) {
        try {
            (new Console()).info("\t+ Copying Directory... {0}", destinationPath);
            (new FileSystemService()).copyDirectory(sourcePath, destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a file.
     *
     * @param sourcePath      The location of the source file.
     * @param destinationPath The location to copy the file.
     */
    private void copyFile(String sourcePath, String destinationPath) {
        try {
            (new Console()).info("\t+ Copying File... {0}", destinationPath);
            (new FileSystemService()).copyFile(sourcePath, destinationPath, this.permissions.canRead(), this.permissions.canWrite(), this.permissions.canExecute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
