package epam.webtech.model;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.NotFoundException;

import java.util.List;

public interface Dao<T> {

    T findById(int id) throws DatabaseException, NotFoundException;
    List<T> findAll() throws DatabaseException;
    void update(T object) throws DatabaseException, NotFoundException;
    void delete(T object) throws DatabaseException, NotFoundException;
}
