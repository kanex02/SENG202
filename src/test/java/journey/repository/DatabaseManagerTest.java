package journey.repository;

import journey.Utils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    void setup() throws IOException {
        // Make sure that it is a new database
        Path testDB = Path.of("src/test/resources/test.db");
        Files.deleteIfExists(testDB);
        databaseManager = DatabaseManager.initialiseWithUrl("src/test/resources/test.db");
        databaseManager.setup();
        Connection conn = databaseManager.connect();
        assertNotNull(conn);
        Utils.closeConn(conn);
    }
}