package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.sql.*;
import java.util.Collection;


public class SQLUserDAO implements UserDAO {

    private final String[] createStatements = {
    """
          CREATE TABLE IF NOT EXISTS users (
          `id` int NOT NULL AUTO_INCREMENT,
          `username` varchar(256) NOT NULL,
          `password` varchar(256) NOT NULL,
          `email` varchar(256),
          `json` TEXT DEFAULT NULL,
          PRIMARY KEY (`id`),
          INDEX(username)
          )
    """
    };

    //private final HashMap<String, UserData> db = new HashMap<String, UserData>();
    @Override
    public UserData getUser(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, json FROM users WHERE id=?";
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

    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        return false;
    }

    @Override
    public void clear() {

    }
}