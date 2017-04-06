package us.sourcefoundry.gutenberg.services;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FileTemplateEntry;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.utils.SystemPathTemplate;

import java.util.HashMap;

public class FileService {

    private ApplicationContext applicationContext;
    private FormeContext formeContext;

    public FileService(ApplicationContext applicationContext, FormeContext formeContext) {
        this.applicationContext = applicationContext;
        this.formeContext = formeContext;
    }

    public void createFromTemplate(FileTemplateEntry fileTemplateEntry) {
        String sourceFilePath =
                (new SystemPathTemplate(this.applicationContext, this.formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), fileTemplateEntry.getSource());
        String destFilePath =
                (new SystemPathTemplate(this.applicationContext, this.formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), fileTemplateEntry.getName());

        (new Console()).info("+ Creating File from Template... {0}",destFilePath);

        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", formeContext);
            put("variables", fileTemplateEntry.getVariables());
            put("user", applicationContext.getUserResponses());
        }};

        (new FileTemplate()).create(sourceFilePath,destFilePath,variables);
    }
}
