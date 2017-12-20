package us.sourcefoundry.gutenberg.commands.build.errors;

public class CouldNotMakeDirectory implements IBuildError {

    @Override
    public String getDescription() {
        return "{0} did not exist and could not be created.";
    }
}
