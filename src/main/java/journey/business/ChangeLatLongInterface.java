package journey.business;

/**
 * A basic interface to allow for retrieving the id of a sale, so it can be fetched from the database
 * This interface accepts any lambda that takes an int and returns a boolean
 */
public interface ChangeLatLongInterface {
    /**
     * Operation.

     * @param lat latitude to record
     * @param lng longitude to record
     * @param label type of the marker
     * @return true if the operation is successful
     */
    boolean operation(double lat, double lng, String label);
}
