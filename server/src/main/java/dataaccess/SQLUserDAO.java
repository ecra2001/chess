package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.sql.*;
import java.util.Collection;
import org.mindrot.jbcrypt.BCrypt;


public class SQLUserDAO implements UserDAO {

    public SQLUserDAO() {
        configureDatabase();
    }

    private final String[] createStatements = {
    """
          CREATE TABLE IF NOT EXISTS users (
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256),
          PRIMARY KEY (`username`),
          INDEX(password)
          )
    """
    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var user = rs.getString("username");
                        var password = rs.getString("password");
                        var email = rs.getString("email");
                        return new UserData(user, password, email);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found");
        }
        return null;
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, userData.getUsername());
                ps.setString(2, BCrypt.hashpw(userData.getPassword(), BCrypt.gensalt()));
                ps.setString(3, userData.getEmail());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("User not found");
        }
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        UserData user = getUser(username);
        if (user == null) {
            throw new DataAccessException("User does not exist");
        }
        return BCrypt.checkpw(password, user.getPassword());
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE users";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to clear user database");
        }
    }
}