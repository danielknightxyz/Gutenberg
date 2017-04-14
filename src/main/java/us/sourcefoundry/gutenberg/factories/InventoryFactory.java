package us.sourcefoundry.gutenberg.factories;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import us.sourcefoundry.gutenberg.models.FormeInventoryItem;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class InventoryFactory extends AbstractFactory<Map<String, FormeInventoryItem>> {

    @Override
    public Map<String, FormeInventoryItem> newInstance() {
        return null;
    }

    public Map<String, FormeInventoryItem> newInstance(String inventoryLocation) {
        try {
            Map<String, FormeInventoryItem> inventory = new HashMap<>();
            Type type = new TypeToken<Map<String, FormeInventoryItem>>() {
            }.getType();

            return (new Gson()).fromJson(new FileReader((new FileSystemService()).getByLocation(inventoryLocation)), type);
        } catch (FileNotFoundException e) {
            (new Console()).error(e.getMessage());
            return new HashMap<>();
        }
    }
}
