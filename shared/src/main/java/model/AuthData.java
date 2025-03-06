package model;

import java.util.Objects;

public class AuthData {
    private String username;
    private String authData;

    AuthData(String username, String authData) {
        this.username = username;
        this.authData = authData;
    }

    public String getUsername(){
        return username;
    }

    public String getAuthData(){
        return authData;
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
        return Objects.equals(username, authData1.username) && Objects.equals(authData, authData1.authData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, authData);
    }

    @Override
    public String toString() {
        return "AuthData{" +
                "username='" + username + '\'' +
                ", authData='" + authData + '\'' +
                '}';
    }
}