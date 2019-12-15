package epam.webtech.model.bet;

import epam.webtech.exceptions.DataSourceException;
import epam.webtech.model.XmlDomParser;
import epam.webtech.model.XmlParser;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BetDomParser implements XmlDomParser, XmlParser<Bet> {

    enum BetTag {
        BETS, BET, BET_AMOUNT, BET_RACE_ID, BET_HORSE_NAME, BET_USER_NAME
    }

    @Override
    public List<Bet> getDataFromFile(String filePath) throws DataSourceException {
        List<Bet> bets;
        File file = new File(filePath);
        try {
            Document document = buildDocument(file);
            NodeList nodeList = document.getDocumentElement().getElementsByTagName("bet");
            bets = new ArrayList<>(nodeList.getLength());
            for (int i = 0; i < nodeList.getLength(); i++) {
                if (nodeList.item(i).getNodeType() != Node.TEXT_NODE) {
                    bets.add(getBetFromNode(nodeList.item(i)));
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            throw new DataSourceException("File " + filePath + " not found");
        }
        return bets;
    }

    private Bet getBetFromNode(Node orderNode) {
        Bet bet = new Bet();
        bet.setId(Integer.parseInt(orderNode.getAttributes().getNamedItem("id").getNodeValue()));
        NodeList orderProps = orderNode.getChildNodes();
        BetTag tag = null;
        String input = null;
        for (int j = 0; j < orderProps.getLength(); j++) {
            if ((orderProps.item(j).getNodeType() != Node.TEXT_NODE)) {
                try {
                    input = orderProps.item(j).getNodeName();
                    tag = BetTag.valueOf(input.toUpperCase().replace("-","_"));
                    switch (tag) {
                        case BET_AMOUNT:
                            bet.setAmount(Integer.parseInt(orderProps.item(j).getTextContent()));
                            break;
                        case BET_RACE_ID:
                            bet.setRaceId(Integer.parseInt(orderProps.item(j).getTextContent()));
                            break;
                        case BET_HORSE_NAME:
                            bet.setHorseName(orderProps.item(j).getTextContent());
                            break;
                        case BET_USER_NAME:
                            bet.setUserName(orderProps.item(j).getTextContent());
                            break;
                    }
                } catch (DOMException e) {
                    //TODO log
                }
            }
        }
        return bet;
    }
}
