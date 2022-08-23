package journey.controller;

import journey.data.Counter;

/**
 * Simple service class to interact with counters
 * @author seng202 teaching team
 */
public class CounterService {
    private final Counter counter;

    /**
     * Constructor
     */
    public CounterService() {
        counter = new Counter();
    }

    /**
     * Increment our counter by one
     */
    public void incrementCounter() {
        counter.setCount(counter.getCount() + 1);
    }

    /**
     * Get the current count of the counter
     * @return Current count
     */
    public int getCurrentCount() {
        return counter.getCount();
    }
}
