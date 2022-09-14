package journey.data;

public class QueryResult {
    private Station[] stations;
    private Vehicle[] vehicles;
    private Journey[] journeys;
    private int pageSize = 20;

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
    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
