import chess.*;
import ui.*;

public class Main {
    public static void main(String[] args) {
        var serverUrl = "http://localhost:8080";
        System.out.println("♕ 240 Chess Client");
        if (args.length == 1) {
            serverUrl = args[0];
        }
        new State(serverUrl).run();
    }
}