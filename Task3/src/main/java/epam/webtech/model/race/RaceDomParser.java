package epam.webtech.model.race;

import epam.webtech.exceptions.DataSourceException;
import epam.webtech.model.XmlDomParser;
import epam.webtech.model.XmlParser;
import epam.webtech.model.enums.RaceStatus;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class RaceDomParser implements XmlDomParser, XmlParser<Race> {

    enum RaceTag {
        RACES, RACE, RACE_DATE, RACE_STATUS, RACE_HORSE_NAME, RACE_HORSE_NAMES, RACE_WINNER_HORSE_NAME
    }

    @Override
    public List<Race> getDataFromFile(String filePath) throws DataSourceException {
        File file = new File(filePath);
        List<Race> races;
        try {
            Document document = buildDocument(file);
            NodeList nodeList = document.getDocumentElement()
                    .getElementsByTagName(RaceTag.RACE.name().toLowerCase().replace("_", "-"));
            NodeList raceHorseNamesList = document.getDocumentElement()
                    .getElementsByTagName(RaceTag.RACE_HORSE_NAMES.name().toLowerCase().replace("_", "-"));
            races = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.TEXT_NODE) {
                    Race race = getRaceFromNode(nodeList.item(i));
                    races.add(race);
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new DataSourceException("File " + filePath + " not found");
        }
        return races;
    }

    private Race getRaceFromNode(Node raceNode) {
        Race race = new Race();
        race.setId(Integer.parseInt(raceNode.getAttributes().getNamedItem("id").getNodeValue()));
        NodeList raceProps = raceNode.getChildNodes();
        RaceTag tag = null;
        String input = null;
        for (int j = 0; j < raceProps.getLength(); j++) {
            if ((raceProps.item(j).getNodeType() != Node.TEXT_NODE)) {
                try {
                    input = raceProps.item(j).getNodeName();
                    tag = RaceTag.valueOf(input.toUpperCase().replace("-", "_"));
                    switch (tag) {
                        case RACE_DATE:
                            System.out.println(raceProps.item(j).getTextContent());
                            race.setDate(Date.valueOf(raceProps.item(j).getTextContent()));
                            break;
                        case RACE_STATUS:
                            race.setStatus(RaceStatus.valueOf(raceProps.item(j).getTextContent()));
                            break;
                        case RACE_WINNER_HORSE_NAME:
                            race.setWinnerHorseName(raceProps.item(j).getTextContent());
                            break;
                        case RACE_HORSE_NAMES:
                            List<String> names = new ArrayList<>();
                            NodeList horseNames = raceProps.item(j).getChildNodes();
                            for (int k = 0; k < horseNames.getLength(); k++)
                                if (horseNames.item(k).getNodeName().equals(RaceTag.RACE_HORSE_NAME
                                        .name().toLowerCase().replace("_", "-")))
                                    names.add(horseNames.item(k).getTextContent());
                            String[] namesArr = new String[names.size()];
                            names.toArray(namesArr);
                            race.setHorsesNames(namesArr);
                            break;
                    }
                } catch (DOMException e) {
                    //TODO log
                }
            }
        }
        return race;
    }
}
