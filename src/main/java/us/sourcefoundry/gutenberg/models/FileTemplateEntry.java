package us.sourcefoundry.gutenberg.models;

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
}
