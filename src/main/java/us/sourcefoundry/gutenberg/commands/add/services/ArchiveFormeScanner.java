package us.sourcefoundry.gutenberg.commands.add.services;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import us.sourcefoundry.gutenberg.commands.add.models.ArchiveScanResult;
import us.sourcefoundry.gutenberg.commands.add.models.ILocationReference;
import us.sourcefoundry.gutenberg.commands.add.models.ScanResultAction;
import us.sourcefoundry.gutenberg.factories.FormeFactory;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.models.forme.Forme;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

/**
 * This will scan an archive for any forme files. It will return a list of archive results so that they can be extracted.
 */
public class ArchiveFormeScanner {

    /**
     * Scan the archive.
     *
     * @param urlStream         The input stream for the file.
     * @param locationReference The location.
     * @param inventory         The current inventroy.  This is used to determine the action.
     * @return List
     */
    public List<ArchiveScanResult> scan(InputStream urlStream, ILocationReference locationReference, Map<String, FormeInventoryItem> inventory) throws IOException {
        //Create a GZIP stream.
        GZIPInputStream input = new GZIPInputStream(urlStream);
        //Convert it to a TAR stream.
        TarArchiveInputStream tar = new TarArchiveInputStream(input);

        //Prepare the results list.
        List<ArchiveScanResult> archiveScanResults = new ArrayList<>();

        //Preset the current directory to root.
        String currentArchiveDirectory = "";

        //Get the first entry.
        ArchiveEntry entry = tar.getNextEntry();

        //Keep going until we run out of entries.
        while (entry != null) {
            //If its a directory, then we need to track that we are now in a new directory.  This is important for tracking
            //the location of any formes that are found.
            if (entry.isDirectory())
                currentArchiveDirectory = entry.getName();
                //If the entry is a form, then we need to create a scan result, since this will need to be extracted when
                //installed.
            else if (FilenameUtils.getName(entry.getName().toLowerCase()).equals("forme.yml")) {
                //Get the forme file as a forme object.
                Forme forme = (new FormeFactory()).newInstance(tar);
                //Determine how this result needs to be handled during installation.
                ScanResultAction action = this.determineResultAction(locationReference, inventory, forme);
                //This allows the installation process know how deep into the archive the forme was found.  This is used
                //so that the path can be preserved when installed.
                int depth = Arrays.asList(FilenameUtils.getPath(entry.getName()).split("/")).size();

                //Create a result.
                ArchiveScanResult result = new ArchiveScanResult();
                result.setArchivePath(currentArchiveDirectory);
                result.setForme(forme);
                result.setDepth(depth);
                result.setAction(action);

                //Add it to the results.
                archiveScanResults.add(result);
            }

            entry = tar.getNextEntry();
        }

        //Reset the file so that it can be extracted.
        urlStream.reset();
        return archiveScanResults;
    }

    /**
     * This determines the action needed to add a forme.
     *
     * @param locationReference The location details.
     * @param inventory         The current inventory.
     * @param forme             The forme found in the source.
     * @return ScanResultAction
     */
    private ScanResultAction determineResultAction(ILocationReference locationReference, Map<String, FormeInventoryItem> inventory, Forme forme) {
        //Get the forme name.
        String formeName = forme.getName();
        //Build the key.
        String key = locationReference.getUser() + "/" + formeName;

        //Check to see if the forme is in the existing inventory with the extended key/name.
        if (inventory.containsKey(key))
            return this.determineActionForPreexistingForme(locationReference.getUser(), inventory.get(key), forme);
            //Check to see if the forme is in the existing inventory with just the name.
        else if (inventory.containsKey(formeName))
            return this.determineActionForPreexistingForme(locationReference.getUser(), inventory.get(formeName), forme);

        //If both checks failed, then this is a new item and needs to be install.
        return ScanResultAction.INSTALL;
    }

    /**
     * Determine a how a pre-existing forme should be handled.
     *
     * @param locationUser  The location's user.
     * @param inventoryItem The current inventory item installed.
     * @param forme         The forme.
     * @return ScanResultAction
     */
    private ScanResultAction determineActionForPreexistingForme(String locationUser, FormeInventoryItem inventoryItem, Forme forme) {
        //If its the same user, then continue to check.
        if (inventoryItem.getUsername().equals(locationUser))
            //If its also the same tag, then don't add.
            if (inventoryItem.getTag() != null && inventoryItem.getTag().equals(forme.getTag()))
                return ScanResultAction.ALREADY_INSTALLED;
            else
                return ScanResultAction.REPLACE;

        //The forme exists but is from a different user.
        return ScanResultAction.INSTALL_WITH_PREEXISTING;
    }
}
