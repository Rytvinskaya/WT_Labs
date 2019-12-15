package epam.webtech.model.horse;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.Dao;

public interface HorseDao extends Dao<Horse> {

    Horse findByName(String name) throws NotFoundException, DatabaseException;

}
