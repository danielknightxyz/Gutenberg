package us.sourcefoundry.gutenberg.commands;

/**
 * This interface specifies the common interface for commands (or actions) in the Gutenberg application.
 */
public interface Command {

    /**
     * Executes the command.
     */
    void execute();

    /**
     * Shows the command's help.
     */
    void help();

    /**
     * Has the help for the command been requested by the user.
     *
     * @return boolean
     */
    boolean hasHelp();
}
