package us.sourcefoundry.gutenberg.commands.root;

import us.sourcefoundry.gutenberg.commands.Command;
import us.sourcefoundry.gutenberg.services.CliService;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;

public class RootCommand implements Command {

    private CliCommand cli;
    private Console console;

    /**
     * Constructor.
     */
    @Inject
    public RootCommand(Console console, CliService cliService) {
        this.console = console;
        this.cli = cliService.getRootCommand();
    }

    /**
     * Runs the command.
     */
    @Override
    public void execute() {
        this.console.message("Gutenberg is installed! See help for usage.");
    }

    /**
     * Prints the help for the command.
     */
    @Override
    public void help() {
        System.out.println("usage: gutenberg [-h] [-v] [ACTION] [ACTION OPTIONS] [ARG...]\n");

        System.out.println("Options:");

        for (Option option : this.cli.getReference().getOptions()) {
            String shortOption = (option.getName() != null ? "-" + option.getName() + "," : "");
            String longOption = (option.getLongName() != null ? "--" + option.getLongName() : "");
            String description = option.getDescription();

            System.out.format("%-1s %-12s %-60s %n", shortOption, longOption, (description != null ? description : ""));
        }

        System.out.println("\nActions:");

        for (us.sourcefoundry.gutenberg.services.commandcli.models.Command command : this.cli.getReference().getSubCommands()) {
            String commandName = command.getName();
            String description = command.getDescription();

            if (command.isDocumented())
                System.out.format("%-9s %-60s %n", commandName, (description != null ? description : "None"));
        }

        System.out.println("\nPlease visit https://github.com/danielknightxyz/gutenberg for more help.");
    }

    /**
     * Is the help been requested.
     *
     * @return boolean
     */
    @Override
    public boolean hasHelp() {
        return this.cli.hasOption("h");
    }


}
