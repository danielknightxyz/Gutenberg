package us.sourcefoundry.gutenberg.services.commandcli.exceptions;

/**
 * This exception gets thrown when a user provides an argument that was not part of the command requiremnts.
 */
public class UnknownArgumentException extends RuntimeException {

    public UnknownArgumentException(String argument){
        super("Unknown argument: " + argument);
    }
}
