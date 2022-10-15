package journey.service;

import java.util.List;

/**
 * Service class to extract testable methods for journey planning.
 */
public class CreateJourneyService {

    private CreateJourneyService() {}
    /**
     * Checks if a journey with the given parameters is valid. Note that a start
     * or end point of (0, 0) is considered invalid, but this is considered acceptable
     * as that point is in the ocean off the coast of Africa, outside NZ.

     * @param vehicle choice of vehicle
     * @param waypoints waypoints of journey
     * @return whether the inputs are valid, in the same order as the function params.
     */

    public static String checkJourney(String vehicle, List<String> waypoints) {
        String valid = "";
        String start = waypoints.get(0);
        String end = waypoints.get(waypoints.size()-1);
        if (vehicle == null || vehicle.isEmpty()) {
            valid += "No vehicle selected!\n";
        }
        if (start.equals("") || end.equals("")) {
            valid += "Invalid journey!\n";
        }
        return valid;
    }
}
