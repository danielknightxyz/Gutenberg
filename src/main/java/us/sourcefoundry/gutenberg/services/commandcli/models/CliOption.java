package us.sourcefoundry.gutenberg.services.commandcli.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This represents an option read from the application arguments.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CliOption {

    //A reference to the option defined.
    private Option reference;
    //The value of the argument if a value is expected.
    private String value;

}
