package dataaccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
    HashSet<GameData> getGameList();
    void addGame(GameData gameData);
    GameData getGame(int gameID);
    boolean gameExists(int gameID);
    void updateGame(GameData gameData);
    void clear();
}