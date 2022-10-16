package journey.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;


class CreateJourneyServiceTest {

    @Test
    void checkJourneyVehicleTest() {
        List<String> waypoints = new ArrayList<>();
        waypoints.add("start");
        waypoints.add("end");
        String error = CreateJourneyService.checkJourney(null, waypoints);
        assertEquals("No vehicle selected!\n", error);
    }

    @Test
    void checkJourneyStart() {
        List<String> waypoints = new ArrayList<>();
        waypoints.add("start");
        waypoints.add("");
        String error = CreateJourneyService.checkJourney("registration", waypoints);
        assertEquals("Invalid journey!\n", error);
    }
}
