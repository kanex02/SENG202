package journey.business;

import journey.controller.MainController;
import journey.data.QueryResult;
import journey.data.Station;
import journey.repository.StationDAO;
/**
 * Class to handle all actions for stations. This acts as a MVC controller taking requests from the view and completing
 * these using relevant repository layer actions
 */
public class StationManager {
    private MainController mainController;
    private final StationDAO stationDAO;

    public StationManager(MainController mainController) {
        stationDAO = new StationDAO();
        this.mainController = mainController;
    }

    /**
     * Gets all stations in the database
     * @return Object list of all Stations
     */
    public Station[] getAllStations() {
        QueryResult data = mainController.getStations();
        return data.getStations();
    }

    /**
     *  Gets a single station in the database
     * @param id identifier of a station in the database
     * @return a singular station
     */
    public Station getStationById(int id) {
        return stationDAO.queryStation(id);
    }
}
