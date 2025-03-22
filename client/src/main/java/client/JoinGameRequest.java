package client;

public class JoinGameRequest {
    private final int gameID;
    private final String playerColor;

    public JoinGameRequest(int gameID, String playerColor) {
        this.gameID = gameID;
        this.playerColor = playerColor;
    }

    public int getGameID() {
        return gameID;
    }

    public String getPlayerColor() {
        return playerColor;
    }
}