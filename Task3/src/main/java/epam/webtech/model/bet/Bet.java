package epam.webtech.model.bet;

import epam.webtech.model.Entity;
import lombok.Data;

@Data
public class Bet extends Entity implements Comparable<Bet> {

    private int amount;
    private int raceId;
    private String horseName;
    private String userName;

    @Override
    public int compareTo(Bet o) {
        return (int) (amount - o.getAmount());
    }
}
