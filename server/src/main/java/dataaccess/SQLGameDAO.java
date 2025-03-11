package dataaccess;

import model.GameData;
import chess.ChessGame;
import java.sql.SQLException;
import java.util.HashSet;
import com.google.gson.Gson;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
          CREATE TABLE IF NOT EXISTS games (
          `gameID` int NOT NULL,
          `whiteUsername` varchar(256),
          `blackUsername` varchar(256),
          `gameName` varchar(256),
          `game` TEXT DEFAULT NULL,
          PRIMARY KEY (`gameID`)
          )
    """
    };

    private void configureDatabase() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public HashSet<GameData> getGameList() {
        var list = new HashSet<GameData>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()){
                        var gameID = rs.getInt("gameID");
                        var whiteUsername = rs.getString("whiteUsername");
                        var blackUsername = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var json = rs.getString("game");
                        var chessGame = new Gson().fromJson(json, ChessGame.class);
                        list.add(new GameData(gameID, whiteUsername, blackUsername,gameName, chessGame));
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public boolean gameExists(int gameID) {
        return false;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void clear() {

    }
}