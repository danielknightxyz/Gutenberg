package us.sourcefoundry.gutenberg.models;

import javax.inject.Singleton;

/**
 * This class provides some information about the environment the application is running.
 */
@Singleton
public class ApplicationContext {

    //The command provided in the command line.
    private String command;
    //The location of the .gutenberg folder with the inventory and formes.
    private String installDirectory = System.getProperty("user.home") + "/.gutenberg";
    //The current directory the application was invoked.
    private String workingDirectory;

    /**
     * Get the command.
     *
     * @return String
     */
    public String getCommand() {
        return command;
    }

    /**
     * Set the command.
     *
     * @param command String
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Get the install directory.
     *
     * @return String
     */
    public String getInstallDirectory() {
        return installDirectory;
    }

    /**
     * Get the working directory.
     *
     * @return String
     */
    public String getWorkingDirectory() {
        return workingDirectory;
    }

    /**
     * Set the working directory.
     *
     * @param workingDirectory String
     */
    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }
}
