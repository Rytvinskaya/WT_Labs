package epam.webtech.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import epam.webtech.model.bet.Bet;
import epam.webtech.model.horse.Horse;
import epam.webtech.model.race.Race;
import epam.webtech.model.user.User;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "HorseRacingData")
public class HorseRacingData {

    @JacksonXmlElementWrapper(localName = "Users")
    @JacksonXmlProperty(localName = "user")
    private List<User> users;

    @JacksonXmlElementWrapper(localName = "Horses")
    @JacksonXmlProperty(localName = "horse")
    private List<Horse> horses;

    @JacksonXmlElementWrapper(localName = "Races")
    @JacksonXmlProperty(localName = "race")
    private List<Race> races;

    @JacksonXmlElementWrapper(localName = "Bets")
    @JacksonXmlProperty(localName = "bet")
    private List<Bet> bets;

}
