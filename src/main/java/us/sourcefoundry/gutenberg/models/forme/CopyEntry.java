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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDest() {
        return dest;
    }

    public void setDest(String dest) {
        this.dest = dest;
    }

    public String getType() {
        return type;
    }

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

    public void copy(String formeLocation, String buildPath, Forme forme, HashMap<String, Object> userResponses) {
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", forme);
            put("user", userResponses);
        }};

        String sourcePath = (new FormattedStringTemplate("{0}/{1}", formeLocation, this.getSource())).create(variables);
        String destinationPath = (new FormattedStringTemplate("{0}/{1}", buildPath, this.getDest())).create(variables);

        if (this.getType().equals("directory")) {
            this.copyDir(sourcePath, destinationPath);
            return;
        }

        this.copyFile(sourcePath, destinationPath);
    }

    private void copyDir(String sourcePath, String destinationPath) {
        try {
            (new Console()).info("+ Copying Directory... {0}", destinationPath);
            (new FileSystemService()).copyDirectory(sourcePath, destinationPath,this.permissions.canRead(),this.permissions.canWrite(),this.permissions.canExecute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(String sourcePath, String destinationPath) {
        try {
            (new Console()).info("+ Copying File... {0}", destinationPath);
            (new FileSystemService()).copyFile(sourcePath, destinationPath,this.permissions.canRead(),this.permissions.canWrite(),this.permissions.canExecute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
