package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class SQLTest {
    private SQLUserDAO sqlUserDAO;
    private SQLAuthDAO sqlAuthDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        sqlUserDAO = new SQLUserDAO();
        sqlAuthDAO = new SQLAuthDAO();
        sqlUserDAO.clear();
        sqlAuthDAO.clear();
        UserData user = new UserData("username", "password", "email");
        sqlUserDAO.createUser(user);
        AuthData auth = new AuthData("username", "authToken");
        sqlAuthDAO.createAuth(auth);
    }

    @Test
    void getUserPositive() throws DataAccessException {
        Assertions.assertNotNull(sqlUserDAO.getUser("username"));
        UserData userData = sqlUserDAO.getUser("username");
        Assertions.assertEquals("username", userData.getUsername());
        Assertions.assertEquals("email", userData.getEmail());
        Assertions.assertTrue(sqlUserDAO.authUser("username", "password"));
    }

    @Test
    void getUserNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser("wrongUsername"));
    }

    @Test
    void createUserPositive() throws DataAccessException {
        UserData userData = sqlUserDAO.getUser("username");
        Assertions.assertEquals("username", userData.getUsername());
        Assertions.assertEquals("email", userData.getEmail());
        Assertions.assertTrue(sqlUserDAO.authUser("username", "password"));
    }

    @Test
    void createUserNegative() {
        UserData sameUser = new UserData("username", "password", "email");
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.createUser(sameUser));
    }

    @Test
    void authUserPositive() throws DataAccessException {
        Assertions.assertTrue(sqlUserDAO.authUser("username", "password"));
    }

    @Test
    void authUserNegative() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.authUser("badUsername", "password"));
        Assertions.assertFalse(sqlUserDAO.authUser("username", "badPassword"));
    }

    @Test
    void clearUserTest() throws DataAccessException {
        sqlUserDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> sqlUserDAO.getUser("username"));
    }

    @Test
    void createAuthPositive() throws DataAccessException {
        AuthData authData = sqlAuthDAO.getAuth("authToken");
        Assertions.assertEquals("username", authData.getUsername());
        Assertions.assertEquals("authToken", authData.getAuthToken());
    }

    @Test
    void createAuthNegative() {
        AuthData sameAuth = new AuthData("username", "authToken");
        Assertions.assertThrows(RuntimeException.class, () -> sqlAuthDAO.createAuth(sameAuth));
    }

    @Test
    void getAuthPositive() throws DataAccessException {
        Assertions.assertNotNull(sqlAuthDAO.getAuth("authToken"));
        AuthData authData = sqlAuthDAO.getAuth("authToken");
        Assertions.assertEquals("username", authData.getUsername());
        Assertions.assertEquals("authToken", authData.getAuthToken());
    }

    @Test
    void getAuthNegative() {
        Assertions.assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth("wrongAuthToken"));
    }

    @Test
    void removeAuthPositive() {
        sqlAuthDAO.removeAuth("authToken");
        Assertions.assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth("authToken"));
    }

    @Test
    void removeAuthNegative() {
        Assertions.assertDoesNotThrow(() -> sqlAuthDAO.removeAuth("badAuthToken"));
    }

    @Test
    void clearAuthTest() throws DataAccessException {
        sqlAuthDAO.clear();
        Assertions.assertThrows(DataAccessException.class, () -> sqlAuthDAO.getAuth("authToken"));
    }

    @Test
    void getGameListPositive() {

    }

    @Test
    void getGameListNegative() {

    }

    @Test
    void addGamePositive() {

    }

    @Test
    void addGameNegative() {

    }

    @Test
    void getGamePositive() {

    }

    @Test
    void getGameNegative() {

    }

    @Test
    void gameExistsPositive() {

    }

    @Test
    void gameExistsNegative() {

    }

    @Test
    void updateGamePositive() {

    }

    @Test
    void updateGameNegative() {

    }

    @Test
    void clearGameTest() {

    }
}