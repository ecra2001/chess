package dataaccess;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class SQLTest {
    private SQLUserDAO sqlUserDAO;
    @BeforeEach
    public void setup() throws DataAccessException {
        sqlUserDAO = new SQLUserDAO();
        sqlUserDAO.clear();
        UserData user = new UserData("username", "password", "email");
        sqlUserDAO.createUser(user);
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
    void createAuthPositive() {

    }

    @Test
    void createAuthNegative() {

    }

    @Test
    void getAuthPositive() {

    }

    @Test
    void getAuthNegative() {

    }

    @Test
    void removeAuthPositive() {

    }

    @Test
    void removeAuthNegative() {

    }

    @Test
    void clearAuthTest() {

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