package us.sourcefoundry.gutenberg.models;

import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.models.templates.SystemPathTemplate;
import us.sourcefoundry.gutenberg.services.Console;

import java.util.HashMap;
import java.util.Map;

public class FileTemplateEntry {

    private String name;
    private String source;
    private Map<String, Object> variables = new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public void create(ApplicationContext applicationContext, FormeContext formeContext) {
        String sourceFilePath =
                (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), this.getSource());
        String destFilePath =
                (new SystemPathTemplate(applicationContext, formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), this.getName());

        (new Console()).info("+ Creating File... {0}", destFilePath);

        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", formeContext);
            put("variables", getVariables());
            put("user", applicationContext.getUserResponses());
        }};

        (new FileTemplate()).create(sourceFilePath, destFilePath, variables);
    }
}
