package us.sourcefoundry.gutenberg.models.forme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.sourcefoundry.gutenberg.models.FormeLocation;
import us.sourcefoundry.gutenberg.factories.FormeFactory;
import us.sourcefoundry.gutenberg.services.console.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.File;
import java.io.FileNotFoundException;
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
    //The release tag for the forme.
    private String tag;
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

    /**
     * This will find the forme file given a location on the file system.
     *
     * @param formeLocation The system path to the file.
     * @return Forme
     */
    public static Forme fromLocation(FormeLocation formeLocation, Console console) throws FileNotFoundException {
        //Get the forme file and make sure it exists.
        File formeFile = (new FileSystemService()).getByLocation("{0}/forme.yml", formeLocation.getPath());
        if (!formeFile.exists()) {
            console.error("Could not locate a forme file in source location.  Does it needs to be initialized?");
            return null;
        }

        //Parse it into a forme object.
        return (new FormeFactory()).newInstance(
                (new FileSystemService()).streamFile(formeFile)
        );
    }
}
