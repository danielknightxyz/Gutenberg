package us.sourcefoundry.gutenberg.commands.add;

import com.google.gson.Gson;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.commands.add.models.ArchiveScanResult;
import us.sourcefoundry.gutenberg.commands.add.models.ILocationReference;
import us.sourcefoundry.gutenberg.commands.add.models.InstallationResult;
import us.sourcefoundry.gutenberg.commands.add.models.ScanResultAction;
import us.sourcefoundry.gutenberg.commands.add.providers.github.GithubLocation;
import us.sourcefoundry.gutenberg.commands.add.providers.github.GithubProviderClient;
import us.sourcefoundry.gutenberg.commands.add.services.ArchiveFormeScanner;
import us.sourcefoundry.gutenberg.commands.add.services.FormeInstaller;
import us.sourcefoundry.gutenberg.config.ApplicationProperties;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.CliService;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This will connect to Github and download a repositories containing one or more formes.  The repository will be scanned
 * and any formes found will be addedOn to the local inventory.
 */
public class Add implements Command {

    //The application properties.
    private ApplicationProperties applicationProperties;
    //The application context.
    private ApplicationContext applicationContext;
    //The command line.
    private CliCommand cli;
    //The console.
    private Console console;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cliService         The cliService service.
     * @param console            The console service.
     */
    @Inject
    public Add(ApplicationContext applicationContext, CliService cliService, Console console, ApplicationProperties applicationProperties) {
        this.applicationContext = applicationContext;
        this.cli = cliService.getRootCommand().getSubCommand();
        this.console = console;
        this.applicationProperties = applicationProperties;
    }

    /**
     * This will execute the action to add forme(s) to Gutenberg inventory
     */
    @Override
    public void execute() {

        //This will get the install directory and make sure it exists.  This is safe even if the location exists already.
        String installDir = this.applicationContext.getInstallDirectory();
        String formeInstallDir = installDir + "/formes";

        //Create the install location if necessary.
        (new FileSystemService()).createDirectory(installDir);

        //Get the commandline repos.
        List<String> locations = this.getCommandLineLocations();

        //Get the existing inventory.
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(installDir + "/inventory.json");

        List<InstallationResult> installationResults = new ArrayList<>();

        this.console.message("");

        //For each repository you want to add, do the following to download, scan, and add them to the local inventory.
        locations.forEach(
                r -> {

                    this.console.message("Adding from {0}", r);

                    //Get the github location from the commandline argument.
                    ILocationReference locationReference = GithubLocation.fromString(r);
                    //Get the provider client.
                    GithubProviderClient githubProviderClient = new GithubProviderClient(this.console, this.applicationProperties);
                    ByteArrayInputStream archiveFileStream = githubProviderClient.getFormeFiles(locationReference);

                    //Get the paths for the formes from archive
                    List<ArchiveScanResult> formeScanResults;
                    try {
                        formeScanResults = (new ArchiveFormeScanner()).scan(archiveFileStream, locationReference, inventory);
                    } catch (IOException e) {
                        this.console.error("Could not open archive from location.");
                        return;
                    }

                    //Get the name of the formes in the archive results.
                    List<String> formesInArchive = this.getFormeNamesFromArchiveScanResults(formeScanResults);

                    //Notify the user about the formes found.
                    this.console.info("{0} formes found: {1}", formeScanResults.size(), String.join(", ", formesInArchive));

                    //Install the formes, and get back the installation results.
                    List<InstallationResult> currentInstallationResults = (new FormeInstaller(this.console)).install(archiveFileStream, formeInstallDir, formeScanResults, locationReference);
                    installationResults.addAll(currentInstallationResults);

                    //We need to add the installed formes to the inventory because, 1: they've been installed 2: the future archives need to know what's been installed.
                    currentInstallationResults.stream()
                            //Only put what was installed into inventory.
                            .filter(InstallationResult::isInstalled)
                            //Put it in inventory.
                            .forEach(result -> inventory.put(result.getFormeInventoryItem().getKey(), result.getFormeInventoryItem()));
                }
        );

        //Store the inventory.
        (new FileSystemService()).createFile(installDir + "/inventory.json", (new Gson().toJson(inventory)));

        if (installationResults.isEmpty())
            this.console.info("Nothing Installed. See help for usage.");

        if (!installationResults.isEmpty()) {
            //Print the results for the user.
            this.console.message("");

            this.printResultsHeader("NAME", "TAG", "REFERENCE", "ADDED");
            installationResults.forEach(result -> {
                        String installationMessage = this.getInstallationMessage(result.getArchiveScanResult().getAction());
                        String tag = (result.getArchiveScanResult().getForme().getTag() != null ? result.getArchiveScanResult().getForme().getTag() : "none");
                        String actionTaken = (result.isInstalled() ? "Yes" : "No") + " " + installationMessage;

                        this.printResults(
                                result.getFormeInventoryItem().getKey(),
                                tag,
                                result.getLocationReference().toFQN(),
                                actionTaken
                        );
                    }
            );

            this.console.message("");
        }
    }

    /**
     * Prints the help for the command.
     */
    @Override
    public void help() {
        System.out.println("add usage: gutenberg add [-h] [REPOSITORY...] \n");

        System.out.println("Options:");

        for (Option option : this.cli.getReference().getOptions()) {
            String shortOption = (option.getName() != null ? "-" + option.getName() + "," : "");
            String longOption = (option.getLongName() != null ? "--" + option.getLongName() : "");
            String description = option.getDescription();

            System.out.format("%-1s %-13s %-60s %n", shortOption, longOption, (description != null ? description : ""));
        }
    }

    /**
     * Is the help been requested.
     *
     * @return boolean
     */
    @Override
    public boolean hasHelp() {
        return this.cli.hasOption("h");
    }

    /**
     * Print the results header.
     *
     * @param key         The Key
     * @param tag         The Tag
     * @param reference   The reference.
     * @param actionTaken The action.
     */
    private void printResultsHeader(String key, String tag, String reference, String actionTaken) {
        System.out.format("\u001B[90m%-45s %-15s %-60s %-50s\u001B[0m%n", key, tag, reference, actionTaken);
    }

    /**
     * Print the result.
     *
     * @param key         The Key
     * @param tag         The Tag
     * @param reference   The reference.
     * @param actionTaken The action.
     */
    private void printResults(String key, String tag, String reference, String actionTaken) {
        System.out.format("%-45s %-15s %-60s %-50s %n", key, tag, reference, actionTaken);
    }

    /**
     * Gets the repositories locations from the command line ignoring the first argument which is the action.
     *
     * @return List
     */
    private List<String> getCommandLineLocations() {
        return this.cli.getAdditionalArgs();
    }

    /**
     * Get the name of the formes in the source.
     *
     * @param scanResults scan results
     * @return List
     */
    private List<String> getFormeNamesFromArchiveScanResults(List<ArchiveScanResult> scanResults) {
        //Get the name of the formes in the archive results.
        return scanResults
                .stream()
                .map(scanResult -> scanResult.getForme().getName())
                .collect(Collectors.toList());
    }

    /**
     * Get the message for the installation message.
     *
     * @param scanResultAction The action.
     * @return String
     */
    private String getInstallationMessage(ScanResultAction scanResultAction) {
        switch (scanResultAction) {
            case REPLACE:
                return "(Replaced)";
            case ALREADY_INSTALLED:
                return "(Already Installed)";
            default:
                return "";
        }

    }
}
