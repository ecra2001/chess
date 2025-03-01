package model;

public class UserData {
    private final String username;

    UserData(String username, String password, String email) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}