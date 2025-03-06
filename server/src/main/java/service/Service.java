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
            authDAO.getAuth(authToken);
            authDAO.removeAuth(authToken);
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
            authDAO.getAuth(authToken);
            return gameDAO.getGameList();
        }

        int createGame(String gameName, String authToken) throws DataAccessException {
            authDAO.getAuth(authToken);
            Random random = new Random();
            int gameID;
            do {
                gameID = random.nextInt(1000) + 1;
            } while (gameDAO.gameExists(gameID));
            ChessBoard chessBoard = new ChessBoard();
            ChessGame chessGame = new ChessGame();
            chessBoard.resetBoard();
            chessGame.setBoard(chessBoard);
            GameData gameData = new GameData(gameID, null, null, gameName, null);
            gameDAO.addGame(gameData);
            return gameID;
        }

        boolean joinGame(String authToken, int gameID, String playerColor) throws DataAccessException {
            AuthData authData = authDAO.getAuth(authToken);
            GameData game = gameDAO.getGame(gameID);
            String currentWhite = game.getWhiteUsername();
            String currentBlack = game.getBlackUsername();
            if (playerColor.equalsIgnoreCase("white") ||
                    playerColor.equalsIgnoreCase("black")) {
                if (playerColor.equalsIgnoreCase("white")) {
                    if (currentWhite == null){
                        currentWhite = authData.getUsername();
                    } else {
                        return false;
                    }
                }
                if (playerColor.equalsIgnoreCase("black")) {
                    if (currentBlack == null){
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

        void clear() {
            gameDAO.clear();
        }
    }
}