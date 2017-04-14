package us.sourcefoundry.gutenberg.services;

import org.apache.commons.cli.*;

import javax.inject.Singleton;
import java.util.List;

/**
 * Cli
 *
 * @author Daniel Knight <daniel.knight@creditcards.com>
 */
@Singleton
public class Cli {

    //The cli options.
    private Options cliOptions;
    //Set the commandline interface object.
    private CommandLine commandLine;

    /**
     * Constructor
     */
    public Cli() {
    }

    /**
     * Constructor
     */
    public Cli(String[] args) {
        this.load(args);
    }

    /**
     * Load
     *
     * @param args ApplicationManager arguments;\.
     */
    public void load(String[] args) {
        //Build the CLI options.
        Options options = this.buildCLIOptions();

        try {
            //Get the options values from the command line.
            this.commandLine = this.buildCLI(options, args);
            this.cliOptions = options;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Has Option
     *
     * @param option The name of the option.
     * @return boolean
     */
    public boolean hasOption(String option) {
        return commandLine.hasOption(option);
    }

    /**
     * Get Option Value
     *
     * @param option The name of the option.
     * @return The value of the option.
     */
    public String getOptionValue(String option) {
        return commandLine.getOptionValue(option);
    }

    /**
     * Print Help
     */
    private void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(250);
        String header = "\nActions: init, build <forme>, add <user/repo:ref>, list\nOptions:";
        String footer = "\nPlease visit https://github.com/sourcefoundryus/gutenberg for more information.";
        formatter.printHelp("gutenberg [ACTION] [ARG...]", header, cliOptions, footer, true);
    }

    /**
     * Notifier Version
     */
    private void printVersion() {
        System.out.println(
                "Gutenberg " + (Cli.class.getPackage().getImplementationVersion() != null ?
                        Cli.class.getPackage().getImplementationVersion() :
                        "Unreleased")
        );
    }

    /**
     * Build Cli Options
     *
     * @return Options
     */
    private Options buildCLIOptions() {
        Option help = OptionBuilder.withLongOpt("help").withDescription("Prints this message.").create("h");
        Option force = OptionBuilder.withLongOpt("force").withDescription("Force the action to complete.").create("f");
        Option version = OptionBuilder.withLongOpt("version").withDescription("Get Version").create("v");

        Option localPath = OptionBuilder
                .withLongOpt("local")
                .withArgName("path")
                .hasArg()
                .withDescription("Path of the directory containing the forme file and source/template resources.")
                .create();

        Option outputPath = OptionBuilder
                .withLongOpt("output")
                .withArgName("path")
                .hasArg()
                .withDescription("Path of the directory in which to build the output.")
                .create("o");

        Option saveAnswers = OptionBuilder
                .withLongOpt("saveanswers")
                .withArgName("path to save file")
                .hasArg()
                .withDescription("Save the answers to any prompts.")
                .create("s");

        Option answersFile = OptionBuilder
                .withLongOpt("answersfile")
                .withArgName("path to answers file")
                .hasArg()
                .withDescription("Path to the answers file.")
                .create("a");

        OptionGroup answersOptionGroup = new OptionGroup();
        answersOptionGroup.addOption(saveAnswers);
        answersOptionGroup.addOption(answersFile);

        Options options = new Options();
        options.addOption(help);
        options.addOption(force);
        options.addOption(version);
        options.addOption(localPath);
        options.addOption(outputPath);
        options.addOptionGroup(answersOptionGroup);

        return options;
    }

    /**
     * Build Cli
     *
     * @param options The options object for the application.
     * @param args    The command line arguments for the application.
     * @return CommandLine
     * @throws ParseException
     */
    private CommandLine buildCLI(Options options, String[] args) throws ParseException {
        CommandLineParser parser = new GnuParser();
        return parser.parse(options, args);
    }

    /**
     * Check if any blocking options have been passed to Cli
     *
     * @return boolean
     */
    public boolean hasBlockingOption() {
        boolean hasHelp = this.hasHelp();
        boolean hasVersion = this.hasVersion();

        //If the want the version, display the version information and exit.
        // This will only show with a package built with maven.
        return hasVersion || hasHelp;

    }

    /**
     * Print a blocking option if there is one
     */
    public void printBlockingOption() {

        //If we have a help request, display the help and nothing else.
        if (hasHelp()) {
            this.printHelp();
        }

        //If the want the version, display the version information and exit.
        // This will only show with a package built with maven.
        if (hasVersion()) {
            this.printVersion();
        }
    }

    /**
     * Has Version
     *
     * @return boolean
     */
    public boolean hasVersion() {
        //Check for the version request.
        return this.hasOption("version");
    }

    /**
     * Has Help
     *
     * @return boolean
     */
    public boolean hasHelp() {
        //Check for the help request.
        return (this.hasOption("h") || this.hasOption("help"));
    }

    /**
     * Get the remaining argument list from the command.
     *
     * @return List
     */
    public List getArgList() {
        return this.commandLine.getArgList();
    }
}
