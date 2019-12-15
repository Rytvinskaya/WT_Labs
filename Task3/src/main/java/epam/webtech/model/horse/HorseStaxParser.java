package epam.webtech.model.horse;

import epam.webtech.exceptions.DataSourceException;
import epam.webtech.model.XmlParser;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HorseStaxParser implements XmlParser<Horse> {

    enum HorseTag {
        HORSES, HORSE, HORSE_NAME, HORSE_WINS_COUNT;
    }

    private XMLStreamReader reader;
    private List<Horse> horses;

    @Override
    public List<Horse> getDataFromFile(String filePath) throws DataSourceException {
        XMLInputFactory xmlInputFactory;
        xmlInputFactory = XMLInputFactory.newFactory();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            reader = xmlInputFactory.createXMLStreamReader(inputStream);
            parse();
        } catch (FileNotFoundException | XMLStreamException e) {
            throw new DataSourceException(e.getMessage());
        }
        return horses;
    }

    private void parse() throws XMLStreamException {
        HorseTag tag = null;
        Horse horse = null;
        int staxEvent;
        while (reader.hasNext()) {
            staxEvent = reader.next();
            switch (staxEvent) {
                case XMLEvent.START_ELEMENT:
                    try {
                        String tagName = reader.getLocalName().toUpperCase().replace("-", "_");
                        tag = HorseTag.valueOf(tagName);
                        switch (tag) {
                            case HORSES:
                                horses = new ArrayList<>();
                                break;
                            case HORSE:
                                horse = new Horse();
                                break;
                        }
                    } catch (Exception e) {
                        tag = null;
                    }
                    break;
                case XMLEvent.CHARACTERS:
                    String text = reader.getText();
                    if (text.trim().isEmpty())
                        break;
                    if ((horse != null) && (tag != null)) {
                        switch (tag) {
                            case HORSE_NAME:
                                horse.setName(text);
                                break;
                            case HORSE_WINS_COUNT:
                                horse.setWinsCounter(Integer.parseInt(text));
                                break;
                        }
                    }
                    break;
                case XMLEvent.END_ELEMENT:
                    try {
                        if (HorseTag.valueOf(reader.getLocalName().toUpperCase().replace("-", "_")) == HorseTag.HORSE) {
                            horses.add(horse);
                            horse = null;
                        }
                    } catch (Exception e) {
                    }
                    break;
            }
        }
    }
}
