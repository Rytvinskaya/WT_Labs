package epam.webtech.model.bet;

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

public class BetMigrationService implements MigrationService<Bet> {

    private static final String TABLE = "bets";
    private static final String SELECT_QUERY = "SELECT * FROM " + TABLE + " WHERE id = ? ;";
    private static final String INSERT_QUERY = "INSERT INTO " + TABLE
            + " (id, amount, race_id, horse_name, user_name) VALUES (?, ?, ?, ?, ?)";

    private final Logger logger = LogManager.getLogger(BetMigrationService.class);

    private JdbcService jdbcService = JdbcService.getInstance();

    private BetMigrationService() {
    }

    private static class SingletonHandler {
        static final BetMigrationService INSTANCE = new BetMigrationService();
    }

    public static BetMigrationService getInstance() {
        return BetMigrationService.SingletonHandler.INSTANCE;
    }

    @Override
    public int migrate(List<Bet> bets) throws DatabaseException {
        AtomicInteger counter = new AtomicInteger();
        for (Bet bet : bets) {
            try {
                saveBet(bet);
                counter.getAndIncrement();
            } catch (AlreadyExistsException e) {
                System.out.println(e.getMessage());
                logger.debug(e);
            }
        }
        logger.debug("Total bets: " + bets.size() + ", successful migrated: " + counter);
        System.out.println(counter + " bets migrated");
        return counter.get();
    }

    private void saveBet(Bet bet) throws AlreadyExistsException, DatabaseException {
        ResultSet resultSet = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = jdbcService.getConnection().prepareStatement(SELECT_QUERY);
            preparedStatement.setInt(1, bet.getId());
            resultSet = preparedStatement.executeQuery();
            if (resultSet.first()) {
                throw new AlreadyExistsException("Record Bet with id " + bet.getId() + " already exists id database");
            }
            preparedStatement = jdbcService.getConnection().prepareStatement(INSERT_QUERY);
            preparedStatement.setInt(1, bet.getId());
            preparedStatement.setFloat(2, bet.getAmount());
            preparedStatement.setInt(3, bet.getRaceId());
            preparedStatement.setString(4, bet.getHorseName());
            preparedStatement.setString(5, bet.getUserName());
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
