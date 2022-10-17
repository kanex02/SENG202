package journey.data;

import journey.Utils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Utils class
 */
class UtilsTest {

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
        Assertions.assertEquals("We are the Journey development team ", str);
    }
    @Test
    void emptyArrToString() {
        String[] arr = {};
        String str = Utils.convertArrayToString(arr, " ");
        Assertions.assertEquals("", str);
    }


    @Test
    void convertArrayListToString() {
        ArrayList<String> arr = new ArrayList<>(asList("We", "are", "the", "Journey", "development", "team"));
        String str = Utils.convertArrayListToString(arr, " ");
        Assertions.assertEquals("We are the Journey development team", str);
    }
    @Test
    void convertEmptyArrayListToString() {
        ArrayList<String> arr = new ArrayList<>();
        String str = Utils.convertArrayListToString(arr, " ");
        Assertions.assertEquals("", str);

    }
    @Test
    void locToLatLng() {
        String loc = "jack erskine";
        String str = Utils.locToLatLng(loc);
        Assertions.assertEquals("-43.52249#172.58119", str);
    }
    @Test
    void locToLatLngEmpty() {
        String loc = "this is not a valid address";
        String str = Utils.locToLatLng(loc);
        Assertions.assertEquals("0.0#0.0", str);
    }
    @Test
    void latLngToAddr() {
        double lat = -43.52249;
        double lng = 172.58119;
        String str = Utils.latLngToAddr(lat, lng);
        Assertions.assertEquals("University of Canterbury, Waimairi Road, Upper Riccarton, Christchurch, Christchurch City, Canterbury, 8041", str);
    }
    @Test
    void latLngToAddrEmpty() {
        double lat = 0;
        double lng = 0;
        String str = Utils.latLngToAddr(lat, lng);
        Assertions.assertEquals("Soul Buoy", str);
    }

}
