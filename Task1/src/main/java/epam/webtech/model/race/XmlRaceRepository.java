package epam.webtech.model.race;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.XmlRepository;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class XmlRaceRepository implements XmlRepository, RaceRepository {

    private static final String DATA_FILE_NAME = "races.xml";

    private XmlMapper xmlMapper = new XmlMapper();
    private Map<Integer, Race> races;
    private int lastId = 0;

    private XmlRaceRepository() {
        File dataFile = new File(DATA_FILE_NAME);
        String xml = null;
        races = new HashMap<>();
        try {
            xml = inputStreamToString(new FileInputStream(dataFile));
            List<Race> raceList = xmlMapper.readValue(xml, new TypeReference<List<Race>>() {
            });
            raceList.forEach(race -> {
                        races.put(race.getId(), race);
                        if (race.getId() > lastId)
                            lastId = race.getId();
                    }
            );
        } catch (IOException e) {
            //TODO Add log
        }
    }

    private void updateDataFile() throws IOException {
        xmlMapper.writeValue(new File(DATA_FILE_NAME), races.values());
    }

    @Override
    public void add(Race object) throws IOException {
        object.setId(++lastId);
        races.put(object.getId(), object);
        updateDataFile();
    }

    @Override
    public Race getByID(int id) throws NotFoundException {
        Race race = races.get(id);
        if (null == race)
            throw new NotFoundException("Race with id " + id + " not found");
        return race;
    }

    @Override
    public void update(Race object) throws NotFoundException, IOException {
        if (races.containsKey(object.getId()))
            races.put(object.getId(), object);
        else
            throw new NotFoundException("Race with id " + object.getId() + " not found");
        updateDataFile();
    }

    @Override
    public void delete(Race object) throws NotFoundException, IOException {
        if (races.containsKey(object.getId()))
            races.remove(object.getId());
        else
            throw new NotFoundException("Race with id " + object.getId() + " not found");
        updateDataFile();
    }

    @Override
    public List<Race> findAll() {
        return new ArrayList<>(races.values());
    }
}
