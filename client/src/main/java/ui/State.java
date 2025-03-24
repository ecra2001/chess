package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;
import model.*;

public class State {
    private final PreLoginUI preLogin;
    private final PostLoginUI postLogin;
    private boolean loggedIn = false;
    private AuthData authData;

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setAuthData(AuthData authData) {
        this.authData = authData;
    }

    public String getAuthToken() {
        return authData.getAuthToken();
    }

    public State(String serverUrl) {
        preLogin = new PreLoginUI(serverUrl, this);
        postLogin = new PostLoginUI(serverUrl, this);
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
                } else if (isLoggedIn()) {
                    result = postLogin.eval(line);
                    System.out.print(SET_TEXT_COLOR_BLUE + result);
                } // need to implement switch to GameplayUI
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        if (isLoggedIn()) {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[LOGGED_IN] >>> " + SET_TEXT_COLOR_GREEN);
        } else {
            System.out.print("\n" + SET_TEXT_COLOR_WHITE + "[LOGGED_OUT] >>> " + SET_TEXT_COLOR_GREEN);
        }
    }
}