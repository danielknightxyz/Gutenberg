package us.sourcefoundry.gutenberg.commands.add;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.sourcefoundry.gutenberg.models.forme.Forme;

/**
 * This represents the a located forme file in a tar archive.  This used when adding forme(s) from an arvhive downloaded
 * from the Github archive.
 */
@Getter
@Setter
@NoArgsConstructor
public class ArchiveScanResult {

    //The path in the archive the forme file was located.
    private String archivePath;
    //The forme from the archive.  This is the actual forme which was located.
    private Forme forme;
    //The level the forme file is located within.
    private int depth = 1;

}
