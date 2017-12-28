package us.sourcefoundry.gutenberg.models;

import lombok.Getter;
import org.ocpsoft.prettytime.PrettyTime;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
public class HumanFriendlyDate {

    private String prettyTime;

    private HumanFriendlyDate(String prettyTime){
        this.prettyTime = prettyTime;
    }

    public static HumanFriendlyDate fromLocalDateTime(LocalDateTime localDateTime){
        return new HumanFriendlyDate(new PrettyTime().format(Timestamp.valueOf(localDateTime)));
    }
}
