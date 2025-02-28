package model;
import chess.ChessGame;

record AuthData(String username, String authToken) {
    public String getAuthToken() {
        return authToken;
    }
}

record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {}

record UserData(String username, String password, String email) {
    public String getUsername() {
        return username;
    }
}