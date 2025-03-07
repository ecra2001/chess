package server;

import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/name", this::clear);
        Spark.post("/name", this::register);
        Spark.post("/name", this::login);
        Spark.delete("/name", this::logout);
        Spark.get("/name", this::listGames);
        Spark.post("/name", this::createGame);
        Spark.put("/name", this::joinGame);

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
        return null;
    }

    private Object login(Request req, Response res) {
        return null;
    }

    private Object logout(Request req, Response res) {
        return null;
    }

    private Object listGames(Request req, Response res) {
        return null;
    }

    private Object createGame(Request req, Response res) {
        return null;
    }

    private Object joinGame(Request req, Response res) {
        return null;
    }

    private Object clear(Request req, Response res) {
        return null;
    }
}
