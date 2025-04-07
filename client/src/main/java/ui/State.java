package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import model.*;
import chess.*;
import client.ServerFacade;
import client.NotificationHandler;
import websocket.messages.ServerMessage;

public class State implements NotificationHandler{
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private final GameplayUI gameplay;
    private boolean loggedIn = false;
    private boolean inGame = false;
    private AuthData authData;
    GameData gameData;

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

    public State(String serverUrl) {
        ServerFacade server = new ServerFacade(serverUrl);
        preLogin = new PreLoginUI(server, this);
        postLogin = new PostLoginUI(server, this, this);
        gameplay = new GameplayUI(server, this, gameData, this);
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
        } else if (isLoggedIn() && isInGame()) {
            System.out.print("\n" + SET_TEXT_COLOR_MAGENTA + "[IN_GAME] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }

    @Override
    public void notify(ServerMessage notification) {

    }
}