package dataaccess;

import chess.ChessGame;
import java.util.HashSet;
import model.GameData;
import java.sql.*;

public class SQLGameDAO implements GameDAO {
    private final Connection conn;
    //need SQL table setup
    public SQLGameDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public HashSet<GameData> listGames() throws DataAccessException{
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName FROM Games";
        HashSet<GameData> games = new HashSet<>();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                int gameID = rs.getInt("gameID");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");

                ChessGame game = retrieveGameData(gameID);
                games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, game));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error listing games.", e);
        }
        return games;
    }

    @Override
    public void addGame(GameData game) throws DataAccessException {
        String sql = "INSERT INTO Games (gameID, whiteUsername, blackUsername, gameName, gameData) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, game.getGameID());
            stmt.setString(2, game.getWhiteUsername());
            stmt.setString(3, game.getBlackUsername());
            stmt.setString(4, game.getGameName());
            stmt.setObject(5, game.getGame());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error adding game.", e);
        }
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT gameID, whiteUsername, blackUsername, gameName, gameData FROM Games WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                ChessGame game = (ChessGame) rs.getObject("gameData"); // Assuming ChessGame is serialized into a BLOB
                return new GameData(gameID, whiteUsername, blackUsername, gameName, game);
            } else {
                throw new DataAccessException("Game with ID " + gameID + " not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error getting game.", e);
        }
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {
        String sql = "UPDATE Games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameData = ? WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, game.getWhiteUsername());
            stmt.setString(2, game.getBlackUsername());
            stmt.setString(3, game.getGameName());
            stmt.setObject(4, game.getGame());
            stmt.setInt(5, game.getGameID());
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("Game with ID " + game.getGameID() + " not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game.", e);
        }
    }

    @Override
    public boolean gameExists(int gameID) throws DataAccessException{
        String sql = "SELECT COUNT(*) FROM Games WHERE gameID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new DataAccessException("Error checking if game exists.", e);
        }
    }

    @Override
    public void clear() throws DataAccessException{
        String sql = "DELETE FROM Games";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing game data.", e);
        }
    }

    private ChessGame retrieveGameData(int gameID) {
        // Implement logic to retrieve ChessGame by gameID
        return new ChessGame();
    }

    //need to make serialize and deserialize
}