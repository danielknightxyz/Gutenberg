package us.sourcefoundry.gutenberg.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * An entry from the inventory file.
 */
@Getter
@Setter
@NoArgsConstructor
public class FormeInventoryItem {

    //The github user name from which the forme was downloaded.
    private String username;
    //The github repository from which the forme was downloaded.
    private String repository;
    //The repository reference.
    private String reference;
    //The name of the forme.
    private String name;
    //The location it was installed in the installation directory.
    private String installPath;
    //The tag for the release.
    private String tag;
    //The key for a forme.
    private String key;
    //The date it was addedOn;
    private LocalDateTime addedOn;

}
