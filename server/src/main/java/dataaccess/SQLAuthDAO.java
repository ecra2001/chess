package dataaccess;

import model.AuthData;

public class SQLAuthDAO implements AuthDAO {

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() {

    }

    @Override
    public boolean authTokenExists(String authToken) throws DataAccessException {
        return false;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE if NOT EXISTS auth (
                    username VARCHAR(255) NOT NULL,
                    authToken VARCHAR(255) NOT NULL,
                    PRIMARY KEY (authToken)
            )"""
    };
}