package dataaccess;

import model.AuthData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLAuthDAO implements AuthDAO {
    private final Connection conn;

    public SQLAuthDAO(Connection conn) {
        this.conn = conn;
    }

    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT authToken FROM AuthData WHERE authToken = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new AuthData(null, rs.getString("authToken"));
            } else {
                throw new DataAccessException("Auth token not found.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token.", e);
        }
    }

    @Override
    public void createAuth(AuthData authData) throws DataAccessException {
        String sql = "INSERT INTO AuthData (authToken) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authData.getAuthToken());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting auth token.", e);
        }
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM AuthData WHERE authToken = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token.", e);
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM AuthData";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth data.", e);
        }
    }
}