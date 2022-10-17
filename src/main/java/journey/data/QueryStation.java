package journey.data;


/**
 * A class that models a station, for use in results and queries.
 * X and Y have been excluded, as latitude and longitude are more widely used.
 */
public class QueryStation extends Station {
    private double range;

    /**
     * calls the constructor of the station class
     */
    public QueryStation() {
        super();
    }

    public double getRange() {
        return range;
    }

    public void setRange(double range) {
        this.range = range;
    }
}
