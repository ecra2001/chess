package server;

import com.google.gson.Gson;
import spark.*;
import dataaccess.*;
import model.*;
import service.Service;

public class Server {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    Service.UserService userService;
    Service.GameService gameService;
    public Server() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();
        userService = new Service.UserService(userDAO, authDAO);
        gameService = new Service.GameService(gameDAO, authDAO);
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.get("/game", this::listGames);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object register(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    private Object login(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    private Object logout(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }

    private Object clear(Request req, Response res) {
        try {
            gameService.clear();
            userService.clear();
            res.status(200);
            return "{}";
        } catch (Exception e) {
            res.status(500);
            return "{ \"message\": \"Error: " + e.getMessage() + "\" }";
        }
    }
}
