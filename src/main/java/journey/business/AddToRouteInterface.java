package journey.business;

public interface AddToRouteInterface {
/**
 * A basic interface to allow for retrieving the id of a sale, so it can be fetched from the database
 * This interface accepts any lambda that takes an string and returns a boolean
 */
    /**
     * Operation
     * @return true if operation is successful
     */
    boolean operation(Double lat, Double lng);
}
