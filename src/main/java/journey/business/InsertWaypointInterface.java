package journey.business;

/**
 * A basic interface to allow for retrieving the lat and long of a click.
 * This interface accepts any lambda that takes two doubles and an int and returns a boolean.
 */
public interface InsertWaypointInterface {
    /**
     * Operation.

     * @param lat latitude to record.
     * @param lng longitude to record.
     * @return true if the operation is successful.
     */
    boolean operation(double lat, double lng, int position);
}
