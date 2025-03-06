package dataaccess;

import model.UserData;
import java.util.HashMap;

public class MemoryUserDAO implements UserDAO {

    private final HashMap<String, UserData> db = new HashMap<String, UserData>();
    @Override
    public UserData getUser(String username) throws DataAccessException {
        if (!db.containsKey(username)) {
            throw new DataAccessException("User not found");
        }
        return db.get(username);
    }

    @Override
    public void createUser(UserData userData) throws DataAccessException {
        String username = userData.getUsername();
        if (db.containsKey(username)) {
            throw new DataAccessException("Username taken");
        }
        db.put(username, userData);
    }

    @Override
    public boolean authUser(String username, String password) {
        return false;
    }

    @Override
    public void clear() {

    }
}