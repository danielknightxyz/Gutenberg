package us.sourcefoundry.gutenberg.factories;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.models.FormeContext;

import java.io.InputStream;

/**
 * Reads the settings file from an input streamFile and hydrates a object model with the values from that file.
 */
public class TemplateContextFactory {

    /**
     * Makes an object hydrated with the values from the forme file.
     * @param settingFile The inputstream from the file.
     * @return FormeContext Object
     */
    public FormeContext make(InputStream settingFile) {
        try {
            //Get the property handler.
            Yaml parser = new Yaml(
                    new Constructor(FormeContext.class)
            );
            return (FormeContext) parser.load(settingFile);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
