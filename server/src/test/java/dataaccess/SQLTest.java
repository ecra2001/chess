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
    void authUserNegative() {

    }

    @Test
    void clearUserTest() {

    }
}