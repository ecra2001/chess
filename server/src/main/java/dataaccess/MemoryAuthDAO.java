package dataaccess;

import model.AuthData;

import java.util.HashMap;

public class MemoryAuthDAO implements AuthDAO {
    private final HashMap<String, AuthData> db = new HashMap<String, AuthData>();
    @Override
    public void createAuth(AuthData authData) {
        String authToken = authData.getAuthToken();
        db.put(authToken, authData);
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        if (!db.containsKey(authToken)) {
            throw new DataAccessException("authToken not found");
        }
        return db.get(authToken);
    }

    @Override
    public void removeAuth(String authToken) {
        db.remove(authToken);
    }

    @Override
    public void clear() {
        db.clear();
    }
}