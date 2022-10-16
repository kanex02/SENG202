package journey.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CreateJourneyServiceTest {

    @Test
    void checkJourneyVehicleTest() {
        List<String> waypoints = new ArrayList<>();
        waypoints.add("start");
        waypoints.add("end");
        String error = CreateJourneyService.checkJourney(null, waypoints);
        assertEquals(error, "No vehicle selected!\n");
    }

    @Test
    void checkJourneyStart() {
        List<String> waypoints = new ArrayList<>();
        waypoints.add("start");
        waypoints.add("");
        String error = CreateJourneyService.checkJourney("registration", waypoints);
        assertEquals(error, "Invalid journey!\n");
    }
}
