package us.sourcefoundry.gutenberg.commands.build.errors;

public class UnEmptyDirectory implements IBuildError {

    @Override
    public String getDescription() {
        return "Could not build. {0} exists and is not empty.";
    }
}
