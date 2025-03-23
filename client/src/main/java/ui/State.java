package ui;

import java.util.Scanner;
import static ui.EscapeSequences.*;

public class State {
    private final PreLoginUI preLogin;

    public State(String serverUrl) {
        preLogin = new PreLoginUI(serverUrl);
    }

    public void run() {
        System.out.println("Welcome to 240 Chess. Type Help to get started.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            printPrompt();
            String line = scanner.nextLine();
            try {
                result = preLogin.eval(line);
                System.out.print(SET_TEXT_COLOR_BLUE + result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
    }

    private void printPrompt() {
        System.out.print("\n" + SET_TEXT_COLOR_WHITE + ">>> " + SET_TEXT_COLOR_GREEN);
    }
}