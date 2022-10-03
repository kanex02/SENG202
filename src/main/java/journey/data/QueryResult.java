package journey.data;

/**
 * intermediary result of a query to be allocated to station, vehicle, or journey
 */
public class QueryResult {
    private Station[] stations;
    private Vehicle[] vehicles;
    private Journey[] journeys;
    private User[] users;

    public Station[] getStations() {
        return stations;
    }

    public void setStations(Station[] stations) {
        this.stations = stations;
    }

    public void setVehicles(Vehicle[] vehicles) {
        this.vehicles = vehicles;
    }

    public void setJourney(Journey[] journeys) {
        this.journeys = journeys;
    }

    public void setUsers(User[] users) {
        this.users = users;
    }


    public Journey[] getJourney() {
        return journeys;
    }

    public Vehicle[] getVehicles() {
        return vehicles;
    }

    public User[] getUsers() {return users;}


}
