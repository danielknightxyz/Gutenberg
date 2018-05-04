package us.sourcefoundry.gutenberg.models;

import lombok.Getter;
import lombok.Setter;
import us.sourcefoundry.gutenberg.services.commandcli.CliCommand;

/**
 * This represents the location of the generated files should go.
 */
@Getter
@Setter
public class BuildLocation {

    //The location of the file
    private String path;

    /**
     * Constructor
     *
     * @param path The path to the output directory.
     */
    private BuildLocation(String path) {
        this.path = path;
    }

    /**
     * Build the build location from the command line.
     *
     * @param cli                THe command line.
     * @param applicationContext The context for the application.
     * @return BuildLocation
     */
    public static BuildLocation fromCliCommand(CliCommand cli, ApplicationContext applicationContext) {
        if (cli.hasOption("o"))
            //Get local forme.
            return new BuildLocation(cli.getOption("o").getValue());

        //If an output directory is not provided in the commandline, then just assume they mean the current working
        //directory.
        return new BuildLocation(applicationContext.getWorkingDirectory());
    }
}
