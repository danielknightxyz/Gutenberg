package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.CliService;

/**
 * Creates a context object for the application.
 */
public class ApplicationContextFactory extends AbstractFactory<ApplicationContext> {

    /**
     * Creates a new instance.
     *
     * @return ApplicationContext
     */
    public ApplicationContext newInstance() {
        return this.getInstance(ApplicationContext.class);
    }

    /**
     * Creates a new instance from options in the CliService.
     *
     * @param cliService The command line.
     * @return ApplicationContext
     */
    public ApplicationContext newInstance(CliService cliService) {
        //Get some details about the system.
        String workingDir = System.getProperty("user.dir");

        String userSpecifiedCommand = "";
        //Get the action specified by the user. This is the first subcommand of of the root.
        if (cliService.getRootCommand().getSubCommand() != null)
            userSpecifiedCommand = cliService.getRootCommand().getSubCommand().getReference().getName();

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = this.newInstance();
        applicationContext.setCommand(userSpecifiedCommand);
        applicationContext.setWorkingDirectory(workingDir);

        return applicationContext;
    }
}
