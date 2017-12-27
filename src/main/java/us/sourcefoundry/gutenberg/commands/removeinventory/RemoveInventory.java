package us.sourcefoundry.gutenberg.commands.removeinventory;

import org.apache.commons.io.FileUtils;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Cli;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

/**
 * This should not be documented.
 */
public class RemoveInventory implements Command {

    //The application context.
    private ApplicationContext applicationContext;
    //The command line.
    private Cli cli;
    //The console.
    private Console console;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     * @param cli                The cli service.
     * @param console            The console service.
     */
    @Inject
    public RemoveInventory(ApplicationContext applicationContext, Cli cli, Console console) {
        this.applicationContext = applicationContext;
        this.cli = cli;
        this.console = console;
    }

    @Override
    public void execute() {

        //Issue warning let them know that all formes will be removed from inventory.
        this.console.warning("Are you sure you want to remove all formes?");

        //Prompt for the code word.  This will make sure that they are intentional.
        String codeWord = this.promptForAnswer();

        //Only continue if they inputed the exact codeword.
        if (!codeWord.equals("REMOVEALL")) {
            this.console.message("Incorrect response...cancelled");
            return;
        }

        //Let them know that since they inputted the correct codeword, all formes will be removed from inventory.
        this.console.message("Removing all formes from inventory...");

        try {
            //Delete the formes directory. I know the is lazy but its also the cleanest way.
            FileUtils.deleteDirectory(new File(applicationContext.getInstallDirectory() + "/formes"));
            //Remove the inventory file.
            (new File(applicationContext.getInstallDirectory() + "/inventory.json")).delete();
        } catch (IOException e) {
            this.console.error("Could not remove forme inventory.");
        }
    }

    /**
     * Prompt the user for the codeword.
     *
     * @return String
     */
    private String promptForAnswer() {
        //Get the input scanner.
        Scanner reader = new Scanner(System.in);
        //Build the message and prompt.
        System.out.print("Please enter 'REMOVEALL' to remove all formes from inventory: ");
        //Return the response.
        return reader.nextLine();
    }
}
