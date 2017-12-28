package us.sourcefoundry.gutenberg.commands.add.providers.github;

import lombok.Getter;
import us.sourcefoundry.gutenberg.commands.add.models.ILocationReference;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

/**
 * This will produce a URL which can be used to get an archive from Github.
 */
@Getter
public class GithubArchiveURL {

    //The Github API url for the archive.
    private URL url;

    /**
     * Constructor
     *
     * @param urlString The url.
     */
    private GithubArchiveURL(String urlString) throws MalformedURLException {
        this.url = new URL(urlString);
    }

    /**
     * This will use the Github location reference to produce a usable Github URL.
     *
     * @param githubApiURLTemplate The template to use to produce a usable Github URL.
     * @param githubLocation       The github location reference.
     * @return GithubArchiveURL
     */
    public static GithubArchiveURL fromGithubLocation(String githubApiURLTemplate, ILocationReference githubLocation) throws MalformedURLException {
        return new GithubArchiveURL(
                MessageFormat.format(
                        githubApiURLTemplate,
                        githubLocation.getUser(),
                        githubLocation.getRepository(),
                        githubLocation.getTag())
        );
    }
}
