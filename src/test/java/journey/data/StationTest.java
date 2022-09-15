package journey.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationTest {

    @BeforeEach
    void setUp() {
    }

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
}