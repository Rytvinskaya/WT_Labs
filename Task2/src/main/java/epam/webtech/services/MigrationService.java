package epam.webtech.services;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.ValidationException;

import java.io.File;
import java.util.List;

public interface MigrationService<T> {

    int migrate(List<T> data) throws ValidationException, DatabaseException;
}
