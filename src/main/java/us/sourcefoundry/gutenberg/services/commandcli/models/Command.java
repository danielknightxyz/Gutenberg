package us.sourcefoundry.gutenberg.services.commandcli.models;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class Command {

    //The name of the command.
    private String name;
    //The description of the command.
    private String description;
    //The command options.
    private List<Option> options;
    //Any sub-commands.
    private List<Command> subCommands;
    //Include this command is documentation.
    private boolean documented = true;
}
