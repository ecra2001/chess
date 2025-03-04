package dataaccess;

import model.GameData;
import java.util.HashSet;

public interface GameDAO {
    HashSet<GameData> listGames() throws DataAccessException;
    void addGame(GameData game) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
    boolean gameExists(int gameID) throws DataAccessException;
    void clear();
}