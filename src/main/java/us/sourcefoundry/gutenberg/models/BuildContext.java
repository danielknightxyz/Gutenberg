package us.sourcefoundry.gutenberg.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import us.sourcefoundry.gutenberg.models.forme.Forme;

import java.util.Map;

/**
 * This allows the build process to get all the info it may need.
 */
@Getter
@Setter
@AllArgsConstructor
public class BuildContext {
    //The requested out put directory for the build.
    private BuildLocation buildLocation;
    //The form to use for the build.
    private Forme forme;
    //The location of the forme.
    private FormeLocation formeLocation;
    //Any answers to the prompts.
    private Map<String, Object> userResponses;
}
