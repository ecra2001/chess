package server.websocket;

import dataaccess.DataAccessException;
import service.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import java.lang.Throwable;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        try {
            UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
            var type = userGameCommand.getCommandType();
            switch (type) {
                case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                case MAKE_MOVE -> {

                }
                case LEAVE -> leave(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
                case RESIGN -> {

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
        connections.add(authToken, session);
        var loadGameMessage = new LoadGameMessage(game.getGame());
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        NotificationMessage notificationMessage = new NotificationMessage("User has joined game");
        connections.broadcast(authToken, notificationMessage);
    }

    private void makeMove() throws IOException {

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
            gameData = new GameData(gameData.getGameID(), gameData.getBlackUsername(), null, gameData.getGameName(), gameData.getGame());
        }
        Service.GameService.gameDAO.updateGame(gameData);
        NotificationMessage notificationMessage = new NotificationMessage("User has left game");
        connections.broadcast(authToken, notificationMessage);
    }

    private void resign(String authToken) throws IOException {

    }

    private void sendError(Session session, ErrorMessage error) throws IOException {
        String json = new Gson().toJson(error);
        System.out.printf("Error: %s%n", json);
        session.getRemote().sendString(json);
    }
}