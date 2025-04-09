package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.DataAccessException;
import service.Service;
import com.google.gson.Gson;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.Objects;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        try {
            Gson gson = new Gson();
            UserGameCommand baseCommand = gson.fromJson(message, UserGameCommand.class);

            switch (baseCommand.getCommandType()) {
                case CONNECT -> {
                    ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                    connect(connectCommand.getAuthToken(), connectCommand.getGameID(), session);
                }
                case MAKE_MOVE -> {
                    MakeMoveCommand makeMoveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    makeMove(makeMoveCommand.getAuthToken(), makeMoveCommand.getGameID(), makeMoveCommand.getMove(), session);
                }
                case LEAVE -> {
                    LeaveCommand leaveCommand = gson.fromJson(message, LeaveCommand.class);
                    leave(leaveCommand.getAuthToken(), leaveCommand.getGameID(), session);
                }
                case RESIGN -> {
                    ResignCommand resignCommand = gson.fromJson(message, ResignCommand.class);
                    resign(resignCommand.getAuthToken(), resignCommand.getGameID(), session);
                }
            }
        } catch (Exception e) {
            System.err.println("Error in onMessage: " + e.getMessage());
            e.printStackTrace();
            try {
                sendError(session, new ErrorMessage("Error: Exception occurred in message handling"));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException, DataAccessException {
        AuthData authData = Service.UserService.authDAO.getAuth(authToken);
        if (authData == null) {
            sendError(session, new ErrorMessage("Error: Not authorized"));
            return;
        }
        GameData game = Service.GameService.gameDAO.getGame(gameID);
        if (game == null) {
            sendError(session, new ErrorMessage("Error: Bad gameID"));
        }
        connections.add(authToken, session, gameID);
        assert game != null;
        var loadGameMessage = new LoadGameMessage(game.getGame());
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        String joinedAs;
        if (Objects.equals(authData.getUsername(), game.getWhiteUsername())) {
            joinedAs = "white";
        } else if (Objects.equals(authData.getUsername(), game.getBlackUsername())) {
            joinedAs = "black";
        } else {
            joinedAs = "observer";
        }

        NotificationMessage notificationMessage = new NotificationMessage("%s has joined game as %s".formatted(authData.getUsername(), joinedAs));
        connections.broadcast(gameID, authToken, notificationMessage);
    }

    private void makeMove(String authToken, int gameID, ChessMove move, Session session) throws IOException, DataAccessException {
        try {
            AuthData authData = Service.UserService.authDAO.getAuth(authToken);
            GameData gameData = Service.GameService.gameDAO.getGame(gameID);
            ChessGame.TeamColor color;
            ChessGame.TeamColor opponentColor;
            if (authData.getUsername().equals(gameData.getWhiteUsername())) {
                color = ChessGame.TeamColor.WHITE;
                opponentColor = ChessGame.TeamColor.BLACK;
            } else if (authData.getUsername().equals(gameData.getBlackUsername())) {
                color = ChessGame.TeamColor.BLACK;
                opponentColor = ChessGame.TeamColor.WHITE;
            } else {
                sendError(session, new ErrorMessage("Error: Observer cannot make move"));
                return;
            }
            if (gameData.getGame().getGameOver()) {
                sendError(session, new ErrorMessage("Error: Game already over"));
                return;
            }
            if (gameData.getGame().getTeamTurn() != color) {
                sendError(session, new ErrorMessage("Error: Not your turn"));
                return;
            } else {
                gameData.getGame().makeMove(move);
                var loadGameMessage = new LoadGameMessage(gameData.getGame());
                connections.broadcast(gameID, null, loadGameMessage);
                NotificationMessage notificationMessage = new NotificationMessage("%s moved.".formatted(authData.getUsername()));
                connections.broadcast(gameID, authToken, notificationMessage);
                if (gameData.getGame().isInCheckmate(opponentColor)) {
                    gameData.getGame().setGameOver(true);
                    notificationMessage = new NotificationMessage("Checkmate! %s wins.".formatted(authData.getUsername()));
                    connections.broadcast(gameID, null, notificationMessage);
                } else if (gameData.getGame().isInStalemate(opponentColor)) {
                    gameData.getGame().setGameOver(true);
                    notificationMessage = new NotificationMessage("Stalemate! Tied game.");
                    connections.broadcast(gameID, null, notificationMessage);
                } else if (gameData.getGame().isInCheck(opponentColor)) {
                    notificationMessage = new NotificationMessage("%s moved. %s in check.".formatted(authData.getUsername(),
                            opponentColor.toString()));
                    connections.broadcast(gameID, null, notificationMessage);
                }
                Service.GameService.gameDAO.updateGame(gameData);
            }
        } catch (InvalidMoveException e) {
            sendError(session, new ErrorMessage("Error: Invalid move"));
        }
    }

    private void leave(String authToken, int gameID, Session session) throws IOException, DataAccessException {
        AuthData authData = Service.UserService.authDAO.getAuth(authToken);
        if (authData == null) {
            sendError(session, new ErrorMessage("Error: Not authorized"));
            return;
        }
        GameData gameData = Service.GameService.gameDAO.getGame(gameID);
        connections.remove(authToken);
        if (authData.getUsername().equals(gameData.getWhiteUsername())) {
            gameData = new GameData(gameData.getGameID(), null, gameData.getBlackUsername(), gameData.getGameName(), gameData.getGame());
        } else if (authData.getUsername().equals(gameData.getBlackUsername())) {
            gameData = new GameData(gameData.getGameID(), gameData.getWhiteUsername(), null, gameData.getGameName(), gameData.getGame());
        }
        Service.GameService.gameDAO.updateGame(gameData);
        NotificationMessage notificationMessage = new NotificationMessage("%s has left game".formatted(authData.getUsername()));
        connections.broadcast(gameID, authToken, notificationMessage);
    }

    private void resign(String authToken, int gameID, Session session) throws IOException, DataAccessException {
        AuthData authData = Service.UserService.authDAO.getAuth(authToken);
        GameData gameData = Service.GameService.gameDAO.getGame(gameID);
        if (authData == null) {
            sendError(session, new ErrorMessage("Error: Not authorized"));
            return;
        }
        if (gameData.getGame().getGameOver()) {
            sendError(session, new ErrorMessage("Error: Game already over"));
            return;
        }
        if (!authData.getUsername().equals(gameData.getWhiteUsername()) &&
                !authData.getUsername().equals(gameData.getBlackUsername())) {
            sendError(session, new ErrorMessage("Error: Observer cannot resign"));
            return;
        }
        gameData.getGame().setGameOver(true);
        Service.GameService.gameDAO.updateGame(gameData);
        NotificationMessage notificationMessage = new NotificationMessage("%s has forfeited".formatted(authData.getUsername()));
        connections.broadcast(gameID, null, notificationMessage);
    }

    private void sendError(Session session, ErrorMessage error) throws IOException {
        String json = new Gson().toJson(error);
        System.out.printf("Error: %s%n", json);
        session.getRemote().sendString(json);
    }
}