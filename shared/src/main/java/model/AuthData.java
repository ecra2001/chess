package model;

import java.util.Objects;

public class AuthData {
    private String username;
    private String authToken;

    public AuthData(String username, String authData) {
        this.username = username;
        this.authToken = authData;
    }

    public String getUsername(){
        return username;
    }

    public String getAuthToken(){
        return authToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AuthData authData1 = (AuthData) o;
        return Objects.equals(username, authData1.username) && Objects.equals(authToken, authData1.authToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authToken);
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }
}