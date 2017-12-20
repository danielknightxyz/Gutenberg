package us.sourcefoundry.gutenberg.commands.build.warnings;

public class PreexistingDirectory implements IBuildWarning {

    @Override
    public String getDescription() {
        return "{0} already exists. Building anyways.";
    }
}
