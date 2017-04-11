package us.sourcefoundry.gutenberg.models.templates;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

/**
 * This allows you to create a new file from a template file.
 */
public class FileTemplate {

    /**
     * Create a new file from a template.
     *
     * @param sourceTemplatePath The path to the source template.
     * @param destinationPath    The expected path of the created file.
     * @param variables          Any variables to use in the template.
     * @return True if it completed without error.  False otherwise.
     */
    public boolean create(String sourceTemplatePath, String destinationPath, HashMap<String, Object> variables) {
        try {
            return this.create(new FileReader(sourceTemplatePath), destinationPath, variables);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Create a new file from a template.
     *
     * @param sourceReader    The reader from which to read the source content.
     * @param destinationPath The expected path of the created file.
     * @param variables       Any variables to use in the template.
     * @return True if it completed without error.  False otherwise.
     */
    public boolean create(Reader sourceReader, String destinationPath, HashMap<String, Object> variables) {
        try {
            PrintWriter writer = new PrintWriter((new FileSystemService()).getByLocation((destinationPath)));
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(sourceReader, UUID.randomUUID().toString());
            mustache.execute(writer, variables);
            writer.flush();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
