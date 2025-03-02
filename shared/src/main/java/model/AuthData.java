package model;

public class AuthData {
    private final String authToken;

    public AuthData(String username, String authToken) {
        this.authToken = authToken;
    }

    public String getAuthToken() {
        return authToken;
    }
}

