package ui;

import java.util.Arrays;
import java.util.zip.DataFormatException;
import com.google.gson.Gson;
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
                case "list" -> list();
                case "logout" -> logout();
                case "quit" -> "quit";
                default -> help();
            };
        } catch (ResponseException | DataFormatException ex) {
            return ex.getMessage();
        }
    }

    public String create(String... params) throws ResponseException, DataFormatException {
        if (params.length == 1) {
            try {
                var gameName = params[0];
                facade.createGame(gameName, state.getAuthToken());
                return String.format("Created game: %s", gameName);
            } catch (ResponseException e) {
                throw new DataFormatException("Error creating game");
            }
        }
        throw new ResponseException(400, "Expected: create <NAME>");
    }

    public String list() throws ResponseException {
        var games = facade.listGames(state.getAuthToken());
        if (games.isEmpty()) {
            return "No games available.";
        }
        var result = new StringBuilder("Available Games:\n");
        for (var game : games) {
            result.append(String.format("ID: %d, Name: %s, White: %s, Black: %s%n",
                    game.getGameID(), game.getGameName(), game.getWhiteUsername(), game.getBlackUsername()));
        }

        return result.toString();
    }

    public String join(String... params) throws ResponseException {
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