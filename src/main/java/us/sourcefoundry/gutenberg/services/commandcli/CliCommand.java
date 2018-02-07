package us.sourcefoundry.gutenberg.services.commandcli;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.sourcefoundry.gutenberg.services.commandcli.models.CliOption;
import us.sourcefoundry.gutenberg.services.commandcli.models.Command;

import java.util.List;
import java.util.Map;

/**
 * This class works has an interface to the command line.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CliCommand {

    private Command reference;
    private Map<String, CliOption> readOptions;
    private CliCommand subCommand;
    private List<String> additionalArgs;

    public boolean hasOption(String optionName) {
        return this.readOptions != null && this.readOptions.containsKey(optionName);
    }

    public CliOption getOption(String optionName) {
        if (this.readOptions == null)
            return null;

        return this.readOptions.get(optionName);
    }
}
