package journey.data;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationTest {
    //Only the distanceTo function needs to be tested, as the rest are trivial.
    @Test
    void distanceTo() {
        Station first = new Station();
        Station second = new Station();
        first.setLatitude(53.32055555555556);
        first.setLongitude(-1.7297222222222221);
        second.setLatitude(53.31861111111111);
        second.setLongitude(-1.6997222222222223 );
        double distance = first.distanceTo(second);
        double modelAnswer = 2;
        assertTrue(distance > modelAnswer*0.9 && distance < modelAnswer*1.1);
    }

    @Test
    void distanceToSame() {
        Station first = new Station();
        Station second = new Station();
        first.setLatitude(23423.56);
        first.setLongitude(-8454.3);
        second.setLatitude(23423.56);
        second.setLongitude(-8454.3 );
        double distance = first.distanceTo(second);
        assertEquals(0, distance);
    }
}