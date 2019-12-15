package epam.webtech.model.horse;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.utils.JdbcService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MySqlHorseDao implements HorseDao {

    private static final String TABLE = "horses";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE + " WHERE id = ? ;";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM " + TABLE + " WHERE name = ? ;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE + ";";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE + " WHERE id = ?;";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE + "SET name = ?, wins_counter = ? WHERE id = ?";

    private JdbcService jdbcService = epam.webtech.utils.JdbcService.getInstance();

    @Override
    public Horse findByName(String name) throws NotFoundException, DatabaseException {
        Horse horse;
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(FIND_BY_NAME_QUERY)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.isFirst()) {
                    horse = getHorseFromResultSet(resultSet);
                } else {
                    throw new NotFoundException("Horse with name " + name + " not found");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
        return horse;
    }

    @Override
    public Horse findById(int id) throws DatabaseException, NotFoundException {
        Horse horse;
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(FIND_BY_ID_QUERY)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.isFirst()) {
                    horse = getHorseFromResultSet(resultSet);
                } else {
                    throw new NotFoundException("Horse with id " + id + " not found");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
        return horse;
    }

    @Override
    public List<Horse> findAll() throws DatabaseException {
        List<Horse> horses = new ArrayList<>();
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(FIND_ALL_QUERY)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    horses.add(getHorseFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        } catch (DatabaseException e) {
            throw e;
        }
        return horses;
    }

    @Override
    public void update(Horse horse) throws DatabaseException, NotFoundException {
        findById(horse.getId());
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(UPDATE_QUERY)) {
            preparedStatement.setString(1, horse.getName());
            preparedStatement.setInt(2, horse.getWinsCounter());
            preparedStatement.setInt(3, horse.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
    }

    @Override
    public void delete(Horse horse) throws DatabaseException, NotFoundException {
        findById(horse.getId());
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(DELETE_QUERY)) {
            preparedStatement.setInt(1, horse.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
    }

    private Horse getHorseFromResultSet(ResultSet resultSet) throws SQLException {
        Horse horse = new Horse();
        horse.setId(resultSet.getInt("id"));
        horse.setName(resultSet.getString("name"));
        horse.setWinsCounter(resultSet.getInt("wins_counter"));
        return horse;
    }
}
