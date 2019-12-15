package epam.webtech.model.horse;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.ValidationException;
import epam.webtech.services.MigrationService;
import epam.webtech.services.JdbcService;
import epam.webtech.services.XsdValidationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class HorseMigrationService implements MigrationService<Horse> {

    private static final String TABLE = "horses";
    private static final String SELECT_QUERY = "SELECT * FROM " + TABLE + " WHERE name = ? ;";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE + " (id, name, wins_counter) VALUES (?, ?, ?)";

    private final Logger logger = LogManager.getLogger(HorseMigrationService.class);

    private XsdValidationService validationService = XsdValidationService.getInstance();
    private JdbcService jdbcService = JdbcService.getInstance();

    private XmlMapper xmlMapper = new XmlMapper();

    private HorseMigrationService() {
    }

    private static class SingletonHandler {
        static final HorseMigrationService INSTANCE = new HorseMigrationService();
    }

    public static HorseMigrationService getInstance() {
        return HorseMigrationService.SingletonHandler.INSTANCE;
    }

    @Override
    public int migrate(List<Horse> horses) throws DatabaseException {
        AtomicInteger counter = new AtomicInteger();
        for (Horse horse : horses) {
            try {
                saveHorse(horse);
                counter.getAndIncrement();
            } catch (AlreadyExistsException e) {
                logger.debug(e);
                System.out.println(e.getMessage());
            }
        }
        logger.debug("Total horses: " + horses.size() + ", successful migrated: " + counter);
        System.out.println(counter + " horses migrated");
        return counter.get();
    }

    private void saveHorse(Horse horse) throws AlreadyExistsException, DatabaseException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = jdbcService.getConnection().prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, horse.getName());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                throw new AlreadyExistsException("Record Horse with name " + horse.getName() + " already exists id database");
            }
            //id, name, winsCounter
            preparedStatement = jdbcService.getConnection().prepareStatement(INSERT_QUERY);
            preparedStatement.setInt(1, horse.getId());
            preparedStatement.setString(2, horse.getName());
            preparedStatement.setInt(3, horse.getWinsCounter());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            logger.error(e);
            throw new DatabaseException(e.getMessage());
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (preparedStatement != null)
                    preparedStatement.close();
            } catch (SQLException e) {
                logger.error(e);
            }

        }
    }
}
