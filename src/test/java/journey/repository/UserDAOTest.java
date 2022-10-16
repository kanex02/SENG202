package journey.repository;

import journey.data.Note;
import journey.data.Station;
import journey.data.User;
import journey.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;
    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        userDAO = new UserDAO();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        s.execute("DELETE FROM Notes WHERE station_ID = -1");
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
    }

    @AfterEach
    void tearDown() throws SQLException {
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        s.execute("DELETE FROM Notes WHERE station_ID = -1");
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
        Utils.closeConn(conn);
    }

    @Test
    void testerInDatabase() {

        User user = userDAO.setCurrentUser("Tester");
        boolean userInDatabase = userDAO.nameInDB("Tester");

        assertTrue(userInDatabase);

    }

    @Test
    void nameNotInDatabase() {
        boolean userInDatabase = userDAO.nameInDB("notInDB");
        assertFalse(userInDatabase);
    }

    @Test
    void updateUserNameTest() {
        User user = userDAO.setCurrentUser("notTester");
        int userID = user.getId();

        userDAO.updateUserName(userID, "Tester");

        User tester = userDAO.setCurrentUser("Tester");

        assertEquals("Tester", tester.getName());
    }
}