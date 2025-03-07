package service;

import dataaccess.DataAccessException;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

public class ServiceTest{
    private Service.UserService userService;
    private Service.GameService gameService;
    private UserData userData;
    private AuthData authData;
    private GameData gameData;
    private final UserDAO userDAO = new MemoryUserDAO();
    private final AuthDAO authDAO = new MemoryAuthDAO();
    private final GameDAO gameDAO = new MemoryGameDAO();
    @BeforeEach
    public void setup() {
        userService = new Service.UserService(userDAO, authDAO);
        gameService = new Service.GameService(gameDAO, authDAO);
        userData = new UserData("username", "password", "email");
        authData = new AuthData("username", "authToken");
        authDAO.createAuth(authData);
    }

    @Test
    public void registerPositive() throws DataAccessException {
        AuthData mockAuthData = userService.register(userData);
        Assertions.assertEquals(authData.getUsername(), mockAuthData.getUsername());
        Assertions.assertNotNull(mockAuthData);
    }

    @Test
    public void registerNegative() throws DataAccessException {
        userService.register(userData);
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(userData));
    }

    @Test
    public void loginPositive() throws DataAccessException {
        userService.register(userData);
        AuthData mockAuthData = userService.login(userData.getUsername(), userData.getPassword());
        Assertions.assertEquals(authData.getUsername(), mockAuthData.getUsername());
        Assertions.assertNotNull(mockAuthData);
    }

    @Test
    public void loginNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () ->
                userService.login(userData.getUsername(), userData.getPassword()));
        userService.register(userData);
        UserData badUserData = new UserData("username", "wrongPass", "email");
        Assertions.assertThrows(DataAccessException.class, () ->
                userService.login(badUserData.getUsername(), badUserData.getPassword()));
    }

    @Test
    public void logoutPositive() throws DataAccessException {
        AuthData mockAuthData = userService.register(userData);
        userService.logout(mockAuthData.getAuthToken());
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(mockAuthData.getAuthToken()));
    }

    @Test
    public void logoutNegative() throws DataAccessException {
        userService.register(userData);
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout("badAuthToken"));
    }

    @Test
    public void clearTest() throws DataAccessException {
        AuthData mockAuthData = userService.register(userData);
        gameService.createGame("GameName", mockAuthData.getAuthToken());
        userService.clear();
        gameService.clear();
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(userData.getUsername()));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(mockAuthData.getAuthToken()));

    }

    @Test
    public void listGamesPositive() throws DataAccessException {
        AuthData mockAuthData = userService.register(userData);
        gameService.createGame("GameName", mockAuthData.getAuthToken());
        Assertions.assertNotNull(gameService.listGames(mockAuthData.getAuthToken()));
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames("badAuthToken"));
    }

    @Test
    public void createGamePositive() throws DataAccessException {
        AuthData mockAuthData = userService.register(userData);
        int gameID1 = gameService.createGame("GameName", mockAuthData.getAuthToken());
        int gameID2 = gameService.createGame("GameName", mockAuthData.getAuthToken());
        Assertions.assertTrue(gameID1 != gameID2);
    }

    @Test
    public void createGameNegative() {
        Assertions.assertThrows(DataAccessException.class, () ->
                gameService.createGame("GameName", "badAuthToken"));
    }

    @Test
    public void joinGamePositive() throws DataAccessException {
        int gameID = gameService.createGame("GameName", authData.getAuthToken());
        gameService.joinGame(authData.getAuthToken(), gameID, "WHITE");
        GameData compareGame = new GameData(gameID, authData.getUsername(), null, "GameName", null);
        Assertions.assertEquals(compareGame, gameDAO.getGame(gameID));
    }

    @Test
    public void joinGameNegative() throws DataAccessException {
        int gameID = gameService.createGame("GameName", authData.getAuthToken());
        Assertions.assertThrows(DataAccessException.class, () ->
                gameService.joinGame("badAuthToken", gameID, "WHITE"));
        Assertions.assertThrows(DataAccessException.class, () ->
                gameService.joinGame(authData.getAuthToken(), 0, "WHITE"));
        Assertions.assertThrows(DataAccessException.class, () ->
                gameService.joinGame(authData.getAuthToken(), gameID, "GREEN"));
    }
}