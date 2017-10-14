package us.sourcefoundry.gutenberg.models.forme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This represents the form file found at the root of every forme template.
 */
@Getter
@Setter
@NoArgsConstructor
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
    //Should the answers be auto-saved?
    private boolean autoSaveAnswers = false;
    //Any directories to create.
    private List<DirectoryEntry> directories = new ArrayList<>();
    //Any files to create with Mustache.
    private List<FileEntry> files = new ArrayList<>();
    //Any static content to copy.
    private List<CopyEntry> copy = new ArrayList<>();


    /**
     * Should the answers be auto-saved.
     *
     * @return boolean
     */
    public boolean shouldAutoSaveAnswers() {
        return autoSaveAnswers;
    }
}
