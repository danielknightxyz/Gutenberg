package us.sourcefoundry.gutenberg.services;

import lombok.Getter;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.exceptions.UnknownArgumentException;
import us.sourcefoundry.gutenberg.services.commandcli.models.Command;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;
import us.sourcefoundry.gutenberg.services.commandcli.services.CliReader;

import javax.inject.Singleton;
import java.util.ArrayList;

/**
 * CliService
 *
 * @author Daniel Knight <daniel.knight@creditcards.com>
 */
@Singleton
@Getter
public class CliService {

    //The cli options.
    private Command cliOptions;
    //Set the commandline interface object.
    private CliCommand rootCommand;

    /**
     * Constructor
     */
    public CliService() {
    }

    /**
     * Load
     *
     * @param args ApplicationManager arguments.
     */
    public void load(String[] args) {
        //Lets build the commands.
        Command command = Command.builder()
                .options(
                        new ArrayList<Option>() {{
                            add(Option.builder().name("h").longName("help").description("Shows this message.").build());
                            add(Option.builder().name("v").longName("version").description("Shows current version.").build());
                        }})
                .subCommands(
                        new ArrayList<Command>() {{
                            add(Command.builder().name("build").description("Build a forme.").documented(true)
                                    .options(new ArrayList<Option>() {{
                                        add(Option.builder().name("h").longName("help").description("Shows this message.").build());
                                        add(Option.builder().name("a").longName("answersfile").description("The location of an answers file.").expectParameter(true).parameterName("path").build());
                                        add(Option.builder().name("s").longName("saveanswers").description("Save the answers to any prompts.").expectParameter(true).parameterName("path").build());
                                        add(Option.builder().name("o").longName("output").description("The location to build the forme.").expectParameter(true).parameterName("path").build());
                                        add(Option.builder().name("l").longName("local").description("Path of the directory containing the forme file and source/template resources.").expectParameter(true).parameterName("path").build());
                                        add(Option.builder().name("f").longName("force").description("Force the build.").build());
                                    }}).build());
                            add(Command.builder().name("init").description("Initialize a new forme file here.").documented(true)
                                    .options(
                                            new ArrayList<Option>() {{
                                                add(Option.builder().name("h").longName("help").description("Shows this message.").build());
                                                add(Option.builder().name("f").longName("force").description("Overwrites the existing forme file.").build());
                                            }}
                                    )
                                    .build());
                            add(Command.builder().name("add").description("Add a forme from a GitHub repository.").documented(true)
                                    .options(
                                            new ArrayList<Option>() {{
                                                add(Option.builder().name("h").longName("help").description("Shows this message.").build());
                                            }}
                                    )
                                    .build());
                            add(Command.builder().name("list").description("List installed formes.").documented(true).build());
                            add(Command.builder().name("remove").description("Remove all formes from inventory").documented(false).build());
                        }}
                )
                .build();

        //Get the options values from the command line.
        this.rootCommand = this.buildCLI(command, args);
        this.cliOptions = command;
    }

    /**
     * Does the root command have the version.
     *
     * @return boolean
     */
    public boolean hasVersion() {
        return this.rootCommand.hasOption("v");
    }

    /**
     * Notifier Version
     */
    public void printVersion() {
        System.out.println(
                "Gutenberg " + (CliService.class.getPackage().getImplementationVersion() != null ?
                        CliService.class.getPackage().getImplementationVersion() :
                        "Unreleased")
        );
    }

    /**
     * Build CliService
     *
     * @param options The options object for the application.
     * @param args    The command line arguments for the application.
     * @return CommandLine
     */
    private CliCommand buildCLI(Command options, String[] args) {
        return (new CliReader()).read(args, options);
    }
}
