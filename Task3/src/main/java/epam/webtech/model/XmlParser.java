package epam.webtech.model;

import epam.webtech.exceptions.DataSourceException;

import java.util.List;

public interface XmlParser<T> {

    List<T> getDataFromFile(String filePath) throws DataSourceException;
}
