package us.sourcefoundry.gutenberg.commands.add.providers.github;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import us.sourcefoundry.gutenberg.commands.add.models.ILocationReference;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class will parse a string into the appropriate Github components.
 */
@Getter
@Setter
@NoArgsConstructor
public class GithubLocation implements ILocationReference<GithubLocation> {

    //The regex pattern which will identify and allow the above components to be parsed.
    private final Pattern PATTERN = Pattern.compile("^(.+)\\/(.+):(.+)$|^(.+)\\/(.+)$");
    //The user or organization in Github.
    private String user;
    //The name of the Github repository..
    private String repository;
    //The Github reference.  This will eventually default to master.
    private String reference;

    /**
     * Constructor
     *
     * @param location The Github location.
     */
    private GithubLocation(String location) {
        //It checks out, so parse and hydrate.
        this.parseLocation(location);
    }

    /**
     * Returns the tag for the location.
     *
     * @return String
     */
    @Override
    public String getTag() {
        return this.reference;
    }

    /**
     * Creates a new github location from a string.
     *
     * @param location The Github location.
     * @return GithubLocation
     */
    public static GithubLocation fromString(String location) {
        return new GithubLocation(location);
    }

    /**
     * Attempts to match the location.
     *
     * @param location The Github location.
     * @return Matcher
     */
    private Matcher locationMatches(String location) {
        return PATTERN.matcher(location);
    }

    /**
     * Parsed the Github location into its components.
     *
     * @param location The Github location.
     */
    private void parseLocation(String location) {
        //Match it.
        Matcher matcher = this.locationMatches(location);

        //If it doesn't match. Don't do anything.
        if (!matcher.matches())
            return;

        //Parse the components.
        String user = (matcher.group(1) != null ? matcher.group(1) : matcher.group(4));
        String repository = (matcher.group(2) != null ? matcher.group(2) : matcher.group(5));
        String reference = (matcher.group(3) != null ? matcher.group(3) : "master");

        //Hydrate the model.
        this.setUser(user);
        this.setRepository(repository);
        this.setReference(reference);
    }

    /**
     * Produces a full description of the location.
     *
     * @return String
     */
    public String toFQN() {
        return MessageFormat.format("{0}/{1}:{2}", this.getUser(), this.getRepository(), this.getReference());
    }
}
