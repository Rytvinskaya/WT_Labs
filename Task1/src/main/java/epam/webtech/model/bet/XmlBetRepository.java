package epam.webtech.model.bet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.XmlRepository;
import epam.webtech.model.user.User;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class XmlBetRepository implements XmlRepository, BetRepository {

    private static final String DATA_FILE_NAME = "bets.xml";

    private XmlMapper xmlMapper = new XmlMapper();
    private Map<Integer, Bet> bets;
    private int lastId = 0;

    private XmlBetRepository() {
        File dataFile = new File(DATA_FILE_NAME);
        String xml = null;
        bets = new HashMap<>();
        try {
            xml = inputStreamToString(new FileInputStream(dataFile));
            List<Bet> betList = xmlMapper.readValue(xml, new TypeReference<List<Bet>>() {
            });
            betList.forEach(bet -> {
                        bets.put(bet.getId(), bet);
                        if (bet.getId() > lastId)
                            lastId = bet.getId();
                    }
            );
        } catch (IOException e) {
            //TODO Add log
        }
    }

    private void updateDataFile() throws IOException {
        xmlMapper.writeValue(new File(DATA_FILE_NAME), bets.values());
    }

    @Override
    public void add(Bet object) throws AlreadyExistsException, IOException {
        object.setId(++lastId);
        bets.put(object.getId(), object);
        updateDataFile();
    }

    @Override
    public Bet getByID(int id) throws NotFoundException {
        Bet bet = bets.get(id);
        if (null == bet)
            throw new NotFoundException("Bet with id " + id + " not found");
        return bet;
    }

    @Override
    public void update(Bet object) throws NotFoundException, IOException {
        if (bets.containsKey(object.getId()))
            bets.put(object.getId(), object);
        else
            throw new NotFoundException("Bet with id " + object.getId() + " not found");
        updateDataFile();
    }

    @Override
    public void delete(Bet object) throws NotFoundException, IOException {
        if (bets.containsKey(object.getId()))
            bets.remove(object.getId());
        else
            throw new NotFoundException("Bet with id " + object.getId() + " not found");
        updateDataFile();
    }

    @Override
    public List<Bet> findAll() {
        return new ArrayList<>(bets.values());
    }

    @Override
    public List<Bet> findByUser(User user) {
        return bets.values().stream()
                .filter(bet -> bet.getUserName().equals(user.getName()))
                .collect(Collectors.toList());
    }
}
