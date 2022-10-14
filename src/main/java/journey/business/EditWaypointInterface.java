package journey.business;

/**
 * A basic interface to allow for changing the lat and lng of a route, so it can be fetched from the database.
 * This interface accepts any lambda that takes two doubles and a int and returns a boolean.
 */
public interface EditWaypointInterface {
    /**
     * Operation.

     * @param lat latitude to record.
     * @param lng longitude to record.
     * @param position position of waypoint (in route).
     * @return true if the operation is successful.
     */
    boolean operation(double lat, double lng, int position);
}
