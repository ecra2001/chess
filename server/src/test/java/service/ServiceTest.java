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

    }
    @Test
    void testLoginUserNegative() throws DataAccessException {

    }
    @Test
    void testLogoutUserPositive() throws DataAccessException {

    }
    @Test
    void testLogoutUserNegative() throws DataAccessException {

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

    }
    @Test
    void testListGamesNegative() throws DataAccessException {

    }
    @Test
    void testCreateGamePositive() throws DataAccessException {

    }
    @Test
    void testCreateGameNegative() throws DataAccessException {

    }
    @Test
    void testJoinGamePositive() throws DataAccessException {

    }
    @Test
    void testJoinGameNegative() throws DataAccessException {

    }
}