package us.sourcefoundry.gutenberg.commands.add.providers.github;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.IOUtils;
import us.sourcefoundry.gutenberg.commands.add.models.ILocationReference;
import us.sourcefoundry.gutenberg.config.ApplicationProperties;
import us.sourcefoundry.gutenberg.services.console.Console;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The provider will fetch an archive from Github given the user, repository, and tag.
 */
@Getter
@Setter
@AllArgsConstructor
public class GithubProviderClient {

    //The console.
    private Console console;
    //The application properties.
    private ApplicationProperties applicationProperties;

    /**
     * Will download the archive from Github and return it as a InputStream.
     *
     * @param locationReference The location.
     * @return Input Stream
     */
    public ByteArrayInputStream getFormeFiles(ILocationReference locationReference) {
        GithubArchiveURL githubArchiveURL;
        try {
            //Get the URL from the Github API archive.
            githubArchiveURL = GithubArchiveURL.fromGithubLocation(
                    this.applicationProperties.get("github.tarball.url").toString(),
                    locationReference
            );
        } catch (MalformedURLException e) {
            this.console.error("Github URL is malformed.");
            return null;
        }


        //Download the file from Github and buffer it for processing.
        ByteArrayInputStream githubArchiveFileStream;
        try {
            githubArchiveFileStream = this.downloadGithubArchive(githubArchiveURL.getUrl());
        } catch (IOException e) {
            this.console.error("Could not download archive from Github.");
            return null;
        }

        return githubArchiveFileStream;
    }

    /**
     * Download the archive from Github.
     *
     * @param resourceURL The URL for the archive.
     * @return ByteArrayInputStram
     */
    private ByteArrayInputStream downloadGithubArchive(URL resourceURL) throws IOException {
        InputStream urlStream = resourceURL.openStream();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        IOUtils.copy(urlStream, baos);
        return new ByteArrayInputStream(baos.toByteArray());
    }
}
