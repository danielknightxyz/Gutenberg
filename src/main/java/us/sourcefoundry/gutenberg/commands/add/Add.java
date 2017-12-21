package us.sourcefoundry.gutenberg.commands.add;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.FormeFactory;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.models.forme.Forme;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.console.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * This will connect to Github and download a repositories containing one or more formes.  The repository will be scanned
 * and any formes found will be added to the local inventory.
 */
public class Add implements Command {

    //The application context.
    private ApplicationContext applicationContext;
    //The command line.
    private Cli cli;
    //The console.
    private Console console;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cli                The cli service.
     * @param console            The console service.
     */
    @Inject
    public Add(ApplicationContext applicationContext, Cli cli, Console console) {
        this.applicationContext = applicationContext;
        this.cli = cli;
        this.console = console;
    }

    /**
     * This will execute the action to add forme(s) to Gutenberg inventory
     */
    @Override
    public void execute() {

        //This will get the install directory and make sure it exists.  This is safe even if the location exists already.
        String installDir = this.applicationContext.getInstallDirectory();
        (new FileSystemService()).createDirectory(installDir);

        //This github api url.
        String githubURL = "https://api.github.com/repos/{0}/{1}/tarball/{2}";
        List<String> repositories = this.getCommandlineRepostiories();

        //Get the existing inventory.
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(installDir + "/inventory.json");

        //For each repository you want to add, do the following to download, scan, and add them to the local inventory.
        repositories.forEach(
                r -> {
                    GithubLocation githubLocation = GithubLocation.fromString(r);

                    String resourceURL = MessageFormat.format(githubURL, githubLocation.getUser(), githubLocation.getRepository(), githubLocation.getReference());

                    this.console.message("Add from {0}", r);
                    this.console.info("Downloading {0}", resourceURL);

                    try {
                        //Download the file from Github and buffer it for processing.
                        URL url = new URL(resourceURL);
                        InputStream urlStream = url.openStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        IOUtils.copy(urlStream, baos);
                        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());

                        //Get the paths for the formes from archive
                        List<ArchiveScanResult> archiveScanResults = scanArchiveForFormes(bais);
                        bais.reset();

                        this.console.message( "\n{0} formes found: {1}\n", archiveScanResults.size(), String.join(
                                ", ",
                                archiveScanResults.stream()
                                        .map(
                                                scanResult -> scanResult.getForme().getName()
                                        )
                                        .collect(Collectors.toList())
                                )
                        );

                        this.extractUsingStream(bais, installDir + "/formes", archiveScanResults);

                        Map<String, FormeInventoryItem> newInventory = archiveScanResults
                                .stream()
                                .map(scanResult -> {
                                    this.console.info("+ {0} added", scanResult.getForme().getName());
                                    FormeInventoryItem item = new FormeInventoryItem();
                                    item.setUsername(githubLocation.getUser());
                                    item.setRepository(githubLocation.getRepository());
                                    item.setReference(githubLocation.getReference());
                                    item.setName(scanResult.getForme().getName());
                                    item.setInstallPath(scanResult.getForme().getName());
                                    return item;
                                })
                                .collect(Collectors.toMap(FormeInventoryItem::getName, Function.identity()));

                        inventory.putAll(newInventory);
                    } catch (FileNotFoundException | UnknownHostException e) {
                        this.console.error("{0} could not be found on Github.", r);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        //Store the inventory.
        (new FileSystemService()).createFile(installDir + "/inventory.json", (new Gson().toJson(inventory)));
    }

    /**
     * Gets the repositories locations from the command line ignoring the first argument which is the action.
     *
     * @return List
     */
    private List<String> getCommandlineRepostiories() {
        return this.cli.getArgList().subList(1, this.cli.getArgList().size());
    }

    /**
     * This will scan the github archive for formes.
     *
     * @param urlStream The file stream from github.
     * @return List
     */
    private List<ArchiveScanResult> scanArchiveForFormes(InputStream urlStream) throws IOException {
        GZIPInputStream input = new GZIPInputStream(urlStream);
        TarArchiveInputStream tar = new TarArchiveInputStream(input);

        ArchiveEntry entry = tar.getNextEntry();
        List<ArchiveScanResult> results = new ArrayList<>();
        String currentArchiveDirectory = "";

        while (entry != null) {
            if (entry.isDirectory())
                currentArchiveDirectory = entry.getName();
            else if (FilenameUtils.getName(entry.getName().toLowerCase()).equals("forme.yml")) {
                Forme forme = (new FormeFactory()).newInstance(tar);
                ArchiveScanResult result = new ArchiveScanResult();
                result.setArchivePath(currentArchiveDirectory);
                result.setForme(forme);
                result.setDepth(Arrays.asList(FilenameUtils.getPath(entry.getName()).split("/")).size());
                results.add(result);
            }

            entry = tar.getNextEntry();
        }

        return results;
    }

    /**
     * This will extract the formes from the github archive and place them in the inventory locations.
     *
     * @param urlStream          The file stream from github.
     * @param destination        The destination for the extracted file/folders.
     * @param archiveScanResults The results of the archive scan.
     */
    private void extractUsingStream(InputStream urlStream, String destination, List<ArchiveScanResult> archiveScanResults) throws IOException {
        //Open the archive from the input stream.
        GZIPInputStream input = new GZIPInputStream(urlStream);
        TarArchiveInputStream tar = new TarArchiveInputStream(input);

        //Get the first/next entry in the archive.
        ArchiveEntry entry = tar.getNextEntry();

        //Go until we run out of entries in the archive.
        while (entry != null) {

            //Check to make sure this entry is part of the scan results.
            if (this.includeEntry(archiveScanResults, entry.getName())) {
                ArchiveScanResult archiveScanResult = this.getArchiveScanResult(archiveScanResults, entry.getName());
                //Set the expected install path.
                String outputPath = destination + "/" + archiveScanResult.getForme().getName() + "/" + this.strip(entry.getName(), archiveScanResult.getDepth());
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
     * Get the scan result for a particular path.
     *
     * @param archiveScanResults The scan results from when the archive was checked for formes.
     * @param entryName          The entry name.
     * @return ArchiveScanResult
     */
    private ArchiveScanResult getArchiveScanResult(List<ArchiveScanResult> archiveScanResults, String entryName) {
        for (ArchiveScanResult result : archiveScanResults)
            if (entryName.contains(result.getArchivePath()))
                return result;
        return null;
    }

    /**
     * Checks to see if the entry from the archive should be included.
     *
     * @param archiveScanResults The results of the archive scan.
     * @param entryName          The archive entry name.
     * @return boolean.
     */
    private boolean includeEntry(List<ArchiveScanResult> archiveScanResults, String entryName) {
        for (ArchiveScanResult result : archiveScanResults)
            if (entryName.contains(result.getArchivePath()))
                return true;
        return false;
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
