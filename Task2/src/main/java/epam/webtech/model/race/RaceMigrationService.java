package epam.webtech.model.race;

import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.ValidationException;
import epam.webtech.services.MigrationService;
import epam.webtech.services.JdbcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RaceMigrationService implements MigrationService<Race> {

    private static final String TABLE = "races";
    private static final String LINK_TABLE = "raceHorses";
    private static final String SELECT_QUERY = "SELECT * FROM " + TABLE + " WHERE id = ? ;";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE
            + " (id, race_date, status, winner) VALUES (?, ?, ?, ?)";
    private static final String LINK_INSERT_QUERY = "INSERT INTO " + LINK_TABLE + " (race_id, horse_name) VALUES (?, ?)";

    private final Logger logger = LogManager.getLogger(RaceMigrationService.class);
    private final JdbcService jdbcService = JdbcService.getInstance();

    private RaceMigrationService() {
    }

    private static class SingletonHandler {
        static final RaceMigrationService INSTANCE = new RaceMigrationService();
    }

    public static RaceMigrationService getInstance() {
        return RaceMigrationService.SingletonHandler.INSTANCE;
    }

    @Override
    public int migrate(List<Race> races) throws DatabaseException {
        AtomicInteger counter = new AtomicInteger();
        for (Race race : races) {
            try {
                saveRace(race);
                counter.getAndIncrement();
            } catch (AlreadyExistsException e) {
                logger.debug(e);
                System.out.println(e.getMessage());
            }
        }
        logger.debug("Total races: " + races.size() + ", successful migrated: " + counter);
        System.out.println(counter + " races migrated");
        return counter.get();
    }

    private void saveRace(Race race) throws AlreadyExistsException, DatabaseException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = jdbcService.getConnection().prepareStatement(SELECT_QUERY);
            preparedStatement.setInt(1, race.getId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                throw new AlreadyExistsException("Record Race with id " + race.getId() + " already exists id database");
            }
            preparedStatement = jdbcService.getConnection().prepareStatement(INSERT_QUERY);
            preparedStatement.setInt(1, race.getId());
            preparedStatement.setString(2, race.getDate().toString());
            preparedStatement.setInt(3, race.getStatus().getPriority());
            preparedStatement.setString(4, race.getWinnerHorseName());
            preparedStatement.executeUpdate();
            preparedStatement = jdbcService.getConnection().prepareStatement(LINK_INSERT_QUERY);
            for (String name : race.getHorsesNames()) {
                preparedStatement.setInt(1, race.getId());
                preparedStatement.setString(2, name);
                preparedStatement.executeUpdate();
            }
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
