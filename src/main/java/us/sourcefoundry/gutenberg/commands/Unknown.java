package us.sourcefoundry.gutenberg.commands;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;

/**
 * The default command to run.  In the case the command is not in the factory and therefore not found.
 */
public class Unknown implements Command {

    //The application context.
    private ApplicationContext applicationContext;

    /**
     * Constructor.
     *
     * @param applicationContext The application context.
     */
    @Inject
    public Unknown(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Runs the command.
     */
    @Override
    public void execute() {
        //Throw an error because the requested command is not provided.
        if (this.applicationContext.getCommand().equals("")) {
            (new Console()).message("Gutenberg is installed! See help for usage.");
            return;
        }

        //Throw an error because the requested command is not valid.
        (new Console()).error("! Action \"{0}\" is not valid. See help for usage.", this.applicationContext.getCommand());
    }
}
