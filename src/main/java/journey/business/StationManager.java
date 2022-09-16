package journey.business;

import journey.controller.MainController;
import journey.data.Database;
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

    /**
     *  Gets a single station in the database
     * @param id identifier of a station in the database
     * @return a singular station
     */
    public Station getStationById(int id) {
        return Database.queryStation(id);
    }
}
