package ui;

import java.util.Arrays;
import model.*;
import exception.ResponseException;
import client.ServerFacade;

public class PreLoginUI {
    private final ServerFacade facade;
    private final String serverUrl;
    private final State state;

    public PreLoginUI(String serverUrl, State state) {
        this.state = state;
        facade = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    public String eval(String input) {
        try {
            var tokens = input.toLowerCase().split(" ");
            var cmd = (tokens.length > 0) ? tokens[0] : "help";
            var params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch (cmd) {
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String register(String... params) throws ResponseException {
        if (params.length == 3) {
            var username = params[0];
            var password = params[1];
            var email = params[2];
            UserData userData = new UserData(username, password, email);
            facade.register(userData);
            state.setLoggedIn(true);
            return String.format("Registered and logged in as %s", userData.getUsername());
        }
        throw new ResponseException(400, "Expected: <USERNAME> <PASSWORD> <EMAIL>");
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