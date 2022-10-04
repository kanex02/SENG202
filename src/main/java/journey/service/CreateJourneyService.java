package journey.service;

import journey.Utils;

/**
 * Service class to extract testable methods for journey planning.
 */
public class CreateJourneyService {
    /**
     * Checks if a journey with the given parameters is valid. Note that a start
     * or end point of (0, 0) is considered invalid, but this is considered acceptable
     * as that point is in the ocean off the coast of Africa, outside NZ.

     * @param vehicle choice of vehicle
     * @param start start point
     * @param end end point
     * @return whether the inputs are valid, in the same order as the function params.
     */
    public static Boolean[] checkJourney(String vehicle, String start, String end) {
        Boolean[] valid = new Boolean[] {true, true, true};
        if (vehicle == null || vehicle.isEmpty()) {
            valid[0] = false;
        }
        if (start.equals("") || Utils.locToLatLng(start).equals("0.0#0.0")) {
            valid[1] = false;
        }
        if (end.equals("") || Utils.locToLatLng(end).equals("0.0#0.0")) {
            valid[2] = false;
        }
        return valid;
    }
}
