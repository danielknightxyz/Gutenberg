package us.sourcefoundry.gutenberg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.factories.ApplicationContextFactory;
import us.sourcefoundry.gutenberg.factories.CommandFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.*;
import us.sourcefoundry.gutenberg.services.Console;

import java.io.*;

public class Main {

    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        //Start by getting the CLI args.
        Cli cli = new Cli(args);

        //If we need to show a blocking option, do it.
        if (cli.hasBlockingOption()) {
            cli.printBlockingOption();
            return;
        }
        
        String banner = "\n" +
                        " _____       _             _                    \n" +
                        "|  __ \\     | |           | |                   \n" +
                        "| |  \\/_   _| |_ ___ _ __ | |__   ___ _ __ __ _ \n" +
                        "| | __| | | | __/ _ \\ '_  \\| '_   \\/ _ \\ '__/  _` |\n" +
                        "| |_\\ \\ |_| | ||  __/ | | | |_) |  __/ | | (_| |\n" +
                        " \\____/\\__,_|\\__\\___|_| |_|_.__/ \\___|_|  \\__, |\n" +
                        "                                           __/ |\n" +
                        "                                          |___/\n";

        //Show the banner
        (new Console()).message(banner);

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = (new ApplicationContextFactory()).make(cli);

        //Create the command.
        Command command = (new CommandFactory(applicationContext,cli).make(applicationContext.getCommand()));
        command.execute();
    }
}
