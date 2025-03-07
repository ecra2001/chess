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
    private UserDAO userDAO = new MemoryUserDAO();
    private AuthDAO authDAO = new MemoryAuthDAO();
    private GameDAO gameDAO = new MemoryGameDAO();
    @BeforeEach
    public void setup() {
        userService = new Service.UserService();
        userData = new UserData("username", "password", "email");
        authData = new AuthData("username", "authToken");
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
    public void logoutNegative() {

    }

    @Test
    public void clearTest() {

    }

    @Test
    public void listGamesPositive() {

    }

    @Test
    public void listGamesNegative() {

    }

    @Test
    public void createGamePositive() {

    }

    @Test
    public void createGameNegative() {

    }

    @Test
    public void joinGamePositive() {

    }

    @Test
    public void joinGameNegative() {

    }
}