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

    private void printContentsHeader(int minWidth, String name, String tag, String reference, String dateAdded) {
        System.out.format("\u001B[90m%-" + (minWidth + 4) + "s %-10s %-60s %-10s\u001B[0m %n", name, tag, reference, dateAdded);
    }

    private void printContents(int minWidth, String name, String tag, String reference, String dateAdded) {
        System.out.format("%-" + (minWidth + 4) + "s %-10s %-60s %-10s%n", name, tag, reference, dateAdded);
    }

    private String buildReference(String username, String repository, String reference) {
        return MessageFormat.format("{0}/{1}:{2}", username, repository, reference);
    }

    private int getLongestFormeName(Map<String, FormeInventoryItem> inventory) {
        final int[] longestName = {0};
        inventory.forEach((k, v) -> {
            if (v.getKey().length() > longestName[0])
                longestName[0] = v.getKey().length();
        });

        return longestName[0];
    }
}
