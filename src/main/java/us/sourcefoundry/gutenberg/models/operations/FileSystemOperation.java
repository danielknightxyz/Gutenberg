package us.sourcefoundry.gutenberg.models.operations;

import us.sourcefoundry.gutenberg.models.BuildContext;

public interface FileSystemOperation<T> {

    void execute(T fileSystemObject, BuildContext buildContext);
}
