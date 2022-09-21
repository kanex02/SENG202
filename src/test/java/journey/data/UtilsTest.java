package journey.data;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Utils class
 */
public class UtilsTest {

    @Test
    void isIntTrue() {
        String str = "10";
        assertEquals(Utils.isInt(str), true);
    }

    @Test
    void isIntFalse() {
        String str = "Hello";
        assertEquals(Utils.isInt(str), false);
    }
}
