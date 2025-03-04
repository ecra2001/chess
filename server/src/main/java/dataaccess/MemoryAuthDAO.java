package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO {
    private final Map<String, AuthData> db = new HashMap<>();

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!db.containsKey(authToken)) {
            throw new DataAccessException("Auth token not found");
        }
        return db.get(authToken);
    }

    @Override
    public void createAuth(AuthData authData) {
        db.put(authData.getAuthToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        db.remove(authToken);
    }

    @Override
    public void clear() {
        db.clear();
    }

    @Override
    public boolean authTokenExists(String authToken) throws DataAccessException {
        return db.containsKey(authToken);
    }
}