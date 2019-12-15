package epam.webtech.model.horse;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import epam.webtech.exceptions.AlreadyExistsException;
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
public class XmlHorseRepository implements XmlRepository, HorseRepository {

    private static final String DATA_FILE_NAME = "horses.xml";
    private XmlMapper xmlMapper = new XmlMapper();
    private Map<String, Horse> horses;
    private int lastId = 0;

    private XmlHorseRepository() {
        File dataFile = new File(DATA_FILE_NAME);
        String xml = null;
        horses = new HashMap<>();
        try {
            xml = inputStreamToString(new FileInputStream(dataFile));
            List<Horse> horseList = xmlMapper.readValue(xml, new TypeReference<List<Horse>>() {
            });
            horseList.forEach(horse -> {
                horses.put(horse.getName(), horse);
                if (horse.getId() > lastId)
                    lastId = horse.getId();
            });
        } catch (IOException e) {
            //TODO Add log
        }
    }

    private void updateDataFile() throws IOException {
        xmlMapper.writeValue(new File(DATA_FILE_NAME), horses.values());
    }

    @Override
    public void add(Horse object) throws AlreadyExistsException, IOException {
        if (horses.containsKey(object.getName()))
            throw new AlreadyExistsException("Horse with name " + object.getName() + " already exists");
        object.setId(++lastId);
        horses.put(object.getName(), object);
        updateDataFile();
    }

    @Override
    public Horse getByID(int id) throws NotFoundException {
        return horses.values().stream()
                .filter(x -> x.getId() == id)
                .findAny()
                .orElseThrow(() -> new NotFoundException("Horse with ID " + id + " not found"));
    }

    @Override
    public void update(Horse object) throws NotFoundException, IOException {
        if (horses.containsKey(object.getName()))
            horses.put(object.getName(), object);
        else
            throw new NotFoundException("Horse with name " + object.getName() + " not found");
        updateDataFile();
    }

    @Override
    public void delete(Horse object) throws NotFoundException, IOException {
        if (horses.containsKey(object.getName()))
            horses.remove(object.getName());
        else
            throw new NotFoundException("Horse with name " + object.getName() + " not found");
        updateDataFile();
    }

    @Override
    public List<Horse> findAll() {
        return new ArrayList<>(horses.values());
    }

    @Override
    public Horse getByName(String name) throws NotFoundException {
        Horse horse = horses.get(name);
        if (null == horse)
            throw new NotFoundException("Horse with name " + name + " not found");
        return horse;
    }
}
