package us.sourcefoundry.gutenberg.commands.build;

import lombok.Getter;
import lombok.Setter;
import us.sourcefoundry.gutenberg.commands.build.errors.CouldNotMakeDirectory;
import us.sourcefoundry.gutenberg.commands.build.errors.IBuildError;
import us.sourcefoundry.gutenberg.commands.build.errors.NotDirectoryError;
import us.sourcefoundry.gutenberg.commands.build.errors.UnEmptyDirectory;
import us.sourcefoundry.gutenberg.commands.build.warnings.IBuildWarning;
import us.sourcefoundry.gutenberg.commands.build.warnings.PreexistingDirectory;
import us.sourcefoundry.gutenberg.models.BuildLocation;
import us.sourcefoundry.gutenberg.services.FileSystemService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class BuildPathChecker {

    public List<IBuildError> errors = new ArrayList<>();
    public List<IBuildWarning> warnings = new ArrayList<>();

    /**
     * This function will check the build path and make sure its available for use as a build location.
     *
     * @param buildLocation The location to build.
     * @param force         Should the build location be used regardless of readiness.
     * @return boolean
     */
    public boolean check(BuildLocation buildLocation, boolean force) {
        File buildLocationObj = (new FileSystemService()).getByLocation(buildLocation.getPath());

        boolean outputDirectoryExists = buildLocationObj.exists();
        boolean isDirectory = buildLocationObj.isDirectory();

        if (outputDirectoryExists && !force) {
            boolean isEmptyDirectory = true;
            String[] buildLocationContents = buildLocationObj.list();

            if (buildLocationContents != null)
                isEmptyDirectory = buildLocationContents.length == 0;

            if (isDirectory && !isEmptyDirectory) {
                this.errors.add(new UnEmptyDirectory());
                return false;
            }

            if (!isDirectory) {
                this.errors.add(new NotDirectoryError());
                return false;
            }
        }

        if (outputDirectoryExists && force && !isDirectory) {
            this.errors.add(new NotDirectoryError());
            return false;
        }

        if (outputDirectoryExists && force)
            this.warnings.add(new PreexistingDirectory());

        if (!outputDirectoryExists && !buildLocationObj.mkdir()) {
            this.errors.add(new CouldNotMakeDirectory());
            return false;
        }

        return true;
    }

    public boolean hasErrors() {
        return this.errors.size() > 0;
    }

    public boolean hasWarnings() {
        return this.warnings.size() > 0;
    }
}
