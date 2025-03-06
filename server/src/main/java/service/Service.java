package service;
import dataaccess.*;
import model.*;
import java.util.UUID;
import java.util.HashSet;

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
        AuthData login(String username, String password) throws DataAccessException {
            boolean authenticate = userDAO.authUser(username, password);
            if (!authenticate) {
                throw new DataAccessException("Incorrect password");
            }
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(username, authToken);
            authDAO.createAuth(authData);
            return authData;
        }
        void logout(String authToken) throws DataAccessException {
            AuthData authData = authDAO.getAuth(authToken);
            authDAO.removeAuth(authData.getAuthToken());
        }
        void clear(){
            userDAO.clear();
            authDAO.clear();
        }
    }

    public static class GameService {
        GameDAO gameDAO;
        AuthDAO authDAO;
        HashSet<GameData> listGames(String authToken) throws DataAccessException {
            AuthData authData = authDAO.getAuth(authToken);
            return gameDAO.getGameList();
        }

        int createGame(String gameName, String AuthToken) {
            return 0;
        }

        boolean joinGame(String authToken, int gameID, String playerColor) {
            return false;
        }

        void clear() {
            gameDAO.clear();
        }
    }
}