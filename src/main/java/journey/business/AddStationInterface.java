package journey.business;

import journey.data.Station;

/**
 * A basic interface to allow for passing the add sale lambda function between classes
 * This interface accepts any lambda that takes in a sale and returns void
 */
public interface AddStationInterface {
    /**
     * Operation
     * @param station sale to be passed to underlying operation
     */
    void operation(Station station);
}
