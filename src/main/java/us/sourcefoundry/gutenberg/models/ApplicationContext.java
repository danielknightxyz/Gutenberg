package us.sourcefoundry.gutenberg.models;

import java.util.Map;

public class ApplicationContext {

    private String command;
    private String workingDirectory;
    private String sourceDirectory;
    private String outputDirectory;
    private Map<String, Object> userResponses;

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getWorkingDirectory() {
        return workingDirectory;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public String getSourceDirectory() {
        return sourceDirectory;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public void setOutputDirectory(String outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public Map<String, Object> getUserResponses() {
        return userResponses;
    }

    public void setUserResponses(Map<String, Object> userResponses) {
        this.userResponses = userResponses;
    }
}
