package journey.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class DatabaseTest {

    //TODO: Write automated SQL tests.
    @Test
    void connection() {
        assertEquals(Database.connect(), 0);
        Database.init();
        assertEquals(Database.disconnect(), 0);
        assertEquals(Database.disconnect(), 1);

    }
}