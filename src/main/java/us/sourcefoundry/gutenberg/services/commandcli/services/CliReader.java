package us.sourcefoundry.gutenberg.services.commandcli.services;

import lombok.NoArgsConstructor;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.models.CliOption;
import us.sourcefoundry.gutenberg.services.commandcli.models.Command;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;
import us.sourcefoundry.gutenberg.utils.Pair;

import java.util.*;

@NoArgsConstructor
public class CliReader {

    public CliCommand read(String[] args, Command command) {

        Map<String, CliOption> readArgumentMap = new HashMap<>();
        List<String> additionalArgs = new ArrayList<>();

        Command currentCommand = command;

        Map<String, Command> subCommandIndex = new HashMap<>();

        if (currentCommand.getSubCommands() != null)
            currentCommand.getSubCommands().forEach(c -> subCommandIndex.put(c.getName(), c));

        Map<String, Option> optionMap = new HashMap<>();
        Map<String, Option> longOptionMap = new HashMap<>();

        if (currentCommand.getOptions() != null) {
            currentCommand.getOptions().forEach(o -> optionMap.put(o.getName(), o));
            currentCommand.getOptions().forEach(o -> longOptionMap.put(o.getLongName(), o));
        }

        CliCommand subCommand = null;

        //Process each argument.
        for (int i = 0; i < args.length; i++) {
            //get the argument from the array.
            String arg = args[i];

            //Process a reference.
            if (arg.startsWith("-")) {
                //Default to a short reference.
                int trimIndex = 1;
                Map<String, Option> indexMap = optionMap;

                //Check to see if its a long reference.  If it is, then adjust to trim two spaces and send in a different map.
                if (arg.startsWith("--")) {
                    trimIndex = 2;
                    indexMap = longOptionMap;
                }

                //Parse the
                Pair<String, CliOption> argumentPair = this.parseArgument(arg.substring(trimIndex), args, i, indexMap);

                if (argumentPair != null) {
                    readArgumentMap.put(argumentPair.getKey(), argumentPair.getValue());
                    if (argumentPair.getValue().getReference().isExpectParameter())
                        i++;
                }
            }
            //If this is a new commend, then read the rest of the args as this command.
            else if (subCommandIndex.containsKey(arg)) {
                subCommand = this.read(Arrays.copyOfRange(args, i + 1, args.length), subCommandIndex.get(arg));
                break;
            }
            //Its an additional argument.
            else if (
                //This is needed so that a named root command is not picked up as an additional argument.
                    !arg.equals(currentCommand.getName()) &&
                            !subCommandIndex.containsKey(arg)
                    ) {
                additionalArgs.add(arg);
            }
        }

        CliCommand cliCommand = new CliCommand();
        cliCommand.setReference(currentCommand);
        cliCommand.setSubCommand(subCommand);
        cliCommand.setReadOptions(readArgumentMap);
        cliCommand.setAdditionalArgs(additionalArgs);
        return cliCommand;
    }

    private Pair<String, CliOption> parseArgument(String arg, String[] args, int index, Map<String, Option> optionMap) {
        String optionName = arg, optionValue = null;

        if (arg.contains("=")) {
            optionName = arg.split("=")[0];
        }

        Option foundOption = optionMap.get(optionName);

        if (foundOption == null)
            return null;

        if (foundOption.isExpectParameter()) {
            if (arg.contains("=")) {
                optionValue = arg.split("=")[1];
            } else {
                optionValue = args[index + 1];
            }
        }

        CliOption cliOption = new CliOption();
        cliOption.setReference(foundOption);
        cliOption.setValue(optionValue);

        return new Pair<>(foundOption.getName(), cliOption);
    }


}
