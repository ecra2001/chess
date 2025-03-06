package dataaccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
    HashSet<GameData> getGameList();
    void addGame(GameData gameData) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    boolean gameExists(int gameID);
    void updateGame(GameData gameData) throws DataAccessException;
    void clear();
}