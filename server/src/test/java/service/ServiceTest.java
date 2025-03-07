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
    private UserDAO userDAO;
    private AuthDAO authDAO;
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
}