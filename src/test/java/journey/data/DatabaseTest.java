package journey.data;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
class DatabaseTest {

    //TODO: Write automated SQL tests.
    @Test
    void connecting() {
        assertEquals(Database.connect(), 0);
        assertEquals(Database.disconnect(), 0);
        assertEquals(Database.disconnect(), 1);
    }

    @Test
    void updateUser() {
        Database.updateUser("Krane");
        assertEquals(Database.getCurrentUser().getName(), "Krane");
    }

    @Test
    void convertArrToStr() {
        String[] strArr = {"a","b","c","d"};
        String stri = Database.convertArrayToString(strArr, ":");
        assertEquals(stri, "a:b:c:d:");
    }
}