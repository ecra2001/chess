package server;

import com.google.gson.Gson;
import spark.*;
import dataaccess.*;
import model.*;
import service.Service;
import java.util.Map;
import java.util.HashSet;
import server.websocket.WebSocketHandler;


public class Server {
    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
    Service.UserService userService;
    Service.GameService gameService;
    private final WebSocketHandler webSocketHandler;
    public Server() {
        userDAO = new SQLUserDAO();
        authDAO = new SQLAuthDAO();
        gameDAO = new SQLGameDAO();
        userService = new Service.UserService(userDAO, authDAO);
        gameService = new Service.GameService(gameDAO, authDAO);
        webSocketHandler = new WebSocketHandler();
    }
    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        Spark.webSocket("/ws", webSocketHandler);

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
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    private Object login(Request req, Response res) {
        try {
            UserData userData = new Gson().fromJson(req.body(), UserData.class);
            AuthData authData = userService.login(userData.getUsername(), userData.getPassword());
            var auth = new AuthData(authData.getUsername(), authData.getAuthToken());
            var json = new Gson().toJson(auth);
            res.status(200);
            return json;
        } catch (DataAccessException e){
            var body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            res.status(401);
            return body;
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    private Object logout(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            userService.logout(authToken);
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            res.status(401);
            return body;
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    private Object listGames(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            HashSet<GameData> gameList = gameService.listGames(authToken);
            var json = new Gson().toJson(Map.of("games", gameList));
            res.status(200);
            return json;
        } catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            res.status(401);
            return body;
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    private Object createGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            GameData gameData = new Gson().fromJson(req.body(), GameData.class);
            if (gameData == null) {
                var body = new Gson().toJson(Map.of("message", "Error: bad request"));
                res.status(400);
                return body;
            }
            int gameID = gameService.createGame(gameData.getGameName(), authToken);
            var json = new Gson().toJson(Map.of("gameID", gameID));
            res.status(200);
            return json;
        } catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            res.status(401);
            return body;
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    private Object joinGame(Request req, Response res) {
        try {
            String authToken = req.headers("authorization");
            Map<String, Double> body = new Gson().fromJson(req.body(), Map.class);
            if (body == null || !body.containsKey("playerColor") || !body.containsKey("gameID")) {
                var json = new Gson().toJson(Map.of("message", "Error: bad request"));
                res.status(400);
                return json;
            }
            double id = body.get("gameID");
            int gameID = (int) id;
            String playerColor = String.valueOf(body.get("playerColor"));
            if (!playerColor.equalsIgnoreCase("white") && !playerColor.equalsIgnoreCase("black")) {
                var json = new Gson().toJson(Map.of("message", "Error: bad request"));
                res.status(400);
                return json;
            }
            boolean joinAttempt = gameService.joinGame(authToken, gameID, playerColor);
            if (!joinAttempt) {
                var json = new Gson().toJson(Map.of("message", "Error: already taken"));
                res.status(403);
                return json;
            }
            res.status(200);
            return "{}";
        } catch (DataAccessException e) {
            var body = new Gson().toJson(Map.of("message", "Error: unauthorized"));
            res.status(401);
            return body;
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    private Object clear(Request req, Response res) {
        try {
            clear();
            res.status(200);
            return "{}";
        } catch (Exception e) {
            res.status(500);
            return new Gson().toJson(Map.of("message", String.format("Error: %s", e.getMessage())));
        }
    }

    public void clear() throws DataAccessException {
        gameService.clear();
        userService.clear();
    }
}
