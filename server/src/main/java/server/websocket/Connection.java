package server.websocket;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;


class Connection {
    final String authToken;
    final Session session;
    private final int gameID; // Store the gameID

    public Connection(String authToken, Session session, int gameID) {
        this.authToken = authToken;
        this.session = session;
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}