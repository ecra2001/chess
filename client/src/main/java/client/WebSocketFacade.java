package client;

import chess.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ui.GameplayUI;
import ui.State;
import websocket.messages.*;
import websocket.commands.*;
import exception.ResponseException;
import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static ui.EscapeSequences.SET_TEXT_COLOR_GREEN;
import static ui.EscapeSequences.SET_TEXT_COLOR_MAGENTA;
import static ui.GameplayUI.printBoard;

public class WebSocketFacade extends Endpoint {
    Session session;
    NotificationHandler notificationHandler;
    private String playerColor;
    public String getPlayerColor() {
        return playerColor;
    }
    private final State state;

    public WebSocketFacade(String url, NotificationHandler notificationHandler, State state) throws ResponseException {
        try {
            this.state = state;
            url = url.replace("http", "ws");
            URI socketURI = new URI(url + "/ws");
            this.notificationHandler = notificationHandler;

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String message) {
                    try {
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
                                ChessGame game = loadGame.getGame();
                                state.setGame(game);
                                printBoard(playerColor, game, null);
                                notificationHandler.notify(loadGame);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Error in onMessage: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
        } catch (DeploymentException | IOException | URISyntaxException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void connect(String authToken, int gameID, String playerColor) throws ResponseException {
        try {
            this.playerColor = playerColor;
            var command = new ConnectCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void leave(String authToken, int gameID) throws ResponseException {
        try {
            var command = new LeaveCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try {
            var command = new MakeMoveCommand(authToken, gameID, move);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }

    public void resign(String authToken, int gameID) throws ResponseException {
        try {
            var command = new ResignCommand(authToken, gameID);
            this.session.getBasicRemote().sendText(new Gson().toJson(command));
        } catch (IOException ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }
}