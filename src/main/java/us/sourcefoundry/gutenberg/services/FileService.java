package us.sourcefoundry.gutenberg.services;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import us.sourcefoundry.gutenberg.models.ApplicationContext;
import us.sourcefoundry.gutenberg.models.FileTemplateEntry;
import us.sourcefoundry.gutenberg.models.FormeContext;
import us.sourcefoundry.gutenberg.utils.SystemPathGenerator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.UUID;

public class FileService {

    private ApplicationContext applicationContext;
    private FormeContext formeContext;

    public FileService(ApplicationContext applicationContext, FormeContext formeContext) {
        this.applicationContext = applicationContext;
        this.formeContext = formeContext;
    }

    public void createFromTemplate(FileTemplateEntry fileTemplateEntry) {
        try {
            String sourceFilePath =
                    (new SystemPathGenerator(this.applicationContext, this.formeContext)).create("{0}/{1}", applicationContext.getSourceDirectory(), fileTemplateEntry.getSource());
            String destFilePath =
                    (new SystemPathGenerator(this.applicationContext, this.formeContext)).create("{0}/{1}", applicationContext.getOutputDirectory(), fileTemplateEntry.getName());

            System.out.println(MessageFormat.format("Creating File from Template... {0}",destFilePath));


            HashMap<String, Object> variables = new HashMap<String, Object>() {{
                put("forme", formeContext);
                put("variables", fileTemplateEntry.getVariables());
                put("user", applicationContext.getUserResponses());
            }};

            PrintWriter writer = new PrintWriter(destFilePath);
            MustacheFactory mf = new DefaultMustacheFactory();
            Mustache mustache = mf.compile(new FileReader(sourceFilePath), UUID.randomUUID().toString());
            mustache.execute(writer, variables);
            writer.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
