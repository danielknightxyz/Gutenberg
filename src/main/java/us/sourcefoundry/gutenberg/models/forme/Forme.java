package us.sourcefoundry.gutenberg.models.forme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents the form file found at the root of every forme template.
 */
public class Forme {

    //Which version of forme is this.
    private int version;
    //The name of the forme.
    private String name;
    //The author of the forme for support requests.
    private String author;
    //The email for support.
    private String email;
    //Any variables to set for use in templating.
    private Map<String, Object> variables = new HashMap<>();
    //Any meta data for use in templating.
    private Map<String, Object> meta = new HashMap<>();
    //Any prompts which will ask the user for information when the forme is used.
    private List<VarPrompt> prompts = new ArrayList<>();
    //Any directories to create.
    private List<DirectoryFormeEntry> directories = new ArrayList<>();
    //Any files to create with Mustache.
    private List<FileFormeEntry> files = new ArrayList<>();
    //Any static content to copy.
    private List<CopyEntry> copy = new ArrayList<>();

    /**
     * Gets the version.
     *
     * @return int
     */
    public int getVersion() {
        return version;
    }

    /**
     * Sets the version.
     *
     * @param version Int
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     * Gets the name.
     *
     * @return String
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name.
     *
     * @param name String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the directories.
     *
     * @return List
     */
    public List<DirectoryFormeEntry> getDirectories() {
        return directories;
    }

    /**
     * Sets the directories.
     *
     * @param directories List
     */
    public void setDirectories(List<DirectoryFormeEntry> directories) {
        this.directories = directories;
    }

    /**
     * Gets the file.
     *
     * @return List
     */
    public List<FileFormeEntry> getFiles() {
        return files;
    }

    /**
     * Sets the files.
     *
     * @param files List
     */
    public void setFiles(List<FileFormeEntry> files) {
        this.files = files;
    }

    /**
     * Gets the copy items.
     *
     * @return List
     */
    public List<CopyEntry> getCopy() {
        return copy;
    }

    /**
     * Sets the copy.
     *
     * @param copy List
     */
    public void setCopy(List<CopyEntry> copy) {
        this.copy = copy;
    }

    /**
     * Gets the author.
     *
     * @return String
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Sets the author.
     *
     * @param author String
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * Gets the email.
     *
     * @return String
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email.
     *
     * @param email String
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets the meta data.
     *
     * @return Map
     */
    public Map<String, Object> getMeta() {
        return meta;
    }

    /**
     * Sets the Meta.
     *
     * @param meta Map
     */
    public void setMeta(Map<String, Object> meta) {
        this.meta = meta;
    }

    /**
     * Gets the variables.
     *
     * @return Map
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Sets the variables.
     *
     * @param variables Map
     */
    public void setVariables(Map<String, Object> variables) {
        this.variables = variables;
    }

    /**
     * Gets the prompts.
     *
     * @return List
     */
    public List<VarPrompt> getPrompts() {
        return prompts;
    }

    /**
     * Sets the prompts.
     *
     * @param prompts List
     */
    public void setPrompts(List<VarPrompt> prompts) {
        this.prompts = prompts;
    }
}
