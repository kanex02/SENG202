package journey.business;

import journey.data.Database;
import journey.data.QueryResult;
import journey.data.Station;

public class StationManager {
    /**
     * Gets all stations in the database
     * @return Object list of all Stations
     */
    public Station[] getAllStations() {
        QueryResult data = Database.catchEmAll();
        return data.getStations();
    }

}
