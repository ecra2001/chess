package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.HashSet;
import java.util.UUID;

public class Service {
    public static class UserService {
        UserDAO userDAO;
        AuthDAO authDAO;
        public UserService(UserDAO userDAO, AuthDAO authDAO) {
            this.userDAO = userDAO;
            this.authDAO = authDAO;
        }

        public AuthData createUser(UserData userData) throws DataAccessException {
            try {
                userDAO.createUser(userData);
            } catch (DataAccessException e) {
                throw new DataAccessException("Error making user.");
            }
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(userData.getUsername(), authToken);
            authDAO.createAuth(authData);
            return authData;
        }

        public AuthData loginUser(UserData userData) throws DataAccessException {
            boolean userAuthenticated;
            try {
                userAuthenticated = userDAO.authUser(userData.getUsername(), userData.getPassword());
            } catch (DataAccessException e) {
                throw new DataAccessException("Error login in");
            }

            if (userAuthenticated) {
                String authToken = UUID.randomUUID().toString();
                AuthData authData = new AuthData(userData.getUsername(), authToken);
                authDAO.createAuth(authData);
                return authData;
            }
            else {
                throw new DataAccessException("Error creating AuthData");
            }
        }

        public void logoutUser(String authToken) throws DataAccessException {
            try {
                authDAO.getAuth(authToken);
            } catch (DataAccessException e) {
                throw new DataAccessException("Error logging out");
            }
            authDAO.deleteAuth(authToken);
        }


        public void clear() {

        }
    }

    public static class GameService {
        GameDAO gameDAO;
        AuthDAO authDAO;
        public GameService(GameDAO gameDAO, AuthDAO authDAO){
            this.gameDAO = gameDAO;
            this.authDAO = authDAO;
        }
        public HashSet<GameData> listGames(String authToken) throws DataAccessException {
            return null;
        }

        public int createGame(String authToken) throws DataAccessException {
            return 0;
        }

        /***
         * Returns an int based on successfulness of joining the game.
         *     0 = Success
         *     1 = Game does not exist or other bad request
         *     2 = Player color already taken
         *     Throws UnauthorizedException if invalid authToken
         *     Throws DataAccessException if the request is bad
         */
        public int joinGame(String authToken, int gameID, String color) throws  DataAccessException {
            return 0;
        }

        public static void clear(GameDAO gameDAO) {

        }
    }
}