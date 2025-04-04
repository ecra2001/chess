package client;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import websocket.messages.*;
import exception.ResponseException;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;

    public WebSocketFacade(String url, NotificationHandler notificationHandler) throws ResponseException {
        try {
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();
                    String messageType = jsonObject.get("serverMessageType").getAsString();

                    switch (messageType) {
                        case "NOTIFICATION" -> {
                            NotificationMessage notif = new Gson().fromJson(message, NotificationMessage.class);
                            notificationHandler.notify(notif);
                        }
                        case "ERROR" -> {
                            ErrorMessage error = new Gson().fromJson(message, ErrorMessage.class);
                            notificationHandler.notify(error);
                        }
                        case "LOAD_GAME" -> {
                            LoadGameMessage loadGame = new Gson().fromJson(message, LoadGameMessage.class);
                            notificationHandler.notify(loadGame);
                        }
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}
}