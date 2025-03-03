package dataaccess;

import model.GameData;
import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {
    private final HashSet<GameData> db = new HashSet<>();

    @Override
    public HashSet<GameData> listGames() {
        return new HashSet<>(db);
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {
        if (gameExists(game.getGameID())) {
            throw new DataAccessException("Game ID already taken.");
        }
        db.add(game);
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        for (GameData game : db) {
            if (game.getGameID() == gameID) {
                return game;
            }
        }
        throw new DataAccessException("Game with ID " + gameID + " not found.");
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        GameData existingGame = getGame(game.getGameID());
        db.remove(existingGame);
        db.add(game);
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
    public void clear() {
        db.clear();
    }
}