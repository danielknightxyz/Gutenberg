package us.sourcefoundry.gutenberg.commands;

import com.google.common.io.ByteStreams;
import com.google.gson.Gson;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import us.sourcefoundry.gutenberg.factories.TemplateContextFactory;
import us.sourcefoundry.gutenberg.models.ArchiveScanResult;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.*;
import java.net.URL;
import java.text.MessageFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

public class Add implements Command {

    private Cli cli;

    public Add(Cli cli) {
        this.cli = cli;
    }

    @Override
    public void execute() {

        String installDir = System.getProperty("user.home") + "/.gutenberg";
        (new FileSystemService()).createDirectory(installDir);

        String githubURL = "https://api.github.com/repos/{0}/{1}/tarball/{2}";
        List<String> repositories = this.cli.getArgList().subList(1, this.cli.getArgList().size());

        Pattern pattern = Pattern.compile("^(.+)\\/(.+):(.+)$|^(.+)\\/(.+)$");

        HashMap<String, FormeInventoryItem> inventory = new HashMap<>();

        try {
            inventory = (new Gson()).fromJson(new FileReader((new FileSystemService()).getByLocation(installDir + "/inventory.json")),HashMap.class);
        } catch (FileNotFoundException e) {
            (new Console()).info("! No inventory found. One will be created.");
        }

        HashMap<String, FormeInventoryItem> finalInventory = inventory;
        repositories.forEach(
                r -> {
                    Matcher matcher = pattern.matcher(r);

                    if (!matcher.matches())
                        return;

                    String githubUser = (matcher.group(1) != null ? matcher.group(1) : matcher.group(4));
                    String githubRepo = (matcher.group(2) != null ? matcher.group(2) : matcher.group(5));
                    String githubRef = (matcher.group(3) != null ? matcher.group(3) : "master");

                    String resourceURL = MessageFormat.format(githubURL, githubUser, githubRepo, githubRef);

                    (new Console()).message("> Add Forme {0}", r);
                    (new Console()).message("# Adding {0}", resourceURL);

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

                        (new Console()).message("# {0} formes found: {1}", archiveScanResults.size(), String.join(
                                ", ",
                                archiveScanResults.stream()
                                        .map(
                                                scanResult -> scanResult.getContext().getName()
                                        )
                                        .collect(Collectors.toList())
                                )
                        );

                        this.extractUsingStream(bais, installDir + "/formes", archiveScanResults);

                        Map<String, FormeInventoryItem> newInventory = archiveScanResults
                                .stream()
                                .map(scanResult -> {
                                    FormeInventoryItem item = new FormeInventoryItem();
                                    item.setUsername(githubUser);
                                    item.setRepository(githubRepo);
                                    item.setReference(githubRef);
                                    item.setName(scanResult.getContext().getName());
                                    item.setInstallPath(this.strip(scanResult.getArchivePath()));
                                    return item;
                                })
                                .collect(Collectors.toMap(FormeInventoryItem::getName, Function.identity()));

                        finalInventory.putAll(newInventory);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );

        (new FileSystemService()).createFile(installDir + "/inventory.json",(new Gson().toJson(finalInventory)));
    }

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
                FormeContext formeContext = (new TemplateContextFactory()).make(tar);
                ArchiveScanResult result = new ArchiveScanResult();
                result.setArchivePath(currentArchiveDirectory);
                result.setContext(formeContext);
                results.add(result);
            }

            entry = tar.getNextEntry();
        }

        return results;
    }

    private void extractUsingStream(InputStream urlStream, String file, List<ArchiveScanResult> archiveScanResults) throws IOException {
        GZIPInputStream input = new GZIPInputStream(urlStream);
        TarArchiveInputStream tar = new TarArchiveInputStream(input);

        ArchiveEntry entry = tar.getNextEntry();

        while (entry != null) {
            String outputPath = file + "/" + this.strip(entry.getName());

            if (this.includeEntry(archiveScanResults, entry.getName())) {

                if (entry.isDirectory())
                    (new FileSystemService()).createDirectory(outputPath);
                else
                    ByteStreams.copy(tar, new FileOutputStream(new File(outputPath)));
            }

            entry = tar.getNextEntry();
        }
    }

    private boolean includeEntry(List<ArchiveScanResult> archiveScanResults, String entryName) {

        for (ArchiveScanResult result : archiveScanResults)
            if (entryName.contains(result.getArchivePath()))
                return true;

        return false;
    }

    private String strip(String path){
        List<String> foldersPath = Arrays.asList(path.split("/"));
        return String.join("/", foldersPath.subList(1, foldersPath.size()));
    }
}
