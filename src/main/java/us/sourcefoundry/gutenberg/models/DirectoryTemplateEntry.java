package us.sourcefoundry.gutenberg.models;

import us.sourcefoundry.gutenberg.models.templates.SystemPathTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

public class DirectoryTemplateEntry {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void create(ApplicationContext applicationContext, FormeContext formeContext){
        String location = (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), this.getName());
        (new Console()).info("+ Creating Directory... {0}", location);
        (new FileSystemService()).createDirectory(location);
    }
}
