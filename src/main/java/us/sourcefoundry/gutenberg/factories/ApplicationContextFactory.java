package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.services.Cli;

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
     * Creates a new instance from options in the Cli.
     *
     * @param cli The command line.
     * @return ApplicationContext
     */
    public ApplicationContext newInstance(Cli cli) {
        //Get some details about the system.
        String workingDir = System.getProperty("user.dir");

        //Get the action specified by the user.
        String userSpecifiedCommand = (cli.getArgList().size() > 0 ? cli.getArgList().get(0).toString() : "");

        //Create an application context for use later in the process.
        ApplicationContext applicationContext = this.newInstance();
        applicationContext.setCommand(userSpecifiedCommand);
        applicationContext.setWorkingDirectory(workingDir);

        return applicationContext;
    }
}
