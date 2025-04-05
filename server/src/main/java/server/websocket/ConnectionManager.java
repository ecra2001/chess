package server.websocket;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final ConcurrentHashMap<String, Connection> connections = new ConcurrentHashMap<>();

    public void add(String authToken, Session session) {
        connections.put(authToken, new Connection(authToken, session));
    }

    public void remove(String authToken) {
        connections.remove(authToken);
    }

    public void send(String authToken, String message) throws IOException {
        Connection conn = connections.get(authToken);
        if (conn != null && conn.session.isOpen()) {
            conn.send(message);
        }
    }

    public void broadcast(String excludeAuthToken, String message) throws IOException {
        List<String> toRemove = new ArrayList<>();
        for (var entry : connections.entrySet()) {
            if (!entry.getKey().equals(excludeAuthToken)) {
                var conn = entry.getValue();
                if (conn.session.isOpen()) {
                    conn.send(message);
                } else {
                    toRemove.add(entry.getKey());
                }
            }
        }
        toRemove.forEach(connections::remove);
    }
}