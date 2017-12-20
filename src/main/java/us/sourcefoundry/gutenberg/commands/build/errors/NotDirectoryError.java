package us.sourcefoundry.gutenberg.commands.build.errors;

public class NotDirectoryError implements IBuildError {

    @Override
    public String getDescription() {
        return "Could not build. {0} exists and is not a directory.";
    }
}
