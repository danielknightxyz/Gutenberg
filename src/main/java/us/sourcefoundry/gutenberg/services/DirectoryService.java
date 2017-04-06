package us.sourcefoundry.gutenberg.services;

import org.apache.commons.io.FileUtils;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.DirectoryTemplateEntry;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.utils.SystemPathTemplate;

import java.io.File;
import java.io.IOException;

public class DirectoryService {

    private ApplicationContext applicationContext;
    private FormeContext formeContext;

    public DirectoryService(ApplicationContext applicationContext, FormeContext formeContext) {
        this.applicationContext = applicationContext;
        this.formeContext = formeContext;
    }

    public boolean createFromTemplate(DirectoryTemplateEntry directoryTemplateEntry) {
        return this.create(
                (new SystemPathTemplate(this.applicationContext, this.formeContext)).create("{0}/{1}", this.applicationContext.getOutputDirectory(), directoryTemplateEntry.getName())
        );
    }

    public boolean create(String directoryPath) {
        (new Console()).info("+ Creating Directory... {0}",directoryPath);
        return (new File(directoryPath)).mkdirs();
    }

    public void delete(String directoryPath) {
        try {
            FileUtils.deleteDirectory(new File(directoryPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
