package epam.webtech.model.user;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.model.Dao;

import java.util.List;

public interface UserDao extends Dao<User> {

    User findByName(String name) throws NotFoundException, DatabaseException;
}
