package dataaccess;

import model.UserData;

public interface UserDAO {
    UserData getUser(String username);
    void createUser(UserData userData);
    boolean authUser(String username, String password);
    void clear();
}