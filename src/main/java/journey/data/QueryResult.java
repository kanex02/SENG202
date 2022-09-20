package journey.data;

/**
 *
 */
public class QueryResult {
    private Station[] stations;
    private Vehicle[] vehicles;
    private Journey[] journeys;

    public Station[] getStations() {
        return stations;
    }

    public void setStations(Station[] stations) {
        this.stations = stations;
    }

    public void setVehicles(Vehicle[] vehicles) {
        this.vehicles = vehicles;
    }

    public void setJourney(Journey[] journeys) { this.journeys = journeys; }

    public Journey[] getJourney() { return journeys; }

    public Vehicle[] getVehicles() {
        return vehicles;
    }

}
