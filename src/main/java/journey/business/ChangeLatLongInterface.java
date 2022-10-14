package journey.business;

/**
 * A basic interface to allow for changing the lat and lng of a marker, so it can be fetched from the database.
 * This interface accepts any lambda that takes two doubles and a string and returns a boolean.
 */
public interface ChangeLatLongInterface {
    /**
     * Operation.

     * @param lat latitude to record.
     * @param lng longitude to record.
     * @param label type of the marker.
     * @return true if the operation is successful.
     */
    boolean operation(double lat, double lng, String label);
}
