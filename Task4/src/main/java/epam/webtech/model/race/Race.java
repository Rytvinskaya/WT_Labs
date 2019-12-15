package epam.webtech.model.race;

import epam.webtech.model.Entity;
import epam.webtech.model.enums.RaceStatus;
import epam.webtech.model.horse.Horse;
import lombok.Data;

import java.util.Date;

@Data
public class Race extends Entity implements Comparable<Race> {

    @Override
    public int compareTo(Race o) {
        if (status.getPriority() == o.status.getPriority()) {
            return date.compareTo(o.date);
        } else
            return o.status.getPriority() - status.getPriority();
    }

    private Date date;
    private RaceStatus status;
    private Horse[] horses;
    private String winnerHorseName;

}
