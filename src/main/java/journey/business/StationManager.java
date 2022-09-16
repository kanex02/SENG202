package journey.business;

import journey.controller.MainController;
import journey.data.QueryResult;
import journey.data.Station;

public class StationManager {
    /**
     * Gets all stations in the database
     * @return Object list of all Stations
     */
    public Station[] getAllStations() {
        QueryResult data = MainController.getStations();
        return data.getStations();
    }
}
