package server;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import dataaccess.*;
import service.Service;
import spark.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    static Service.UserService userService;
    static Service.GameService gameService;
    private final Gson gson = new Gson();

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
        //Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private Object clear(Request req, Response resp) throws DataAccessException {
        userService.clear();
        Service.GameService.clear(gameDAO);
        resp.status(200);
        return "{}";
    }

    public Object register(Request req, Response resp) throws DataAccessException {
        try {
            UserData userData = gson.fromJson(req.body(), UserData.class);
            if (userData.getUsername() == null || userData.getPassword() == null || userData.getEmail() == null) {
                resp.status(400);
                return gson.toJson(Map.of("message", "Error: bad request"));
            }

            AuthData authData = userService.createUser(userData);
            resp.status(200);
            return gson.toJson(Map.of("username", authData.getUsername(), "authToken", authData.getAuthToken()));
        } catch (DataAccessException e) {
            resp.status(403);
            return gson.toJson(Map.of("message", "Error: already taken"));
        } catch (Exception e) {
            resp.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object login(Request req, Response resp) throws DataAccessException {
        try {
            UserData userData = gson.fromJson(req.body(), UserData.class);
            AuthData authData = userService.loginUser(userData);
            resp.status(200);
            return gson.toJson(Map.of("username", authData.getUsername(), "authToken", authData.getAuthToken()));
        } catch (DataAccessException e) {
            resp.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            resp.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object logout(Request req, Response resp) throws DataAccessException {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                resp.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }
            userService.logoutUser(authToken);
            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            resp.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            resp.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object listGames(Request req, Response resp) throws DataAccessException {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null) {
                resp.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }

            HashSet<GameData> games = gameService.listGames(authToken);
            resp.status(200);
            return gson.toJson(Map.of("games", games));
        } catch (DataAccessException e) {
            resp.status(401);
            return gson.toJson(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            resp.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object createGame(Request req, Response resp) throws DataAccessException {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || !authDAO.authTokenExists(authToken)) {
                resp.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }

            Map<String, String> body = gson.fromJson(req.body(), Map.class);
            if (body == null || !body.containsKey("gameName")) {
                resp.status(400);
                return gson.toJson(Map.of("message", "Error: bad request"));
            }

            int gameID = gameService.createGame(authToken, body.get("gameName"));
            resp.status(200);
            return gson.toJson(Map.of("gameID", gameID));
        } catch (DataAccessException e) {
            resp.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public Object joinGame(Request req, Response resp) throws DataAccessException {
        try {
            String authToken = req.headers("Authorization");
            if (authToken == null || !authDAO.authTokenExists(authToken)) {
                resp.status(401);
                return gson.toJson(Map.of("message", "Error: unauthorized"));
            }

            Map<String, Object> body = gson.fromJson(req.body(), Map.class);
            if (body == null || !body.containsKey("playerColor") || !body.containsKey("gameID")) {
                resp.status(400);
                return gson.toJson(Map.of("message", "Error: bad request"));
            }

            int gameID = ((Number) body.get("gameID")).intValue();
            String playerColor = (String) body.get("playerColor");

            boolean success = gameService.joinGame(authToken, gameID, playerColor);
            if (!success) {
                resp.status(403);
                return gson.toJson(Map.of("message", "Error: already taken"));
            }

            resp.status(200);
            return "{}";
        } catch (DataAccessException e) {
            resp.status(400);
            return gson.toJson(Map.of("message", "Error: bad request"));
        } catch (Exception e) {
            resp.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
