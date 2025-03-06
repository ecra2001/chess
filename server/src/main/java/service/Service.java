package service;
import dataaccess.*;
import model.*;
import java.util.UUID;

public class Service {

    public static class UserService {
        UserDAO userDAO;
        AuthDAO authDAO;
        AuthData register(UserData userData){
            return null;
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