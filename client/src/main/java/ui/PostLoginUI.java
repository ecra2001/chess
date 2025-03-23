package ui;

import java.util.Arrays;
import model.*;
import exception.ResponseException;
import client.ServerFacade;

public class PostLoginUI {
    private final ServerFacade facade;
    private final String serverUrl;
    private final State state;

    public PostLoginUI(String serverUrl, State state) {
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
                case "create" -> create(params);
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) throws ResponseException {
        return null;
    }

    public String logout() throws ResponseException {
        state.setLoggedIn(false);
        facade.logout(state.getAuthToken());
        return "logged out";
    }

    public String help() {
        return """
                create <NAME>
                list
                join <ID> [BLACK|WHITE]
                logout
                quit
                help
                """;
    }
}