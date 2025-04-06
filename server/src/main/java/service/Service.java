package service;
import dataaccess.*;
import model.*;
import java.util.UUID;
import java.util.HashSet;
import java.util.Random;
import chess.ChessGame;
import chess.ChessBoard;

public class Service {

    public static class UserService {
        public static UserDAO userDAO;
        public static AuthDAO authDAO;
        public UserService(UserDAO userDAO, AuthDAO authDAO){
            UserService.userDAO = userDAO;
            UserService.authDAO = authDAO;
        }
        public AuthData register(UserData userData) throws DataAccessException {
            userDAO.createUser(userData);
            String username = userData.getUsername();;
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(username, authToken);
            authDAO.createAuth(authData);
            return authData;
        }
        public AuthData login(String username, String password) throws DataAccessException {
            boolean authenticate = userDAO.authUser(username, password);
            if (!authenticate) {
                throw new DataAccessException("Incorrect password");
            }
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(username, authToken);
            authDAO.createAuth(authData);
            return authData;
        }
        public void logout(String authToken) throws DataAccessException {
            try {
                authDAO.getAuth(authToken);
            } catch (DataAccessException e) {
                throw new DataAccessException("Failed to logout");
            }
            authDAO.removeAuth(authToken);
        }
        public void clear() throws DataAccessException {
            userDAO.clear();
            authDAO.clear();
        }
    }

    public static class GameService {
        public static GameDAO gameDAO;
        public static AuthDAO authDAO;

        public GameService(GameDAO gameDAO, AuthDAO authDAO) {
            this.gameDAO = gameDAO;
            this.authDAO = authDAO;
        }

        public HashSet<GameData> listGames(String authToken) throws DataAccessException {
            authDAO.getAuth(authToken);
            return gameDAO.getGameList();
        }

        public int createGame(String gameName, String authToken) throws DataAccessException {
            authDAO.getAuth(authToken);
            Random random = new Random();
            int gameID;
            do {
                gameID = random.nextInt(1000) + 1;
            } while (gameDAO.gameExists(gameID));
            ChessGame chessGame = new ChessGame();
            ChessBoard chessBoard = new ChessBoard();
            chessBoard.resetBoard();
            chessGame.setBoard(chessBoard);
            GameData gameData = new GameData(gameID, null, null, gameName, chessGame);
            gameDAO.addGame(gameData);
            return gameID;
        }

        public boolean joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
            AuthData authData = authDAO.getAuth(authToken);
            GameData game = gameDAO.getGame(gameID);
            String currentWhite = game.getWhiteUsername();
            String currentBlack = game.getBlackUsername();
            if (playerColor.equalsIgnoreCase("white") ||
                    playerColor.equalsIgnoreCase("black")) {
                if (playerColor.equalsIgnoreCase("white")) {
                    if (currentWhite == null) {
                        currentWhite = authData.getUsername();
                    } else {
                        return false;
                    }
                }
                if (playerColor.equalsIgnoreCase("black")) {
                    if (currentBlack == null) {
                        currentBlack = authData.getUsername();
                    } else {
                        return false;
                    }
                }
            } else {
                throw new DataAccessException("Invalid color");
            }
            GameData gameData = new GameData(gameID, currentWhite, currentBlack, game.getGameName(), game.getGame());
            gameDAO.updateGame(gameData);
            return true;
        }

        public void clear() {
            gameDAO.clear();
        }
    }
}