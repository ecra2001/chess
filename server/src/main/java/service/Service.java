package service;

import chess.ChessBoard;
import chess.ChessGame;
import dataaccess.GameDAO;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.DataAccessException;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import java.util.HashSet;

public class Service {
    public static class UserService {
        UserDAO userDAO;
        AuthDAO authDAO;
        public UserService(UserDAO userDAO, AuthDAO authDAO) {
            this.userDAO = userDAO;
            this.authDAO = authDAO;
        }
    }

    public static class GameService {

    }
}