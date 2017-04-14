package us.sourcefoundry.gutenberg.models.forme;

import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.IOException;
import java.util.HashMap;

public class CopyEntry {

    private String source;
    private String dest;
    private String type;

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
            (new FileSystemService()).copyDirectory(sourcePath, destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(String sourcePath, String destinationPath) {
        try {
            (new Console()).info("+ Copying File... {0}", destinationPath);
            (new FileSystemService()).copyFile(sourcePath, destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
