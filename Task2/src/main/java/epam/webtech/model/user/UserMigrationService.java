package epam.webtech.model.user;

import epam.webtech.exceptions.AlreadyExistsException;
import epam.webtech.exceptions.DatabaseException;
import epam.webtech.services.MigrationService;
import epam.webtech.services.JdbcService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UserMigrationService implements MigrationService<User> {

    private static final String TABLE = "users";
    private static final String SELECT_QUERY = "SELECT * FROM " + TABLE + " WHERE name = ? ;";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE
            + " (id, name, password_hash, bank, authority_lvl) VALUES (?, ?, ?, ?, ?)";

    private static final Logger logger = LogManager.getLogger(UserMigrationService.class);

    private JdbcService jdbcService = JdbcService.getInstance();

    private UserMigrationService() {
    }

    private static class SingletonHandler {
        static final UserMigrationService INSTANCE = new UserMigrationService();
    }

    public static UserMigrationService getInstance() {
        return UserMigrationService.SingletonHandler.INSTANCE;
    }

    @Override
    public int migrate(List<User> users) throws DatabaseException {
        AtomicInteger counter = new AtomicInteger();
        for (User user : users) {
            try {
                saveUser(user);
                counter.getAndIncrement();
            } catch (AlreadyExistsException e) {
                logger.debug(e);
                System.out.println(e.getMessage());
            }
        }
        logger.debug("Total users: " + users.size() + ", successful migrated: " + counter);
        System.out.println(counter + " users migrated");
        return counter.get();
    }

    private void saveUser(User user) throws AlreadyExistsException, DatabaseException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = jdbcService.getConnection().prepareStatement(SELECT_QUERY);
            preparedStatement.setString(1, user.getName());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                throw new AlreadyExistsException("Record User with name " + user.getName() + " already exists id database");
            }
            preparedStatement = jdbcService.getConnection().prepareStatement(INSERT_QUERY);
            preparedStatement.setInt(1, user.getId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getPasswordHash());
            preparedStatement.setInt(4, user.getBank());
            preparedStatement.setInt(5, user.getAuthorityLvl());
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
