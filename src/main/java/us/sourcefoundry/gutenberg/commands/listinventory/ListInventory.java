package us.sourcefoundry.gutenberg.commands.listinventory;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
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

        //Otherwise, show the contents of the inventory to the user.
        this.console.message("Inventory Contents:");
        inventory.forEach((k, v) -> this.console.info("> {0}", v.getName()));
        this.console.message("\n");
    }
}
