package journey.business;

public interface SetStartAddrInterface {
/**
 * A basic interface to allow for retrieving the id of a sale, so it can be fetched from the database
 * This interface accepts any lambda that takes an string and returns a boolean
 */
    /**
     * Operation
     * @param addr address of a location in the form of a string
     * @return true if operation is successful
     */
    boolean operation(String addr);
}
