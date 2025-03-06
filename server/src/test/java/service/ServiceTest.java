package service;

import dataaccess.DataAccessException;
import dataaccess.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import model.*;

public class ServiceTest{
    private Service.UserService userService;
    private Service.GameService gameService;
    private UserData userData;
    private AuthData authData;
    UserDAO userDAO;
    @BeforeEach
    public void setup() {
        userService = new Service.UserService();
        userData = new UserData("username", "password", "email");
        authData = new AuthData("username", "authToken");
    }

    @Test
    public void registerPositive() throws DataAccessException {
        AuthData authData = userService.register(userData);
        Assertions.assertEquals(authData,userService.register(userData));
    }

    @Test
    public void registerNegative(){
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(userData));
    }
}