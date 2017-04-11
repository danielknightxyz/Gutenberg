package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.commands.*;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Cli;

public class CommandFactory {

    private ApplicationContext applicationContext;
    private Cli cli;

    public CommandFactory(ApplicationContext applicationContext, Cli cli){
        this.applicationContext = applicationContext;
        this.cli = cli;
    }

    public Command make(String cliCommand){
        switch (cliCommand.toLowerCase()){
            case "add": return new Add(this.cli);
            case "init": return new Init(this.applicationContext,this.cli);
            case "build": return new Build(this.applicationContext,this.cli);
            default: return new Unknown(this.applicationContext);
        }
    }
}
