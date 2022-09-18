package journey.data;

import journey.repository.DatabaseManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class DatabaseManagerTest {
    DatabaseManager databaseManager;

    @BeforeEach
    void init() {
        databaseManager = DatabaseManager.getInstance();
    }

    @Test
    void connecting() throws SQLException {
        Connection conn = databaseManager.connect();
        assertNotNull(conn);
        assertNotNull(conn.getMetaData());
        Utils.closeConn(conn);
    }
}