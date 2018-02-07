package us.sourcefoundry.gutenberg.services.commandcli.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CliOption {

    private Option reference;
    private String value;

}
