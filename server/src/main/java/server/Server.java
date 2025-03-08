package server;

import com.google.gson.Gson;
import spark.*;
import dataaccess.*;
import model.*;
import service.Service;
import java.util.Map;

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
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = userService.register(userData);
            if (userData.getUsername() == null || userData.getPassword() == null || userData.getEmail() == null) {
                var body = new Gson().toJson(Map.of("message", "Error: bad request"));
                res.status(400);
                return body;
            }
            var auth = new AuthData(authData.getUsername(), authData.getAuthToken());
            var json = new Gson().toJson(auth);
            res.status(200);
            return json;
        } catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", "Error: already taken"));
            res.status(403);
            return body;
        } catch (Exception e) {
            res.status(500);
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
        }
    }

    private Object login(Request req, Response res) {
        try {

            return null;
        } catch (Exception e) {
            res.status(500);
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
        }
    }

    private Object logout(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            return null;
        } catch (Exception e) {
            res.status(500);
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
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
            var body = new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
            return body;
        }
    }
}
