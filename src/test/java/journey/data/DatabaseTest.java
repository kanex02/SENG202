package journey.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
class DatabaseTest {

    //TODO: Write automated SQL tests.
    @Test
    void connection() {
        assertEquals(Database.connect(), 0);
        Database.setup();
        assertEquals(Database.disconnect(), 0);
        assertEquals(Database.disconnect(), 1);

    }

//    @Test
//    void updateUser() {
//        Database.updateUser(1);
//        assertEquals(Database.getCurrentUser().getName(), "Krane");
//    }

    @Test
    void convertArrToStr() {
        String[] strArr = {"a","b","c","d"};
        String stri = Database.convertArrayToString(strArr, ":");
        assertEquals(stri, "a:b:c:d:");
    }
}