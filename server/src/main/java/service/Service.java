package service;
import dataaccess.*;
import model.*;
import java.util.UUID;

public class Service {

    public static class UserService {
        UserDAO userDAO;
        AuthDAO authDAO;
        AuthData register(UserData userData) throws DataAccessException {
            userDAO.createUser(userData);
            String username = userData.getUsername();;
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(username, authToken);
            authDAO.createAuth(authData);
            return authData;
        }
        AuthData login(String username, String password){
            return null;
        }
        void logout(String authToken) {

        }
        void clear(){
            userDAO.clear();
            authDAO.clear();
        }
    }

    public static class GameService {

    }
}