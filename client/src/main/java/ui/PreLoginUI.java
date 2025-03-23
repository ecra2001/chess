package ui;

import model.*;
import exception.ResponseException;
import client.ServerFacade;

public class PreLoginUI {
    private final ServerFacade server;
    private final String serverUrl;

    public PreLoginUI(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }
}