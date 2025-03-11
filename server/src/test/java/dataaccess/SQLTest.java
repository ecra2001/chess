package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import java.util.HashSet;
import chess.ChessGame;

class SQLTest {
    private SQLUserDAO sqlUserDAO;
    private SQLAuthDAO sqlAuthDAO;
    private SQLGameDAO sqlGameDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        sqlUserDAO = new SQLUserDAO();
        sqlAuthDAO = new SQLAuthDAO();
        sqlGameDAO = new SQLGameDAO();
        sqlUserDAO.clear();
        sqlAuthDAO.clear();
        sqlGameDAO.clear();
        UserData user = new UserData("username", "password", "email");
        sqlUserDAO.createUser(user);
        AuthData auth = new AuthData("username", "authToken");
        sqlAuthDAO.createAuth(auth);
        ChessGame chessGame = new ChessGame();
        GameData game = new GameData(123, "white", "black", "game", chessGame);
        sqlGameDAO.addGame(game);
    }

    @Test
    void getUserPositive() throws DataAccessException {
        Assertions.assertNotNull(sqlUserDAO.getUser("username"));
        UserData userData = sqlUserDAO.getUser("username");
        Assertions.assertEquals("username", userData.getUsername());
        Assertions.assertEquals("email", userData.getEmail());
        Assertions.assertTrue(sqlUserDAO.authUser("username", "password"));
    }

    @Test
    void getUserNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser("wrongUsername"));
    }

    @Test
    void createUserPositive() throws DataAccessException {
        UserData userData = sqlUserDAO.getUser("username");
        Assertions.assertEquals("username", userData.getUsername());
        Assertions.assertEquals("email", userData.getEmail());
        Assertions.assertTrue(sqlUserDAO.authUser("username", "password"));
    }

    @Test
    void createUserNegative() {
        UserData sameUser = new UserData("username", "password", "email");
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.createUser(sameUser));
    }

    @Test
    void authUserPositive() throws DataAccessException {
        Assertions.assertTrue(sqlUserDAO.authUser("username", "password"));
    }

    @Test
    void authUserNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.authUser("badUsername", "password"));
        Assertions.assertFalse(sqlUserDAO.authUser("username", "badPassword"));
    }

    @Test
    void clearUserTest() throws DataAccessException {
        sqlUserDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser("username"));
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        AuthData authData = sqlAuthDAO.getAuth("authToken");
        Assertions.assertEquals("username", authData.getUsername());
        Assertions.assertEquals("authToken", authData.getAuthToken());
    }

    @Test
    void createAuthNegative() {
        AuthData sameAuth = new AuthData("username", "authToken");
        Assertions.assertThrows(RuntimeException.class, () -> sqlAuthDAO.createAuth(sameAuth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        Assertions.assertNotNull(sqlAuthDAO.getAuth("authToken"));
        AuthData authData = sqlAuthDAO.getAuth("authToken");
        Assertions.assertEquals("username", authData.getUsername());
        Assertions.assertEquals("authToken", authData.getAuthToken());
    }

    @Test
    void getAuthNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth("wrongAuthToken"));
    }

    @Test
    void removeAuthPositive() {
        sqlAuthDAO.removeAuth("authToken");
        Assertions.assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth("authToken"));
    }

    @Test
    void removeAuthNegative() {
        Assertions.assertDoesNotThrow(() -> sqlAuthDAO.removeAuth("badAuthToken"));
    }

    @Test
    void clearAuthTest() throws DataAccessException {
        sqlAuthDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth("authToken"));
    }

    @Test
    void getGameListPositive() throws DataAccessException {
        GameData game1 = new GameData(234, null, null, "game1", null);
        GameData game2 = new GameData(345, null, null, "game2", null);
        GameData game3 = new GameData(456, null, null, "game3", null);
        sqlGameDAO.addGame(game1);
        sqlGameDAO.addGame(game2);
        sqlGameDAO.addGame(game3);
        Assertions.assertNotNull(sqlGameDAO.getGameList());
    }

    @Test
    void getGameListNegative() {
        sqlGameDAO.clear();
        Assertions.assertDoesNotThrow(() -> sqlGameDAO.getGameList());
    }

    @Test
    void addGamePositive() throws DataAccessException {
        GameData gameData = sqlGameDAO.getGame(123);
        Assertions.assertEquals("white", gameData.getWhiteUsername());
        Assertions.assertEquals("black", gameData.getBlackUsername());
        Assertions.assertEquals("game", gameData.getGameName());
        Assertions.assertEquals("game", gameData.getGameName());
        Assertions.assertNotNull(gameData.getGame());
    }

    @Test
    void addGameNegative() {
        GameData sameGame = new GameData(123, null, null, "game", null);
        Assertions.assertThrows(DataAccessException.class, () -> sqlGameDAO.addGame(sameGame));
    }

    @Test
    void getGamePositive() {

    }

    @Test
    void getGameNegative() {

    }

    @Test
    void gameExistsPositive() {

    }

    @Test
    void gameExistsNegative() {

    }

    @Test
    void updateGamePositive() {

    }

    @Test
    void updateGameNegative() {

    }

    @Test
    void clearGameTest() {
        sqlGameDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> sqlGameDAO.getGame(123));
    }
}