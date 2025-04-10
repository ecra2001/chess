package client;

import com.google.gson.Gson;
import exception.ResponseException;
import model.*;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

public class ServerFacade {
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public AuthData register(UserData userData) throws ResponseException {
        var path = "/user";
        return this.makeRequest("POST", path, userData, AuthData.class, null);
    }

    public AuthData login(String username, String password) throws ResponseException {
        var path = "/session";
        var body = new LoginRequest(username, password);
        return this.makeRequest("POST", path, body, AuthData.class, null);
    }

    public void logout(String authToken) throws ResponseException {
        var path = "/session";
        this.makeRequest("DELETE", path, null, null, authToken);
    }

    public List<GameData> listGames(String authToken) throws ResponseException {
        var path = "/game";
        record ListGameResponse(List<GameData> games) {
        }
        var response = this.makeRequest("GET", path, null, ListGameResponse.class, authToken);
        return response.games();
    }

    public int createGame(String gameName, String authToken) throws ResponseException {
        var path = "/game";
        var body = new CreateGameRequest(gameName);
        GameData response = this.makeRequest("POST", path, body, GameData.class, authToken);
        return response.getGameID();
    }

    public boolean joinGame(String authToken, int gameID, String playerColor) throws ResponseException {
        var path = "/game";
        var body = new JoinGameRequest(gameID, playerColor);
        JoinGameResponse response = this.makeRequest("PUT", path, body, JoinGameResponse.class, authToken);

        return response.isSuccess();
    }

    private <T> T makeRequest(String method, String path, Object request, Class<T> responseClass, String authToken) throws ResponseException {
        try {
            URL url = (new URI(serverUrl + path)).toURL();
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod(method);
            http.setDoOutput(true);
            if (authToken != null) {
                http.setRequestProperty("Authorization", authToken);
            }

            writeBody(request, http);
            http.connect();
            throwIfNotSuccessful(http);
            return readBody(http, responseClass);
        } catch (ResponseException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseException(500, ex.getMessage());
        }
    }


    private static void writeBody(Object request, HttpURLConnection http) throws IOException {
        if (request != null) {
            http.addRequestProperty("Content-Type", "application/json");
            String reqData = new Gson().toJson(request);
            try (OutputStream reqBody = http.getOutputStream()) {
                reqBody.write(reqData.getBytes());
            }
        }
    }

    private void throwIfNotSuccessful(HttpURLConnection http) throws IOException, ResponseException {
        var status = http.getResponseCode();
        if (!isSuccessful(status)) {
            try (InputStream respErr = http.getErrorStream()) {
                if (respErr != null) {
                    throw ResponseException.fromJson(respErr);
                }
            }

            throw new ResponseException(status, "other failure: " + status);
        }
    }

    private static <T> T readBody(HttpURLConnection http, Class<T> responseClass) throws IOException {
        T response = null;
        if (http.getContentLength() < 0) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader reader = new InputStreamReader(respBody);
                if (responseClass != null) {
                    response = new Gson().fromJson(reader, responseClass);
                }
            }
        }
        return response;
    }


    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }
}