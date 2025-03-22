import chess.*;
import dataaccess.*;
import server.*;

public class Main {
    public static void main(String[] args) throws DataAccessException {
        try { DatabaseManager.createDatabase(); } catch (DataAccessException e) {
            throw new DataAccessException("Could not create database");
        }
        System.out.println("♕ 240 Chess Server");
        int port = 8080;
        Server server = new Server();
        server.run(port);

    }
}