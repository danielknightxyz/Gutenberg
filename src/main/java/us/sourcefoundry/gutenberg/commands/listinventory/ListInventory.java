package us.sourcefoundry.gutenberg.commands.listinventory;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.models.HumanFriendlyDate;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Map;

/**
 * This will list any formes addedOn to inventory.
 */
public class ListInventory implements Command {

    //The application context.
    private ApplicationContext applicationContext;
    //The console.
    private Console console;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param console            The console service.
     */
    @Inject
    public ListInventory(ApplicationContext applicationContext, Console console) {
        this.applicationContext = applicationContext;
        this.console = console;
    }

    /**
     * This will execute the action to list inventory entries.
     */
    @Override
    public void execute() {
        //Get the inventory.
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(this.applicationContext.getInstallDirectory() + "/inventory.json");

        //If teh inventory is empty, tell the user.
        if (inventory == null || inventory.size() < 1) {
            this.console.message("");
            this.console.message("Your inventory empty.");
            this.console.message("");
            return;
        }

        final int longestName = this.getLongestFormeName(inventory);

        this.console.message("");
        //Otherwise, show the contents of the inventory to the user.
        this.printContentsHeader(longestName, "NAME", "TAG", "SOURCE", "ADDED");
        inventory.forEach((key, inventoryItem) ->
                this.printContents(
                        longestName,
                        inventoryItem.getKey(),
                        (inventoryItem.getTag() == null ? "none" : inventoryItem.getTag()),
                        this.buildReference(inventoryItem.getUsername(), inventoryItem.getRepository(), inventoryItem.getReference()),
                        HumanFriendlyDate.fromLocalDateTime(inventoryItem.getAddedOn()).getPrettyTime()
                )
        );

        this.console.message("");
    }

    /**
     * Prints the help for the command.
     */
    @Override
    public void help() {
    }

    /**
     * Is the help been requested.
     *
     * @return boolean
     */
    @Override
    public boolean hasHelp() {
        return false;
    }

    /**
     * Prints the results header.
     *
     * @param minWidth  Min width of the first cell.
     * @param name      The name header.
     * @param tag       The tag header.
     * @param reference The reference header.
     * @param dateAdded The date added header.
     */
    private void printContentsHeader(int minWidth, String name, String tag, String reference, String dateAdded) {
        System.out.format("\u001B[90m%-" + (minWidth + 4) + "s %-10s %-60s %-10s\u001B[0m %n", name, tag, reference, dateAdded);
    }

    /**
     * Prints the results row.
     *
     * @param minWidth  Min width of the first cell.
     * @param name      The name row.
     * @param tag       The tag row.
     * @param reference The reference row.
     * @param dateAdded The date added row.
     */
    private void printContents(int minWidth, String name, String tag, String reference, String dateAdded) {
        System.out.format("%-" + (minWidth + 4) + "s %-10s %-60s %-10s%n", name, tag, reference, dateAdded);
    }

    /**
     * Build a Github reference.
     *
     * @param username   The Github user.
     * @param repository The Github repo.
     * @param reference  The Github tag or reference.
     * @return String
     */
    private String buildReference(String username, String repository, String reference) {
        return MessageFormat.format("{0}/{1}:{2}", username, repository, reference);
    }

    /**
     * Gets the length of the longest name. This will be used for man width of the results table.
     *
     * @param inventory The inventory.
     * @return int
     */
    private int getLongestFormeName(Map<String, FormeInventoryItem> inventory) {
        final int[] longestName = {0};
        inventory.forEach((k, v) -> {
            if (v.getKey().length() > longestName[0])
                longestName[0] = v.getKey().length();
        });

        return longestName[0];
    }
}
