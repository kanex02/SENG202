package journey.repository;

import journey.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {
    DatabaseManager databaseManager;


    @Test
    void connecting() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        Connection conn = databaseManager.connect();
        assertNotNull(conn);
        assertNotNull(conn.getMetaData());
        Utils.closeConn(conn);
    }

    @Test
    void setup() throws SQLException, IOException {
        databaseManager = new DatabaseManager("src/test/resources/test.db");
        databaseManager.setup();
        assertNotNull(databaseManager.connect());
    }
}