package epam.webtech.model.user;

import epam.webtech.exceptions.DatabaseException;
import epam.webtech.exceptions.NotFoundException;
import epam.webtech.utils.JdbcService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class MySqlUserDao implements UserDao {

    private static final String TABLE = "users";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM " + TABLE + " WHERE id = ? ;";
    private static final String FIND_BY_NAME_QUERY = "SELECT * FROM " + TABLE + " WHERE name = ? ;";
    private static final String FIND_ALL_QUERY = "SELECT * FROM " + TABLE + ";";
    private static final String DELETE_QUERY = "DELETE FROM " + TABLE + " WHERE id = ?;";
    private static final String UPDATE_QUERY = "UPDATE " + TABLE
            + "SET name = ?, password_hash = ?, bank = ?, authority_lvl = ? WHERE id = ?";

    private JdbcService jdbcService = JdbcService.getInstance();

    @Override
    public User findById(int id) throws DatabaseException, NotFoundException {
        User user;
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(FIND_BY_ID_QUERY)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.isFirst()) {
                    user = getUserFromResultSet(resultSet);
                } else {
                    throw new NotFoundException("User with id " + id + " not found");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
        return user;
    }

    @Override
    public User findByName(String name) throws NotFoundException, DatabaseException {
        User user;
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(FIND_BY_NAME_QUERY)) {
            preparedStatement.setString(1, name);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.isFirst()) {
                    user = getUserFromResultSet(resultSet);
                } else {
                    throw new NotFoundException("User with name " + name + " not found");
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
        return user;
    }

    @Override
    public List<User> findAll() throws DatabaseException {
        List<User> users = new ArrayList<>();
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(FIND_ALL_QUERY)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    users.add(getUserFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        } catch (DatabaseException e) {
            throw e;
        }
        return users;
    }

    @Override
    public void update(User user) throws DatabaseException, NotFoundException {
        findById(user.getId());
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(UPDATE_QUERY)) {
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPasswordHash());
            preparedStatement.setInt(3, user.getBank());
            preparedStatement.setInt(4, user.getAuthorityLvl());
            preparedStatement.setInt(5, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
    }

    @Override
    public void delete(User user) throws DatabaseException, NotFoundException {
        findById(user.getId());
        try (PreparedStatement preparedStatement = jdbcService.getConnection().prepareStatement(DELETE_QUERY)) {
            preparedStatement.setInt(1, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Database error");
        }
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setName(resultSet.getString("name"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setBank(resultSet.getInt("bank"));
        user.setAuthorityLvl(resultSet.getInt("authority_lvl"));
        return user;
    }
}
