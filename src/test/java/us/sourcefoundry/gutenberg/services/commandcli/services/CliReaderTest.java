package us.sourcefoundry.gutenberg.services.commandcli.services;

import org.junit.Test;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;
import us.sourcefoundry.gutenberg.services.commandcli.models.Command;
import us.sourcefoundry.gutenberg.services.commandcli.models.Option;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class CliReaderTest {

    @Test
    public void testReadBasicOption() {

        //The argument.
        String[] args = new String[1];
        args[0] = "-o";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
    }

    @Test
    public void testReadLongOption() {

        //The argument.
        String[] args = new String[1];
        args[0] = "--reference";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").longName("reference").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
    }

    @Test
    public void testReadLongOptionWithValue() {

        //The argument.
        String[] args = new String[2];
        args[0] = "--reference";
        args[1] = "value";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").expectParameter(true).longName("reference").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertEquals("value", cliCommand.getOption("o").getValue());
    }

    @Test
    public void testReadLongOptionWithValueWithEquals() {

        //The argument.
        String[] args = new String[1];
        args[0] = "--reference=value";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").expectParameter(true).longName("reference").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertEquals("value", cliCommand.getOption("o").getValue());
    }

    @Test
    public void testReadBasicWithValueOption() {

        //The argument.
        String[] args = new String[1];
        args[0] = "-o=value";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").expectParameter(true).parameterName("field").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertEquals("value", cliCommand.getOption("o").getValue());
    }

    @Test
    public void testReadBasicWithValueAndSpaceDelimiterOption() {

        //The argument.
        String[] args = new String[2];
        args[0] = "-o";
        args[1] = "value";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").expectParameter(true).parameterName("field").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertEquals("value", cliCommand.getOption("o").getValue());
    }

    @Test
    public void testReadBasicWithValueAndSpaceDelimiterAndSecondOption() {

        //The argument.
        String[] args = new String[3];
        args[0] = "-o";
        args[1] = "value";
        args[2] = "-h";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("o").expectParameter(true).parameterName("field").build());
        optionList.add(Option.builder().name("h").build());

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertTrue(cliCommand.hasOption("h"));
        assertEquals("value", cliCommand.getOption("o").getValue());
    }

    @Test
    public void testReadBasicWithAdditionalArgsOption() {

        //The argument.
        String[] args = new String[2];
        args[0] = "-o";
        args[1] = "something-more";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        Option option = Option.builder().name("o").build();
        optionList.add(option);

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertEquals(1, cliCommand.getAdditionalArgs().size());
        assertEquals("something-more", cliCommand.getAdditionalArgs().get(0));
    }

    @Test
    public void testReadBasicWithValueAndSpaceDelimiterAndSecondAndAdditionalArgOption() {

        //The argument.
        String[] args = new String[4];
        args[0] = "-o";
        args[1] = "value";
        args[3] = "something-more";
        args[2] = "-h";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("o").expectParameter(true).parameterName("field").build());
        optionList.add(Option.builder().name("h").build());

        Command command = Command.builder().options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertTrue(cliCommand.hasOption("o"));
        assertTrue(cliCommand.hasOption("h"));
        assertEquals("value", cliCommand.getOption("o").getValue());
        assertEquals(1, cliCommand.getAdditionalArgs().size());
        assertEquals("something-more", cliCommand.getAdditionalArgs().get(0));
    }

    @Test
    public void testUnnamedRootCommand() {
        //The argument.
        String[] args = new String[0];

        Command command = Command.builder().build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertEquals(command, cliCommand.getReference());
        assertEquals(null, cliCommand.getReference().getName());
    }

    @Test
    public void testNamedRootCommand() {
        //The argument.
        String[] args = new String[1];
        args[0] = "build";

        Command command = Command.builder().name("build").build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertEquals(command, cliCommand.getReference());
        assertEquals("build", cliCommand.getReference().getName());
    }

    @Test
    public void testNamedRootCommandWithOption() {
        //The argument.
        String[] args = new String[2];
        args[0] = "build";
        args[1] = "-o";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("o").build());

        Command command = Command.builder().name("build").options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertEquals(command, cliCommand.getReference());
        assertEquals("build", cliCommand.getReference().getName());
        assertTrue(cliCommand.hasOption("o"));
    }

    @Test
    public void testNamedRootCommandWithOptionAndExtraArgs() {
        //The argument.
        String[] args = new String[3];
        args[0] = "build";
        args[1] = "-o";
        args[2] = "something-more";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("o").build());

        Command command = Command.builder().name("build").options(optionList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, command);

        assertEquals(command, cliCommand.getReference());
        assertEquals("build", cliCommand.getReference().getName());
        assertTrue(cliCommand.hasOption("o"));
        assertEquals(1, cliCommand.getAdditionalArgs().size());
        assertEquals("something-more", cliCommand.getAdditionalArgs().get(0));
    }

    @Test
    public void testNamedRootCommandWithRootOptionAndSubCommandAndExtraArgs() {
        //The argument.
        String[] args = new String[2];
        args[0] = "-h";
        args[1] = "build";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("h").build());

        List<Command> subCommandList = new ArrayList<>();
        Command subCommand = Command.builder().name("build").build();
        subCommandList.add(subCommand);

        Command rootCommand = Command.builder().options(optionList).subCommands(subCommandList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, rootCommand);

        assertEquals(rootCommand, cliCommand.getReference());
        assertEquals(null, cliCommand.getReference().getName());
        assertTrue(cliCommand.hasOption("h"));
        assertEquals(subCommand, cliCommand.getSubCommand().getReference());
        assertEquals("build", cliCommand.getSubCommand().getReference().getName());
    }

    @Test
    public void testNamedRootCommandWithRootOptionAndSubCommandWithOptionsAndExtraArgs() {
        //The argument.
        String[] args = new String[3];
        args[0] = "-h";
        args[1] = "build";
        args[2] = "-o";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("h").build());

        List<Command> subCommandList = new ArrayList<>();
        Command subCommand = Command.builder().name("build").options(
                new ArrayList<Option>() {{
                    add(Option.builder().name("o").build());
                }}
        ).build();
        subCommandList.add(subCommand);

        Command rootCommand = Command.builder().options(optionList).subCommands(subCommandList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, rootCommand);

        assertEquals(rootCommand, cliCommand.getReference());
        assertEquals(null, cliCommand.getReference().getName());
        assertTrue(cliCommand.hasOption("h"));
        assertFalse(cliCommand.hasOption("o"));
        assertEquals(subCommand, cliCommand.getSubCommand().getReference());
        assertEquals("build", cliCommand.getSubCommand().getReference().getName());
        assertTrue(cliCommand.getSubCommand().hasOption("o"));
    }

    @Test
    public void testNamedRootCommandWithRootOptionAndSubCommandWithOptionsWithValueAndExtraArgs() {
        //The argument.
        String[] args = new String[3];
        args[0] = "-h";
        args[1] = "build";
        args[2] = "-o=test";

        //Stand up some expected options.
        List<Option> optionList = new ArrayList<>();
        optionList.add(Option.builder().name("h").build());

        List<Command> subCommandList = new ArrayList<>();
        Command subCommand = Command.builder().name("build").options(
                new ArrayList<Option>() {{
                    add(Option.builder().name("o").expectParameter(true).build());
                }}
        ).build();
        subCommandList.add(subCommand);

        Command rootCommand = Command.builder().options(optionList).subCommands(subCommandList).build();

        //Read the arguments.
        CliReader reader = new CliReader();
        CliCommand cliCommand = reader.read(args, rootCommand);

        assertEquals(rootCommand, cliCommand.getReference());
        assertEquals(null, cliCommand.getReference().getName());
        assertTrue(cliCommand.hasOption("h"));
        assertFalse(cliCommand.hasOption("o"));
        assertEquals(subCommand, cliCommand.getSubCommand().getReference());
        assertEquals("build", cliCommand.getSubCommand().getReference().getName());
        assertTrue(cliCommand.getSubCommand().hasOption("o"));
        assertEquals("test",cliCommand.getSubCommand().getOption("o").getValue());
    }

}