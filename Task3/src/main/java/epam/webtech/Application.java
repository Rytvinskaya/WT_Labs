package epam.webtech;

import epam.webtech.exceptions.DataSourceException;
import epam.webtech.model.XmlParser;
import epam.webtech.model.bet.Bet;
import epam.webtech.model.bet.BetDomParser;
import epam.webtech.model.enums.RaceStatus;
import epam.webtech.model.horse.Horse;
import epam.webtech.model.horse.HorseStaxParser;
import epam.webtech.model.race.Race;
import epam.webtech.model.race.RaceDomParser;
import epam.webtech.model.user.User;
import epam.webtech.model.user.UserSaxParser;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Application {

    public static void main(String[] args) {
        Properties prop = new Properties();
        try (InputStream input = new FileInputStream("src/main/resources/conf.properties")) {
            prop.load(input);
            String xmlDataFileName = prop.getProperty("data.xmlFileName");
            //Users
            System.out.println("USERS:");
            UserSaxParser userParser = new UserSaxParser();
            XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(userParser);
            xmlReader.parse(new InputSource(xmlDataFileName));
            userParser.getUsers().forEach(Application::printUser);
            //Horses
            System.out.println("HORSES:");
            XmlParser parser = new HorseStaxParser();
            parser.getDataFromFile(xmlDataFileName).forEach(horse -> printHorse((Horse)horse));
            //Races
            System.out.println("RACES:");
            parser = new RaceDomParser();
            parser.getDataFromFile(xmlDataFileName).forEach(race -> printRace((Race)race));
            //Bets
            System.out.println("BETS:");
            parser = new BetDomParser();
            parser.getDataFromFile(xmlDataFileName).forEach(bet -> printBet((Bet)bet));
        } catch (IOException | SAXException | DataSourceException ex) {
            ex.printStackTrace(); //TODO log
        }
    }

    private static void printUser(User user) {
        String output = "    User:\n        Name: " +
                user.getName() +
                "\n        PasswordHash: " +
                user.getPasswordHash() +
                "\n        Bank: " +
                user.getBank() +
                "\n        AuthorityLvl: " +
                user.getAuthorityLvl();
        System.out.println(output);
    }

    private static void printHorse(Horse horse) {
        String output = "    Horse:\n        Name: " +
                horse.getName() +
                "\n        WinsCount: " +
                horse.getWinsCounter();
        System.out.println(output);
    }

    private static void printRace(Race race) {
        StringBuilder output = new StringBuilder();
        output.append("    Race:\n        ID: ");
        output.append(race.getId());
        output.append("\n        Date: ");
        output.append(race.getDate());
        output.append("\n        Horses:");
        for (String horseName:race.getHorsesNames()) {
            output.append("\n            ");
            output.append(horseName);
        }
        output.append("\n        Status: ");
        output.append(race.getStatus());
        if (race.getStatus().equals(RaceStatus.FINISHED)) {
            output.append("\n        Winner: ");
            output.append(race.getWinnerHorseName());
        }
        System.out.println(output.toString());
    }

    private static void printBet(Bet bet) {
        String output = "    Bet:\n        Race id: " +
                bet.getRaceId()  +
                "\n        User: " +
                bet.getUserName() +
                "\n        Horse: " +
                bet.getHorseName() +
                "\n        Amount: " +
                bet.getAmount();
        System.out.println(output);
    }
}
