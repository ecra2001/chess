package server;

import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import model.AuthData;
import java.util.HashSet;
import service.Service.UserService;
import service.Service.GameService;
import spark.Request;
import spark.Response;

public class Handler {
    public static class UserHandler {
        UserService userService;
        public UserHandler(UserService userService){
            this.userService = userService;
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


    }
    public static class GameHandler {
        GameService gameService;
        public GameHandler(GameService gameService){
            this.gameService = gameService;
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
}