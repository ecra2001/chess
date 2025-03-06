package dataaccess;

import model.GameData;

import java.util.HashSet;

public class MemoryGameDAO implements GameDAO {

    @Override
    public HashSet<GameData> getGameList() {
        return null;
    }

    @Override
    public void addGame(GameData gameData) {

    }

    @Override
    public GameData getGame(int gameID) {
        return null;
    }

    @Override
    public boolean gameExists(int gameID) {
        return false;
    }

    @Override
    public void updateGame(GameData gameData) {

    }

    @Override
    public void clear() {

    }
}
