package dataaccess;

import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {

    @Override
    public void createAuth(AuthData authData) {

    }

    @Override
    public AuthData getAuth(String authToken) {
        return null;
    }

    @Override
    public void removeAuth(String authToken) {

    }

    @Override
    public void clear() {

    }
}