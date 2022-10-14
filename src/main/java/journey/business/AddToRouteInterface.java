package journey.business;

/**
 * A basic interface to allow for retrieving the id of a route, so it can be fetched from the database.
 * This interface accepts any lambda that takes two doubles and returns a boolean.
 */
public interface AddToRouteInterface {
    /**
     * Operation.

     * @return true if operation is successful.
     */
    boolean operation(Double lat, Double lng);
}
