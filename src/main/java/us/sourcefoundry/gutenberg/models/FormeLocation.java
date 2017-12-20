package us.sourcefoundry.gutenberg.models;

import lombok.Getter;
import lombok.Setter;
import us.sourcefoundry.gutenberg.factories.InventoryFactory;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.console.Console;

import java.util.Map;

@Getter
@Setter
public class FormeLocation {

    //The location of the file
    private String path;

    /**
     * Constructor
     * @param path The path for the forme location.
     */
    private FormeLocation(String path) {
        this.path = path;
    }

    /**
     * Determines the location of the Forme file.  First by seeing if the forme location is being supplied by the user
     * via the local option.  If not, its going to check and make sure there's a forme name provided as an argument.  If
     * the forme name is given as a argument, this will be looked up in the inventory.
     *
     * @return String
     */
    public static FormeLocation fromCli(Cli cli, ApplicationContext applicationContext, Console console) {
        //If the forme is local, then look some where other than the inventory; which will be supplied by the user.
        if (cli.hasOption("local"))
            //Get local forme.
            return cli.getOptionValue("local").equals(".") ? new FormeLocation(applicationContext.getWorkingDirectory()) : new FormeLocation(cli.getOptionValue("local"));

        //Else, lets check out the inventory.
        String formeName = (cli.getArgList().get(1) != null ? cli.getArgList().get(1).toString() : null);

        //If the forme name is not provided, return null.
        if (formeName == null)
            return null;

        return determineInventoryFormeLocation(formeName, applicationContext, console);
    }

    /**
     * This will look up the installation path of a forme from the installed inventory.  If the inventory doesn't exist,
     * then null will be returned.
     *
     * @param formeName The name of the forme to look up in inventory.
     * @return
     */
    private static FormeLocation determineInventoryFormeLocation(String formeName, ApplicationContext applicationContext, Console console) {
        //Get the install directory.
        String installDir = applicationContext.getInstallDirectory();
        //Get the inventory.
        Map<String, FormeInventoryItem> inventory = (new InventoryFactory()).newInstance(installDir + "/inventory.json");

        //Check to make sure the inventory is valid.  If the user is trying to run a local forme, then we don't care
        //if the inventory is valid.
        if (inventory == null) {
            console.info("No inventory found.");
            return null;
        }

        //If the forme is not in inventory, return null.
        if (!inventory.containsKey(formeName)) {
            console.info("{0} not found in inventory.");
            return null;
        }

        //Get inventory forme.
        return new FormeLocation(installDir + "/formes/" + inventory.get(formeName).getInstallPath());
    }
}
