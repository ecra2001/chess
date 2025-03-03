package server;

import service.Service;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import spark.*;

public class Server {

    UserDAO userDAO;
    AuthDAO authDAO;
    GameDAO gameDAO;
   static Service.UserService userService;
   static Service.GameService gameService;

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

    private Object clear(Request req, Response resp) {
        return "{}";
    }

    public Object register(Request req, Response resp) throws DataAccessException {
        return null;
    }

    public Object login(Request req, Response resp) throws DataAccessException {
        return null;
    }

    public Object logout(Request req, Response resp) throws DataAccessException {
        return null;
    }

    public Object listGames(Request req, Response resp) throws DataAccessException {
        return null;
    }

    public Object createGame(Request req, Response resp) throws DataAccessException {
        return null;
    }

    public Object joinGame(Request req, Response resp) throws DataAccessException {
        return null;
    }
}
