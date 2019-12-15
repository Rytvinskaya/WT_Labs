package epam.webtech.model.race;

import epam.webtech.model.Entity;
import epam.webtech.model.enums.RaceStatus;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

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
    private String[] horsesNames;
    private String winnerHorseName;

    @Override
    public int hashCode() {

        return Objects.hash(getId());
    }

}
