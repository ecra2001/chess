package server.websocket;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;
import websocket.commands.*;

import java.io.IOException;
import java.util.Timer;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        JsonObject json = JsonParser.parseString(message).getAsJsonObject();
        String typeStr = json.get("commandType").getAsString();
        UserGameCommand.CommandType type = UserGameCommand.CommandType.valueOf(typeStr);

        switch (type) {
            case CONNECT -> {
                ConnectCommand cmd = new Gson().fromJson(message, ConnectCommand.class);
                handleConnect(cmd, session);
            }
            case MAKE_MOVE -> {
                MakeMoveCommand cmd = new Gson().fromJson(message, MakeMoveCommand.class);
                handleMakeMove(cmd);
            }
            case LEAVE -> {
                LeaveCommand cmd = new Gson().fromJson(message, LeaveCommand.class);
                handleLeave(cmd);
            }
            case RESIGN -> {
                ResignCommand cmd = new Gson().fromJson(message, ResignCommand.class);
                handleResign(cmd);
            }
        }
    }

    private void handleConnect(ConnectCommand cmd, Session session) throws IOException {

    }

    private void handleMakeMove(MakeMoveCommand cmd) throws IOException {

    }

    private void handleLeave(LeaveCommand cmd) throws IOException {

    }

    private void handleResign(ResignCommand cmd) throws IOException {

    }
}