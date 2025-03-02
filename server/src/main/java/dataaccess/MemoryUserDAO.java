package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> db = new HashMap<>();

    @Override
    public void createUser(UserData user) throws DataAccessException {
        if (db.containsKey(user.getUsername())) {
            throw new DataAccessException("User already exists.");
        }
        db.put(user.getUsername(), user);
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
    }

    @Override
    public boolean authUser(String username, String password) throws DataAccessException {
    }

    @Override
    public void clear() {
        db.clear();
    }
}