package us.sourcefoundry.gutenberg.commands.removeinventory;

import org.apache.commons.io.FileUtils;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Random;
import java.util.Scanner;

/**
 * This should not be documented.
 */
public class RemoveInventory implements Command {

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
    public RemoveInventory(ApplicationContext applicationContext, Console console) {
        this.applicationContext = applicationContext;
        this.console = console;
    }

    /**
     * Runs the command.
     */
    @Override
    public void execute() {

        //Issue warning let them know that all formes will be removed from inventory.
        this.console.warning("Are you sure you want to remove all formes? CTRL-C to cancel.");

        String challengeCode = this.getChallenge();
        //Prompt for the code word.  This will make sure that they are intentional.
        String codeWord = this.promptForAnswer(challengeCode);

        //Only continue if they inputed the exact codeword.
        if (!codeWord.equals(challengeCode)) {
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
     * Prompt the user for the codeword.
     *
     * @return String
     */
    private String promptForAnswer(String challengeCode) {
        //Get the input scanner.
        Scanner reader = new Scanner(System.in);
        //Build the message and prompt.
        System.out.print(
                MessageFormat.format("Please enter {0} to remove all formes from inventory: ", challengeCode)
        );
        //Return the response.
        return reader.nextLine();
    }

    /**
     * Get a challenge string for the user to input.
     *
     * @return String
     */
    private String getChallenge() {
        //The approved characters for a challenge.
        String approvedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder challenge = new StringBuilder();
        //Generate 5 random charactors and add them to the string builder.
        Random rnd = new Random();
        while (challenge.length() < 5) {
            int index = (int) (rnd.nextFloat() * approvedChars.length());
            challenge.append(approvedChars.charAt(index));
        }
        //Build the string and return it.
        return challenge.toString();

    }
}
