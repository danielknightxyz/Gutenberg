package us.sourcefoundry.gutenberg.models.operations;

import us.sourcefoundry.gutenberg.models.BuildContext;
import us.sourcefoundry.gutenberg.models.forme.CopyEntry;
import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.FileSystemService;
import us.sourcefoundry.gutenberg.services.console.Console;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;

public class FileCopy implements FileSystemOperation<CopyEntry> {

    @Inject
    private Console console;

    @Override
    public void execute(CopyEntry fileSystemObject, BuildContext buildContext) {
        //Prepare the variables map.
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", buildContext.getForme());
            put("user", buildContext.getUserResponses());
        }};

        //Generate the paths to the for the source and destination.
        String sourcePath = (new FormattedStringTemplate("{0}/{1}", buildContext.getFormeLocation().getPath(), fileSystemObject.getSource())).create(variables);
        String destinationPath = (new FormattedStringTemplate("{0}/{1}", buildContext.getBuildLocation().getPath(), fileSystemObject.getDest())).create(variables);

        //If this is a file that needs to be copied.
        this.copy(fileSystemObject, sourcePath, destinationPath);
    }

    /**
     * Copies a file.
     *
     * @param sourcePath      The location of the source file.
     * @param destinationPath The location to copy the file.
     */
    private void copy(CopyEntry fileSystemObject, String sourcePath, String destinationPath) {
        try {
            this.console.info("+ Copying File... {0}", destinationPath);
            (new FileSystemService()).copyFile(sourcePath, destinationPath, fileSystemObject.getPermissions().canRead(), fileSystemObject.getPermissions().canWrite(), fileSystemObject.getPermissions().canExecute());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
