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
            (new Console()).message("Yay! Gutenberg is installed! See help for usage.");
            return;
        }

        //Throw an error because the requested command is not valid.
        (new Console()).error("! Action \"{0}\" is not valid. See help for usage.",this.applicationContext.getCommand());
    }
}
