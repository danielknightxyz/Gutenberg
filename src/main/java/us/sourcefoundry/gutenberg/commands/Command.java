package us.sourcefoundry.gutenberg.commands;

/**
 * This interface specifies the common interface for commands (or actions) in the Gutenberg application.
 */
public interface Command {

    /**
     * Executes the command.
     */
    void execute();
}
