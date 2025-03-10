package dataaccess;

import model.GameData;

import java.util.HashSet;

public class SQLGameDAO implements GameDAO {

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