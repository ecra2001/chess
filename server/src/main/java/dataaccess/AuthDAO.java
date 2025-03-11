package dataaccess;

import model.AuthData;

public interface AuthDAO {
    void createAuth(AuthData authData);
    AuthData getAuth(String authToken) throws DataAccessException;
    void removeAuth(String authToken);
    void clear() throws DataAccessException;
}