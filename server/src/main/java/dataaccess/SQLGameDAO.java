package dataaccess;

import chess.ChessGame;
import java.util.HashSet;
import model.GameData;

public class SQLGameDAO implements GameDAO {

    @Override
    public HashSet<GameData> listGames() throws DataAccessException{
        return null;
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {

    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public boolean gameExists(int gameID) throws DataAccessException{
        return false;
    }

    @Override
    public void clear() {

    }

    private final String[] createStatements = {
            """
            CREATE TABLE if NOT EXISTS game (
                    gameID INT NOT NULL,
                    whiteUsername VARCHAR(255),
                    blackUsername VARCHAR(255),
                    gameName VARCHAR(255),
                    chessGame TEXT,
                    PRIMARY KEY (gameID)
            )"""
    };
}