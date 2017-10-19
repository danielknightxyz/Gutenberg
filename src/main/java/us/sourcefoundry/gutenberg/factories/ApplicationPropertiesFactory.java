package us.sourcefoundry.gutenberg.factories;

import us.sourcefoundry.gutenberg.config.ApplicationProperties;

import java.io.IOException;
import java.io.InputStream;

/**
 * Creates a context object for the application.
 */
public class ApplicationPropertiesFactory extends AbstractFactory<ApplicationProperties> {

    /**
     * Creates a new instance.
     *
     * @return ApplicationContext
     */
    public ApplicationProperties newInstance() {
        return this.getInstance(ApplicationProperties.class);
    }

    /**
     * Creates a new instance from options in the Cli.
     *
     * @param filename The name of the properites file.
     * @return ApplicationContext
     */
    public ApplicationProperties newInstance(String filename) {
        ApplicationProperties properties = this.newInstance();

        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
            properties.load(is);
            is.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return properties;
    }
}
