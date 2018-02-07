package us.sourcefoundry.gutenberg.services.commandcli.services;

import lombok.NoArgsConstructor;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.models.CliOption;
import us.sourcefoundry.gutenberg.services.commandcli.models.Command;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;
import us.sourcefoundry.gutenberg.utils.Pair;

import java.util.*;

/**
 * This is the command line reader.  This class is responsible for reading the user provided arguments and matching them up
 * to command and command options defined for the application.
 */
@NoArgsConstructor
public class CliReader {

    public CliCommand read(String[] args, Command command) {

        Map<String, CliOption> readArgumentMap = new HashMap<>();
        List<String> additionalArgs = new ArrayList<>();

        //Index the sub-commands so that if one is encountered, it will be recognized and then processed.
        Map<String, Command> subCommandIndex = new HashMap<>();

        if (command.getSubCommands() != null)
            command.getSubCommands().forEach(c -> subCommandIndex.put(c.getName(), c));

        //The indexed options.
        Map<String, Option> optionMap = new HashMap<>();
        //Need to be able to lookup options based off long option names.
        Map<String, Option> longOptionMap = new HashMap<>();

        //Perform the indexing of the options.
        if (command.getOptions() != null)
            command.getOptions().forEach(o -> {
                optionMap.put(o.getName(), o);
                longOptionMap.put(o.getLongName(), o);
            });

        //Place holder for any subcommand that is read.
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

                //Parse the argument.
                Pair<String, CliOption> argumentPair = this.parseArgument(arg.substring(trimIndex), args, i, indexMap);

                //If the par is null, don't put it in the read arg map.
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
                    !arg.equals(command.getName()) &&
                            !subCommandIndex.containsKey(arg)
                    ) {
                additionalArgs.add(arg);
            }
        }

        //Create a cli command.
        CliCommand cliCommand = new CliCommand();
        cliCommand.setReference(command);
        cliCommand.setSubCommand(subCommand);
        cliCommand.setReadOptions(readArgumentMap);
        cliCommand.setAdditionalArgs(additionalArgs);
        return cliCommand;
    }

    /**
     * This will parse and argument as an cli option.
     *
     * @param arg       The argument value.
     * @param args      The set of arguments for the application.
     * @param index     The index of argument.
     * @param optionMap The option map to use to lookup the reference.
     * @return Pair
     */
    private Pair<String, CliOption> parseArgument(String arg, String[] args, int index, Map<String, Option> optionMap) {
        //Default the option name to the argument
        String optionName = arg,
                //Default the value to null.
                optionValue = null;

        //Look for a equal sign.  This indicates that the option was provided a vlue.
        if (arg.contains("="))
            optionName = arg.split("=")[0];


        //Look up the option reference.
        Option foundOption = optionMap.get(optionName);

        //If the option reference is not found then there's nothing to do, return a null value.
        if (foundOption == null)
            return null;

        //If the option reference is expecting a value, then this will extract it.
        if (foundOption.isExpectParameter())
            //Look for the equals.
            if (arg.contains("="))
                optionValue = arg.split("=")[1];
            //If the equals was not found, then assume (since we are expecting a parameter) that the next argument is the value.
            else
                optionValue = args[index + 1];

        //Create an option with the ref and value.
        CliOption cliOption = new CliOption();
        cliOption.setReference(foundOption);
        cliOption.setValue(optionValue);

        //Return the Name/Value pair.
        return new Pair<>(foundOption.getName(), cliOption);
    }


}
