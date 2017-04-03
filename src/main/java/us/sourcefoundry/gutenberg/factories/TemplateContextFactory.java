package us.sourcefoundry.gutenberg.factories;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import us.sourcefoundry.gutenberg.models.FormeContext;

import java.io.InputStream;

public class TemplateContextFactory {

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
