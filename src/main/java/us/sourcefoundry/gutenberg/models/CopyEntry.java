package us.sourcefoundry.gutenberg.models;

import us.sourcefoundry.gutenberg.models.templates.SystemPathTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.IOException;

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

    public void copy(FormeContext formeContext, ApplicationContext applicationContext) {
        if (this.getType().equals("directory")) {
            this.copyDir(formeContext, applicationContext);
            return;
        }
        this.copyFile(formeContext, applicationContext);
    }

    private void copyDir(FormeContext formeContext, ApplicationContext applicationContext) {
        try {
            String sourcePath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), this.getSource());
            String destinationPath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), this.getDest());

            (new Console()).info("+ Copying Directory... {0}", destinationPath);
            (new FileSystemService()).copyDirectory(sourcePath,destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFile(FormeContext formeContext, ApplicationContext applicationContext) {
        try {
            String sourcePath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), this.getSource());
            String destinationPath = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), this.getDest());

            (new Console()).info("+ Copying File... {0}", destinationPath);
            (new FileSystemService()).copyFile(sourcePath,destinationPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
