package us.sourcefoundry.gutenberg.models.forme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * This represents a form entry to create a file from a Mustache template.
 */
@Getter
@Setter
@NoArgsConstructor
public class FileEntry {

    //The destination file dest.
    private String dest;
    //The source Mustache template.
    private String source;
    //The raw Mustache template content.
    private String content;
    //The permissions of the file being created.
    private Permissions permissions = new Permissions();
    //The variables provided in the form file for this entry.
    private Map<String, Object> variables = new HashMap<>();

    /**
     * Is this file entry sourced from another file.
     *
     * @return boolean
     */
    public boolean isFileSourced() {
        return this.getSource() != null;
    }
}
