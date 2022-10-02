package journey.data;

import journey.Utils;
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
    @Test
    void locToLatLng() {
        String loc = "jack erskine";
        String str = Utils.locToLatLng(loc);
        Assertions.assertEquals(str, 	"-43.52249#172.58119");
    }
    @Test
    void locToLatLngEmpty() {
        String loc = "this is not a valid address";
        String str = Utils.locToLatLng(loc);
        Assertions.assertEquals(str, "0.0#0.0");
    }
    @Test
    void latLngToAddr() {
        double lat = -43.52249;
        double lng = 172.58119;
        String str = Utils.latLngToAddr(lat, lng);
        Assertions.assertEquals(str, "University of Canterbury, Waimairi Road, Upper Riccarton, Christchurch, Christchurch City, Canterbury, 8041");
    }
    @Test
    void latLngToAddrEmpty() {
        double lat = 0;
        double lng = 0;
        String str = Utils.latLngToAddr(lat, lng);
        Assertions.assertEquals(str, "Soul Buoy");
    }

}
