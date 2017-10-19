package us.sourcefoundry.gutenberg.models.forme;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class represents an instruction to copy either a file or directory(recursively) into the build path.
 */
@Getter
@Setter
@NoArgsConstructor
public class CopyEntry {

    //The source file or directory.
    private String source;
    //The expected destination in the build path.
    private String dest;
    //The type of entry it is: file or directory.
    private String type;
    //The permissions of the file or directory being created.
    private Permissions permissions = new Permissions();

}
