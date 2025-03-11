package dataaccess;

import model.AuthData;
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
          CREATE TABLE IF NOT EXISTS chessGames (
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
            var statement = "SELECT id, whiteUsername, blackUsername, gameName, game FROM chessGames";
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
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "INSERT INTO chessGames (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameData.getGameID());
                ps.setString(2, gameData.getWhiteUsername());
                ps.setString(3, gameData.getBlackUsername());
                ps.setString(4, gameData.getGameName());
                var json = new Gson().toJson(gameData.getGame());
                ps.setString(5, json);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to add game to database");
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID, whiteUsername, blackUsername, gameName, game FROM chessGames WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var white = rs.getString("whiteUsername");
                        var black = rs.getString("blackUsername");
                        var gameName = rs.getString("gameName");
                        var json = rs.getString("game");
                        var chessGame = new Gson().fromJson(json, ChessGame.class);
                        return new GameData(gameID, white, black, gameName, chessGame);
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Failed to get game from Database");
        }
        throw new DataAccessException("GameID does not exist");
    }

    @Override
    public boolean gameExists(int gameID) {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT gameID from chessGames WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "UPDATE chessGames SET whiteUsername=?, blackUsername=?, gameName=?, game=? WHERE gameID=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, gameData.getWhiteUsername());
                ps.setString(2, gameData.getBlackUsername());
                ps.setString(3, gameData.getGameName());
                var json = new Gson().toJson(gameData.getGame());
                ps.setString(4, json);
                ps.setInt(5, gameData.getGameID());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void clear() {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "TRUNCATE chessGames";
            try (var ps = conn.prepareStatement(statement)) {
                ps.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}