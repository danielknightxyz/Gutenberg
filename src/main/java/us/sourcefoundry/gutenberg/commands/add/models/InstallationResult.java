package us.sourcefoundry.gutenberg.commands.add.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;

/**
 * This provides context to what was attempted to be installed; and the result of the installation request.
 */
@Getter
@Setter
@AllArgsConstructor
public class InstallationResult {

    //The scan result being installed.
    private ArchiveScanResult archiveScanResult;
    //The location used to install.
    private ILocationReference locationReference;
    //The new inventory item.
    private FormeInventoryItem formeInventoryItem;
    //Was it installed.
    private boolean installed;

}
