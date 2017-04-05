package us.sourcefoundry.gutenberg.commands;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Console;

public class Unknown implements Command{

    private ApplicationContext applicationContext;

    public Unknown(ApplicationContext applicationContext){
        this.applicationContext = applicationContext;
    }

    @Override
    public void execute() {
        //Throw an error because the requested command is unknown.
        (new Console()).error("Action \"{0}\" is unknown.",this.applicationContext.getCommand());
    }
}
