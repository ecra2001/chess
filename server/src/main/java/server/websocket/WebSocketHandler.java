package server.websocket;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.messages.*;

import java.io.IOException;
import java.util.Timer;

@WebSocket
public class WebSocketHandler {
    private final ConnectionManager connections = new ConnectionManager();
}