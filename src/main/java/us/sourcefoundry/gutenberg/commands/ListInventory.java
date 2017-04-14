package us.sourcefoundry.gutenberg.commands;

import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.Console;

import javax.inject.Inject;
import java.util.Map;

/**
 * This will list any formes added to inventory.
 */
public class ListInventory implements Command {

    //The application context.
    private ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     */
    @Inject
    public ListInventory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * This will execute the action to list inventory entries.
     */
    @Override
    public void execute() {
        //Get the inventory.
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(this.applicationContext.getInstallDirectory() + "/inventory.json");

        //If inventory is not present, tell the user.
        if(inventory == null) {
            (new Console()).warning("! Inventory not found or empty.");
            return;
        }

        //If teh inventory is empty, tell the user.
        if(inventory.size() < 1){
            (new Console()).warning("! Inventory empty.");
            return;
        }

        //Otherwise, show the contents of the inventory to the user.
        (new Console()).message("Inventory Contents:");
        inventory.forEach((k,v) -> (new Console()).info("> {0}", v.getName()));
    }
}
