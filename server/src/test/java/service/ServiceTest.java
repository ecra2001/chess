package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Service;

import java.util.HashSet;

public class ServiceTest {
    static UserDAO userDAO;
    static AuthDAO authDAO;
    static GameDAO gameDAO;
    static Service.UserService userService;
    static Service.GameService gameService;
    static UserData defaultUser;
    static AuthData authData;

    @BeforeAll
    static void init() throws DataAccessException {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        gameService = new Service.GameService(gameDAO, authDAO);
        userService = new Service.UserService(userDAO, authDAO);
        authData = new AuthData("Username", "authToken");
        authDAO.createAuth(authData);
    }
    @BeforeEach
    void setup() throws DataAccessException {
        defaultUser = new UserData("Username", "password", "email");
        gameDAO.clear();
        userDAO.clear();
        //authDAO.clear();
    }
    @Test
    void testCreateUserPositive() throws DataAccessException {
        AuthData auth = userService.createUser(defaultUser);
        Assertions.assertEquals(authDAO.getAuth(auth.getAuthToken()), auth);
    }
    @Test
    void testCreateUserNegative() throws DataAccessException {
        userService.createUser(defaultUser);
        Assertions.assertThrows(DataAccessException.class, () -> userService.createUser(defaultUser));
    }
    @Test
    void testLoginUserPositive() throws DataAccessException {
        userService.createUser(defaultUser);
        AuthData auth = userService.loginUser(defaultUser);
        Assertions.assertEquals(authDAO.getAuth(auth.getAuthToken()), auth);
    }
    @Test
    void testLoginUserNotExist() throws DataAccessException{
        Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(defaultUser));
    }
    @Test
    void testLoginUserBadPassword() throws DataAccessException {
        userService.createUser(defaultUser);
        UserData badPassUser = new UserData(defaultUser.getUsername(), "badPassword", defaultUser.getEmail());
        Assertions.assertThrows(DataAccessException.class, () -> userService.loginUser(badPassUser));
    }
    @Test
    void testLogoutUserPositive() throws DataAccessException {
        AuthData auth = userService.createUser(defaultUser);
        userService.logoutUser(auth.getAuthToken());
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.getAuthToken()));
    }
    @Test
    void testLogoutUserNegative() throws DataAccessException {
        AuthData auth = userService.createUser(defaultUser);
        Assertions.assertThrows(DataAccessException.class, () -> userService.logoutUser("badAuthToken"));
    }
    @Test
    void testClear() throws DataAccessException {
        AuthData auth = userService.createUser(defaultUser);
        gameService.createGame(authData.getAuthToken(), "name");
        userService.clear();
        Service.GameService.clear(gameDAO);
        Assertions.assertThrows(DataAccessException.class, () -> userDAO.getUser(defaultUser.getUsername()));
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.getAuth(auth.getAuthToken()));
        Assertions.assertEquals(gameDAO.listGames(), HashSet.newHashSet(0));
    }
    @Test
    void testListGamesPositive() throws DataAccessException {
        int gameID1 = gameService.createGame(authData.getAuthToken(), "game1");
        int gameID2 = gameService.createGame(authData.getAuthToken(), "game2");
        int gameID3 = gameService.createGame(authData.getAuthToken(), "game3");
        HashSet<GameData> gameList = HashSet.newHashSet(3);
        gameList.add(new GameData(gameID1, null, null, "game1", null));
        gameList.add(new GameData(gameID2, null, null, "game2", null));
        gameList.add(new GameData(gameID3, null, null, "game3", null));
        Assertions.assertEquals(gameList, gameService.listGames(authData.getAuthToken()));
    }
    @Test
    void testListGamesNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> gameService.listGames("badToken"));
    }
    @Test
    void testCreateGamePositive() throws DataAccessException {
        int game1 = gameService.createGame(authData.getAuthToken(), "game1");
        Assertions.assertTrue(gameDAO.gameExists(game1));
        int game2 = gameService.createGame(authData.getAuthToken(), "game2");
        Assertions.assertNotEquals(game1, game2);
    }
    @Test
    void testCreateGameNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> gameService.createGame("diffAuth", "game"));
    }
    @Test
    void testJoinGamePositive() throws DataAccessException {
        int game = gameService.createGame(authData.getAuthToken(), "player");
        gameService.joinGame(authData.getAuthToken(), game, "WHITE");
        GameData testGameData = new GameData(game, authData.getUsername(), null, "player", null);
        Assertions.assertEquals(testGameData, gameDAO.getGame(game));
    }
    @Test
    void testJoinGameNegative() throws DataAccessException {
        int game = gameService.createGame(authData.getAuthToken(), "player");
        int diffGameId = 123;
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(authData.getAuthToken(), diffGameId, "WHITE"));
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame("diffAuth", game, "WHITE"));
        Assertions.assertThrows(DataAccessException.class, () -> gameService.joinGame(authData.getAuthToken(), game, "BLUE"));
    }
}