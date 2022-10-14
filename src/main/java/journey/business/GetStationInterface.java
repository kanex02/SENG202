package journey.business;

/**
 * A basic interface to allow for retrieving the id of a station, so it can be fetched from the database.
 * This interface accepts any lambda that takes an int and returns a boolean.
 */
public interface GetStationInterface {
    /**
     * Operation.

     * @param id station id.
     * @return true if operation is successful.
     */
    boolean operation(int id);
}
