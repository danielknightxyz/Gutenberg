package us.sourcefoundry.gutenberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.CommandFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Cli;

import java.io.*;
import java.util.*;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        /*
         * Setup some shit.
         */

        Cli cli = new Cli(args);

        if (cli.hasBlockingOption()) {
            cli.printBlockingOption();
            return;
        }

        //Get some details about the system.
        String workingDir = System.getProperty("user.dir");
        String sourceDir = workingDir;

        if(cli.hasOption("in"))
            sourceDir = cli.getOptionValue("in");

        List remainingArgs = cli.getArgList();
        String commandName = "";

        if (remainingArgs.size() > 0)
            commandName = remainingArgs.get(0).toString();

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = new ApplicationContext();
        applicationContext.setWorkingDirectory(workingDir);
        applicationContext.setSourceDirectory(sourceDir);

        //Create the command.
        Command command = (new CommandFactory(applicationContext,cli).make(commandName));
        command.execute();
    }



}
