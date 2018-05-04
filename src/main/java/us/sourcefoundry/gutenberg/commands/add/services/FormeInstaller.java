package us.sourcefoundry.gutenberg.commands.add.services;

import com.google.common.io.ByteStreams;
import lombok.AllArgsConstructor;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import us.sourcefoundry.gutenberg.commands.add.models.ArchiveScanResult;
import us.sourcefoundry.gutenberg.commands.add.models.ILocationReference;
import us.sourcefoundry.gutenberg.commands.add.models.InstallationResult;
import us.sourcefoundry.gutenberg.commands.add.models.ScanResultAction;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.console.Console;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPInputStream;

@AllArgsConstructor
public class FormeInstaller {

    private Console console;

    /**
     * Installs formes from an archive file stream.
     *
     * @param archiveFileStream  The stream for the file.
     * @param destination        The install destination.
     * @param archiveScanResults The scan results to determine what to do.
     * @param locationReference  The source of the file.
     * @return List
     */
    public List<InstallationResult> install(InputStream archiveFileStream, String destination, List<ArchiveScanResult> archiveScanResults, ILocationReference locationReference) {
        //Open the archive from the input stream.
        List<InstallationResult> installationResults = new ArrayList<>();

        archiveScanResults.forEach(scanResult -> {
            FormeInventoryItem item = new FormeInventoryItem();
            item.setUsername(locationReference.getUser());
            item.setRepository(locationReference.getRepository());
            item.setReference(locationReference.getTag());
            item.setName(scanResult.getForme().getName());
            item.setInstallPath(locationReference.getUser() + "/" + scanResult.getForme().getName());
            item.setKey(scanResult.getForme().getName());
            item.setTag(scanResult.getForme().getTag());
            item.setAddedOn(LocalDateTime.now());

            boolean installed = false;

            if (scanResult.getAction() == ScanResultAction.INSTALL_WITH_PREEXISTING)
                item.setKey(locationReference.getUser() + "/" + scanResult.getForme().getName());

            try {
                if (scanResult.getAction() != (ScanResultAction.ALREADY_INSTALLED)) {
                    this.extractFormeUsingStream(archiveFileStream, destination, scanResult, locationReference);
                    installed = true;
                }
                archiveFileStream.reset();
            } catch (Exception e) {
                this.console.error(e.getMessage());
                e.printStackTrace();
            }

            installationResults.add(new InstallationResult(scanResult, locationReference, item, installed));
        });

        return installationResults;
    }

    /**
     * This will extract the formes from the archive and place them in the inventory locations.
     *
     * @param archiveFileStream The file stream from the location.
     * @param destination       The destination for the extracted file/folders.
     * @param archiveScanResult The results of the archive scan.
     */
    private void extractFormeUsingStream(InputStream archiveFileStream, String destination, ArchiveScanResult archiveScanResult, ILocationReference locationReference) throws IOException {
        //Open the archive from the input stream.
        GZIPInputStream input = new GZIPInputStream(archiveFileStream);
        TarArchiveInputStream tar = new TarArchiveInputStream(input);

        //Get the first/next entry in the archive.
        ArchiveEntry entry = tar.getNextEntry();

        //Go until we run out of entries in the archive.
        while (entry != null) {

            //Check to make sure this entry is part of the scan results.
            if (this.includeEntry(archiveScanResult, entry.getName())) {
                //Set the expected install path.
                String outputPath = MessageFormat.format(
                        "{0}/{1}/{2}/{3}",
                        destination,
                        locationReference.getUser(),
                        archiveScanResult.getForme().getName(),
                        this.strip(entry.getName(), archiveScanResult.getDepth()
                        )
                );
                //If its a directory, we'll need to create it.
                if (entry.isDirectory())
                    (new FileSystemService()).createDirectory(outputPath);
                else
                    //If its a file, we need to copy it from the archive to the install directory.
                    ByteStreams.copy(tar, new FileOutputStream(new File(outputPath)));
            }

            //Advance the entry.
            entry = tar.getNextEntry();
        }
    }

    /**
     * Checks to see if the entry from the archive should be included.
     *
     * @param archiveScanResults The results of the archive scan.
     * @param entryName          The archive entry name.
     * @return boolean.
     */
    private boolean includeEntry(ArchiveScanResult archiveScanResults, String entryName) {
        return entryName.contains(archiveScanResults.getArchivePath());
    }

    /**
     * Strips the first value off the entry name to prevent the root of the archive beeing included in any destination paths.
     *
     * @param path The entry path.
     * @return String, the path with the root element stripped.
     */
    private String strip(String path, int startingDepth) {
        List<String> foldersPath = Arrays.asList(path.split("/"));
        return String.join("/", foldersPath.subList(startingDepth, foldersPath.size()));
    }
}
