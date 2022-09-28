package journey.data;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Utils class
 */
public class UtilsTest {

    @Test
    void isIntTrue() {
        String str = "10";
        assertTrue(Utils.isInt(str));
    }

    @Test
    void isIntFalse() {
        String str = "Hello";
        assertFalse(Utils.isInt(str));
    }

    @Test
    void arrToString() {
        String[] arr = {"We", "are", "the", "Journey", "development", "team"};
        String str = Utils.convertArrayToString(arr, " ");
        Assertions.assertEquals(str, "We are the Journey development team ");
    }
}
