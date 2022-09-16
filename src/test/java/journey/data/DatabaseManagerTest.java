package journey.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
class DatabaseManagerTest {
    DatabaseManager databaseManager;

    @BeforeEach
    void init() {
        databaseManager = DatabaseManager.getInstance();
    }

    @Test
    void connecting() {
        databaseManager.connect();
    }

    @Test
    void updateUser() {
        databaseManager.updateUser("Krane");
        assertEquals(databaseManager.getCurrentUser().getName(), "Krane");
    }
}