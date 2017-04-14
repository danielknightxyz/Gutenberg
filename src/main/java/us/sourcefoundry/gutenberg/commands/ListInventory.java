package us.sourcefoundry.gutenberg.commands;

import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.Console;

import javax.inject.Inject;
import java.util.Map;

public class ListInventory implements Command {

    //The application context.
    private ApplicationContext applicationContext;

    @Inject
    public ListInventory(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute() {
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(this.applicationContext.getInstallDirectory() + "/inventory.json");

        if(inventory == null) {
            (new Console()).warning("! Inventory not found or empty.");
            return;
        }

        (new Console()).message("Inventory Contents:");
        inventory.forEach((k,v) -> (new Console()).info("> {0}", v.getName()));
    }
}
