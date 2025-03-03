package dataaccess;

import model.UserData;

public class SQLUserDAO implements UserDAO {

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null; //temporary
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
        return false; //temporary
    }

    @Override
    public void clear() throws DataAccessException {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE if NOT EXISTS user (
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    email VARCHAR(255),
                    PRIMARY KEY (username)
            )"""
    };
}