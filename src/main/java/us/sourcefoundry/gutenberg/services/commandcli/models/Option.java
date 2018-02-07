package us.sourcefoundry.gutenberg.services.commandcli.models;

import lombok.Builder;
import lombok.Getter;

/**
 * This is a cli option for a defined command.
 */
@Getter
@Builder
public class Option {

    //The name of the reference.
    private String name;
    //The description of the reference.
    private String description;
    //Any aliases for the reference.
    private String longName;
    //Should this reference expect a value.
    private boolean expectParameter;
    //The parameter name;
    private String parameterName;
}
