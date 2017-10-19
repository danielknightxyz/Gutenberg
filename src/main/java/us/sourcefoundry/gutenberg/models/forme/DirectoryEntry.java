package us.sourcefoundry.gutenberg.models.forme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents a forme entry to create a directory.  This allows the use of Mustache templates.
 */
@Getter
@Setter
@NoArgsConstructor
public class DirectoryEntry {

    //The dest of the directory.
    private String dest;
}
