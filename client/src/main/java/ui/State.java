package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import model.*;
import chess.*;
import client.ServerFacade;
import client.NotificationHandler;
import websocket.messages.ServerMessage;
import client.WebSocketFacade;
import websocket.messages.*;

public class State implements NotificationHandler{
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private final GameplayUI gameplay;
    private boolean loggedIn = false;
    private boolean inGame = false;
    private AuthData authData;
    private Integer gameID;
    private WebSocketFacade webSocket;
    private ChessGame game;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
    public boolean isInGame() {
        return inGame;
    }

    public void setInGame(boolean inGame) {
        this.inGame = inGame;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    public String getAuthToken() {
        return authData.getAuthToken();
    }
    public Integer getGameID() {
        return gameID;
    }
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }

    public WebSocketFacade getWebSocket() {
        return webSocket;
    }
    public void setWebSocket(WebSocketFacade webSocket) {
        this.webSocket = webSocket;
    }

    public ChessGame getGame() {
        return game;
    }

    public void setGame(ChessGame game) {
        this.game = game;
    }

    public State(String serverUrl) {
        ServerFacade server = new ServerFacade(serverUrl);
        preLogin = new PreLoginUI(server, this);
        postLogin = new PostLoginUI(server, this, this);
        gameplay = new GameplayUI(server, this, this);
    }

    public void run() {
        System.out.println("Welcome to 240 Chess. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                if (!isLoggedIn()) {
                    result = preLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } else if (isLoggedIn() && !isInGame()) {
                    result = postLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } else if (isLoggedIn() && isInGame()) {
                    result = gameplay.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        if (isLoggedIn() && !isInGame()) {
            System.out.print("\n" + SET_TEXT_COLOR_YELLOW + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        } else if (!isLoggedIn()) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    @Override
    public void notify(ServerMessage notification) {
        if (notification instanceof NotificationMessage) {
            NotificationMessage notif = (NotificationMessage) notification;
            System.out.println(SET_TEXT_COLOR_YELLOW + notif.getMessage());
        } else if (notification instanceof ErrorMessage) {
            ErrorMessage error = (ErrorMessage) notification;
            System.out.println(SET_TEXT_COLOR_RED + error.getErrorMessage());
        }
        System.out.print("\n" + SET_TEXT_COLOR_MAGENTA + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
    }
}