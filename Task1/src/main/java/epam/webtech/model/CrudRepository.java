package epam.webtech.model;

import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.NotFoundException;

import java.io.IOException;
import java.util.List;

public interface CrudRepository<T> {

    void add(T object) throws IOException, AlreadyExistsException;

    T getByID(int id) throws NotFoundException;

    void update(T object) throws NotFoundException, IOException;

    void delete(T object) throws NotFoundException, IOException;

    List<T> findAll();
}
