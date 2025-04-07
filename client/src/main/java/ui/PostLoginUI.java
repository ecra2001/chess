package ui;

import java.util.Arrays;
import java.util.zip.DataFormatException;

import client.WebSocketFacade;
import client.NotificationHandler;
import model.*;
import exception.ResponseException;
import client.ServerFacade;
import chess.*;

public class PostLoginUI {
    ServerFacade facade;
    private final String serverUrl;
    private final NotificationHandler notificationHandler;
    private WebSocketFacade ws;
    private final State state;

    public PostLoginUI(ServerFacade facade, State state, NotificationHandler notificationHandler) {
        this.state = state;
        this.facade = facade;
        serverUrl = facade.getServerUrl();
        this.notificationHandler = notificationHandler;
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
                case "join" -> join(params);
                case "observe" -> observe(params);
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
        int gameNumber = 0;
        for (var game : games) {
            gameNumber += 1;
            result.append(String.format("[%d] - Name: %s, White: %s, Black: %s%n",
                    gameNumber, game.getGameName(), game.getWhiteUsername(), game.getBlackUsername()));
        }

        return result.toString();
    }

    public String join(String... params) throws ResponseException, DataFormatException {
        if (params.length == 2) {
            var gameNumber = Integer.parseInt(params[0]) - 1;
            var color = params[1];
            if (!color.equalsIgnoreCase("WHITE") && !color.equalsIgnoreCase("BLACK")) {
                return "Invalid color: Select [WHITE] or [BLACK]";
            }
            var games = facade.listGames(state.getAuthToken());
            if (games.isEmpty() || gameNumber < 0 || gameNumber >= games.size()) {
                return "Game doesn't exist. Enter 'list' to see full list of games";
            }
            GameData gameSelection = games.get(gameNumber);
            try {
                facade.joinGame(state.getAuthToken(), gameSelection.getGameID(), color);
                ws = new WebSocketFacade(serverUrl, notificationHandler);
                ws.connect(state.getAuthToken(), gameSelection.getGameID());
                state.setGameID(gameSelection.getGameID());
                state.setInGame(true);
                return "joined game";
            } catch (ResponseException e) {
                throw new DataFormatException("Failed to join: Either color taken or game no longer exists.");
            }
        }
        throw new DataFormatException("Join error");
    }

    public String observe(String... params) throws ResponseException {
        if (params.length == 1) {
            var gameNum = Integer.parseInt(params[0]) - 1;
            var allGames = facade.listGames(state.getAuthToken());
            if (allGames.isEmpty() || gameNum < 0 || gameNum >= allGames.size()) {
                return "Game doesn't exist. Enter 'list' to see full list of games";
            }
            GameData gameSelection = allGames.get(gameNum);
            ws = new WebSocketFacade(serverUrl, notificationHandler);
            ws.connect(state.getAuthToken(), gameSelection.getGameID());
            state.setGameID(gameSelection.getGameID());
            state.setInGame(true);
            return "observing game";
        }
        throw new ResponseException(400, "Expected: observe <ID>");
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
                observe <ID>
                logout
                quit
                help
                """;
    }
}