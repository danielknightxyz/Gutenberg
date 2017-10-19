package us.sourcefoundry.gutenberg.models.operations;

import us.sourcefoundry.gutenberg.models.BuildContext;
import us.sourcefoundry.gutenberg.models.forme.DirectoryEntry;
import us.sourcefoundry.gutenberg.models.templates.FormattedStringTemplate;
import us.sourcefoundry.gutenberg.services.Console;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import javax.inject.Inject;
import java.util.HashMap;

public class DirectoryCreation implements FileSystemOperation<DirectoryEntry> {

    @Inject
    private Console console;

    @Override
    public void execute(DirectoryEntry directoryEntry, BuildContext buildContext) {
        //Create the variable map.
        HashMap<String, Object> variables = new HashMap<String, Object>() {{
            put("forme", buildContext.getForme());
            put("user", buildContext.getUserResponses());
        }};

        //Build the expected destination the directory.
        String destination =
                (new FormattedStringTemplate(
                        "{0}/{1}", buildContext.getBuildLocation().getPath(), directoryEntry.getDest()))
                        .create(variables);

        //Since this is a event, tell the user.
        this.console.info("\t+ Creating Directory... {0}", destination);
        //Create the directory use the file system service.
        (new FileSystemService()).createDirectory(destination);
    }
}
