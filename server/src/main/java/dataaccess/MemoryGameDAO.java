package dataaccess;

import chess.ChessPiece;
import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {
    HashSet<GameData> db = new HashSet<GameData>();
    @Override
    public HashSet<GameData> getGameList() {
        return db;
    }

    @Override
    public void addGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.getGameID();
        if (gameExists(gameID)){
            throw new DataAccessException("GameID already exists");
        }
        db.add(gameData);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : db) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game not found");
    }

    @Override
    public boolean gameExists(int gameID) {
        for (GameData game : db) {
            if (game.getGameID() == gameID) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void updateGame(GameData gameData) throws DataAccessException {
        int gameID = gameData.getGameID();
        GameData outdatedGame = getGame(gameID);
        db.remove(outdatedGame);
        db.add(gameData);
    }

    @Override
    public void clear() {
        db.clear();
    }
}
