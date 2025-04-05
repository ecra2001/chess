package server.websocket;

import dataaccess.DataAccessException;
import service.Service;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException, DataAccessException {
        UserGameCommand userGameCommand = new Gson().fromJson(message, UserGameCommand.class);
        var type = userGameCommand.getCommandType();
        switch (type) {
            case CONNECT -> connect(userGameCommand.getAuthToken(), userGameCommand.getGameID(), session);
            case MAKE_MOVE -> {

            }
            case LEAVE -> {

            }
            case RESIGN -> {

            }
        }
    }

    private void connect(String authToken, int gameID, Session session) throws IOException, DataAccessException {
        connections.add(authToken, session);
        GameData game = Service.GameService.getGameData(authToken, gameID);
        var loadGameMessage = new LoadGameMessage(game.getGame());
        session.getRemote().sendString(new Gson().toJson(loadGameMessage));

        NotificationMessage notificationMessage = new NotificationMessage("Game loaded successfully");
        connections.broadcast(authToken, notificationMessage);
    }

    private void makeMove() throws IOException {

    }

    private void leave() throws IOException {

    }

    private void resign() throws IOException {

    }
}