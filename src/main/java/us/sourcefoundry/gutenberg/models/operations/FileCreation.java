package us.sourcefoundry.gutenberg.models.operations;

import us.sourcefoundry.gutenberg.models.BuildContext;
import us.sourcefoundry.gutenberg.models.forme.FileEntry;
import us.sourcefoundry.gutenberg.models.templates.FileTemplate;
import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.models.templates.StringTemplate;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class FileCreation implements FileSystemOperation<FileEntry> {

    @Inject
    private Console console;

    @Override
    public void execute(FileEntry fileSystemObject, BuildContext buildContext) {
        //Create the variable map.
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", buildContext.getForme());
            put("user", buildContext.getUserResponses());
            put("variables", fileSystemObject.getVariables());
        }};

        //Build the full path to the destination.
        String destFilePath =
                (new FormattedStringTemplate("{0}/{1}", buildContext.getBuildLocation().getPath(), fileSystemObject.getDest())).create(variables);

        //Create the file from a source file template.
        if (fileSystemObject.isFileSourced()) {
            this.createFromSourceFile(fileSystemObject, buildContext.getFormeLocation().getPath(), destFilePath, variables);
            return;
        }
        //Create the file from the content provided in the forme file.
        this.createFromSourceContent(fileSystemObject, destFilePath, variables);
    }

    /**
     * Creates a new file from a source file template.
     *
     * @param formeLocation The location of the forme file used for determining the location of the form source file.
     * @param destFilePath  The location to build the file to.
     * @param variables     Any variables to use for templating.
     */
    private void createFromSourceFile(FileEntry fileSystemObject, String formeLocation, String destFilePath, Map<String, Object> variables) {
        //Build the full path to the Mustache template.
        String sourceFilePath =
                (new FormattedStringTemplate("{0}/{1}", formeLocation, fileSystemObject.getSource())).create(variables);

        //Since this is a event, tell the user.
        this.console.info("+ Creating File... {0}", destFilePath);
        //Create the file.
        (new FileTemplate()).create(sourceFilePath, destFilePath, fileSystemObject.getPermissions(), variables);
    }

    /**
     * Creates a new file from the content of the file entry in the forme file.
     *
     * @param destFilePath The location to build the file to.
     * @param variables    Any variables to use for templating.
     */
    private void createFromSourceContent(FileEntry fileSystemObject, String destFilePath, Map<String, Object> variables) {
        //Build the string template from the specified content.
        StringReader reader = new StringReader((new StringTemplate(fileSystemObject.getContent())).create(variables));

        //Since this is a event, tell the user.
        this.console.info("\t+ Creating File... {0}", destFilePath);
        //Create the file.
        (new FileTemplate()).create(reader, fileSystemObject.getPermissions(), destFilePath, variables);
    }
}
