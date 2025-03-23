package ui;

import java.util.Arrays;
import java.util.zip.DataFormatException;

import model.*;
import exception.ResponseException;
import client.ServerFacade;

public class PreLoginUI {
    private final ServerFacade facade;
    private final State state;

    public PreLoginUI(String serverUrl, State state) {
        this.state = state;
        facade = new ServerFacade(serverUrl);
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "login" -> login(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | DataFormatException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException, DataFormatException {
        if (params.length == 3) {
            try {
                var username = params[0];
                var password = params[1];
                var email = params[2];
                UserData userData = new UserData(username, password, email);
                AuthData authData = facade.register(userData);
                state.setAuthData(authData);
                state.setLoggedIn(true);
                return String.format("Registered and logged in as %s", userData.getUsername());
            } catch (ResponseException e) {
                throw new DataFormatException("Username already taken. Try another.");
            }
        }
        throw new ResponseException(400, "Expected: register <USERNAME> <PASSWORD> <EMAIL>");
    }

    public String login(String... params) throws ResponseException, DataFormatException {
        if (params.length == 2) {
            try {
                var username = params[0];
                var password = params[1];
                AuthData authData = facade.login(username, password);
                state.setAuthData(authData);
                state.setLoggedIn(true);
                return String.format("Logged in as %s", authData.getUsername());
            } catch (ResponseException e) {
                throw new DataFormatException("Incorrect Username or password. Try again.");
            }
        }
        throw new ResponseException(400, "Expected: login <USERNAME> <PASSWORD>");
    }

    public String help() {
        return """
                register <USERNAME> <PASSWORD> <EMAIL>
                login <USERNAME> <PASSWORD>
                quit
                help
                """;
    }
}