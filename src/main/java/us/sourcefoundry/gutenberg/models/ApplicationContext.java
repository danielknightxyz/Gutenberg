package us.sourcefoundry.gutenberg.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.inject.Singleton;

/**
 * This class provides some information about the environment the application is running.
 */
@Singleton
@Getter
@Setter
@NoArgsConstructor
public class ApplicationContext {

    //The command provided in the command line.
    private String command;
    //The location of the .gutenberg folder with the inventory and formes.
    private String installDirectory = System.getProperty("user.home") + "/.gutenberg";
    //The current directory the application was invoked.
    private String workingDirectory;

}
