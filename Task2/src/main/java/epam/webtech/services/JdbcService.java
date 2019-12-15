package epam.webtech.services;

import epam.webtech.exceptions.DatabaseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcService {


    private final Logger logger = LogManager.getLogger(JdbcService.class);

    private Connection connection;
    private boolean isInited = false;

    private JdbcService() {
    }

    public void init(String url, String user, String password) throws DatabaseException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(url, user, password);
            logger.debug("Connection to " + url + " successful");
            isInited = true;
        } catch (ClassNotFoundException | SQLException e) {
            logger.error("Connection to " + url + " failed " + e);
            isInited = false;
            throw new DatabaseException(e.getMessage());
        }
    }

    private static class SingletonHandler {
        static final JdbcService INSTANCE = new JdbcService();
    }

    public static JdbcService getInstance() {
        return JdbcService.SingletonHandler.INSTANCE;
    }

    public Connection getConnection() throws DatabaseException {
        if (isInited)
            return connection;
        else
            throw new DatabaseException("JdbcService not initialized");
    }


}
