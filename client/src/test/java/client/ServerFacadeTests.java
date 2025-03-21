package client;

import exception.ResponseException;
import org.junit.jupiter.api.*;
import server.Server;
import model.*;

public class ServerFacadeTests {

    private static Server server;
    private ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositive() throws ResponseException {
        //Assertions.assertTrue(serverFacade.register(new UserData("username", "password", "email")));
    }

}
