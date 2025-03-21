package client;

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


    @Test
    public void registerPositive() throws ResponseException {
        UserData userData = new UserData("player1", "password", "p1@email.com");
        //Assertions.assertTrue(serverFacade.register(new UserData("username", "password", "email")));
        var authData = serverFacade.register(userData);
        assertTrue(authData.getAuthToken().length() > 10);
    }

}
