package us.sourcefoundry.gutenberg.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormeContext {

    private int version;
    private String name;
    private String author;
    private String email;
    private Map<String, Object> variables = new HashMap<>();
    private Map<String, Object> meta = new HashMap<>();
    private List<VarPrompt> prompts = new ArrayList<>();
    private List<DirectoryTemplateEntry> directories = new ArrayList<>();
    private List<FileTemplateEntry> files = new ArrayList<>();
    private List<CopyEntry> copy = new ArrayList<>();

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DirectoryTemplateEntry> getDirectories() {
        return directories;
    }

    public void setDirectories(List<DirectoryTemplateEntry> directories) {
        this.directories = directories;
    }

    public List<FileTemplateEntry> getFiles() {
        return files;
    }

    public void setFiles(List<FileTemplateEntry> files) {
        this.files = files;
    }

    public List<CopyEntry> getCopy() {
        return copy;
    }

    public void setCopy(List<CopyEntry> copy) {
        this.copy = copy;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<String, Object> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    public Map<String, Object> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    public List<VarPrompt> getPrompts() {
        return prompts;
    }

    public void setPrompts(List<VarPrompt> prompts) {
        this.prompts = prompts;
    }
}
