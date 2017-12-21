package us.sourcefoundry.gutenberg.commands.listinventory;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.Map;

/**
 * This will list any formes added to inventory.
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

        //If inventory is not present, tell the user.
        if (inventory == null) {
            this.console.warning("Inventory not found or empty.");
            return;
        }

        //If teh inventory is empty, tell the user.
        if (inventory.size() < 1) {
            this.console.warning("Inventory empty.");
            return;
        }

        final int longestName = this.getLongestFormeName(inventory);

        //Otherwise, show the contents of the inventory to the user.
        this.printContents(longestName, "NAME", "REFERENCE");
        inventory.forEach((k, v) -> this.printContents(longestName,   v.getName(),
                this.buildReference(v.getUsername(), v.getRepository(), v.getReference())));
    }

    private void printContents(int minWidth, String name, String reference) {
        System.out.format("%-" + (minWidth + 4) + "s %-35s %n", name, reference);
    }

    private String buildReference(String username, String repository, String reference) {
        return MessageFormat.format("{0}/{1}:{2}", username, repository, reference);
    }

    private int getLongestFormeName( Map<String, FormeInventoryItem> inventory){
        final int[] longestName = {0};
        inventory.forEach((k, v) -> {
            if (v.getName().length() > longestName[0])
                longestName[0] = v.getName().length();
        });

        return longestName[0];
    }
}
