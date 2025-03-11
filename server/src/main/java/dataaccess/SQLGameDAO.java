package dataaccess;

import model.GameData;

import java.sql.SQLException;
import java.util.HashSet;

public class SQLGameDAO implements GameDAO {
    public SQLGameDAO() {
        configureDatabase();
    }
    private final String[] createStatements = {
            """
          CREATE TABLE IF NOT EXISTS game (
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
        return null;
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