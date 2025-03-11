package dataaccess;

import model.AuthData;

import java.sql.*;
import java.util.Collection;
import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

public class SQLAuthDAO implements AuthDAO {
    public SQLAuthDAO() {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
          CREATE TABLE IF NOT EXISTS auth (
          `username` varchar(256) NOT NULL,
          `authToken` varchar(256) NOT NULL,
          PRIMARY KEY (`authToken`),
          INDEX(username)
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
    public void createAuth(AuthData authData) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO auth (username, authToken) VALUES (?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authData.getUsername());
                ps.setString(2, authData.getAuthToken());
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, authToken FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var user = rs.getString("username");
                        return new AuthData(user, authToken);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get auth in database");
        }
        throw new DataAccessException("AuthToken does not exist");
    }

    @Override
    public void removeAuth(String authToken) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE auth";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new DataAccessException("Failed to clear auth database");
        }
    }
}