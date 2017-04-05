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
        //Throw an error because the requested command is not provided.
        if(this.applicationContext.getCommand().equals("")) {
            (new Console()).error("! Action not provided.  See help for options.");
            return;
        }

        //Throw an error because the requested command is not valid.
        (new Console()).error("! Action \"{0}\" is not valid.",this.applicationContext.getCommand());
    }
}
