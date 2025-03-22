package client;

import dataaccess.DataAccessException;
import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import model.*;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        var url = "http://localhost:" + port;
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade(url);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        server.clear();
    }

    @Test
    public void registerPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        var authData = serverFacade.register(userData);
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    public void registerNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        serverFacade.register(userData);
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.register(userData));
    }

    @Test
    public void loginPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        serverFacade.register(userData);
        var authData = serverFacade.login(userData.getUsername(), userData.getPassword());
        assertTrue(authData.getAuthToken().length() > 10);
    }

    @Test
    public void loginNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        serverFacade.register(userData);
        UserData badUser = new UserData("player2", "pass", "p1@email.com");
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.login(badUser.getUsername(), badUser.getPassword()));
    }

    @Test
    public void logoutPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = serverFacade.register(userData);
        Assertions.assertDoesNotThrow(() -> serverFacade.logout(authData.getAuthToken()));
    }

    @Test
    public void logoutNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.logout("badAuth"));
    }

    @Test
    public void listGamesPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = serverFacade.register(userData);
        serverFacade.createGame("game1", authData.getAuthToken());
        Assertions.assertDoesNotThrow(() -> serverFacade.listGames(authData.getAuthToken()));
    }

    @Test
    public void listGamesNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.listGames("badAuth"));
    }

    @Test
    public void createGamePositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = serverFacade.register(userData);
        GameData gameData = new GameData(123, "white", "black", "game1", null);
        Assertions.assertDoesNotThrow(() -> serverFacade.createGame(gameData.getGameName(), authData.getAuthToken()));
    }

    @Test
    public void createGameNegative() {
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.createGame("game2", "badAuth"));
    }

    @Test
    public void joinGamePositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = serverFacade.register(userData);
        int gameID = serverFacade.createGame("game1", authData.getAuthToken());
        //Assertions.assertTrue(serverFacade.joinGame(authData.getAuthToken(), gameID, "white"));
        Assertions.assertDoesNotThrow(() -> serverFacade.joinGame(authData.getAuthToken(), gameID, "white"));
    }

    @Test
    public void joinGameNegative() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        AuthData authData = serverFacade.register(userData);
        int gameID = serverFacade.createGame("game1", authData.getAuthToken());
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.joinGame("badAuth", gameID, "white"));
    }
}
