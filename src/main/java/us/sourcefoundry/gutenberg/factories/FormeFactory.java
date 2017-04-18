package us.sourcefoundry.gutenberg.factories;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.models.forme.Forme;

import java.io.InputStream;

/**
 * Reads the settings file from an input streamFile and hydrates a object model with the values from that file.
 */
public class FormeFactory extends AbstractFactory<Forme> {

    /**
     * Creates a new instance of Forme.
     *
     * @return Forme
     */
    public Forme newInstance() {
        return this.getInstance(Forme.class);
    }

    /**
     * Makes an object hydrated with the values from the forme file.
     *
     * @param settingFile The inputstream from the file.
     * @return Forme Object
     */
    public Forme newInstance(InputStream settingFile) {
        try {
            //Get the property handler.
            Yaml parser = new Yaml(
                    new Constructor(Forme.class)
            );
            return (Forme) parser.load(settingFile);
        } catch (Throwable e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}
