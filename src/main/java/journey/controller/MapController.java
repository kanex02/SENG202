package journey.controller;


import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import journey.business.GetLatLongInterface;
import journey.business.JavaScriptBridge;
import journey.business.StationManager;
import journey.data.Journey;
import journey.data.Station;
import journey.data.Utils;
import journey.repository.StationDAO;
import netscape.javascript.JSObject;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller for displaying Open Street Maps through JavaFX webview
 * @author Morgan English with slight changes/additions form Daniel Neal
 */
public class MapController {
    @FXML private WebView webView;
    private WebEngine webEngine;
    private StationManager stationManager;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;
    private boolean routeDisplayed = false;
    private MainController mainController;
    private GetLatLongInterface callback;
    private StationDAO stationDAO;


    /**
     * Initialise map
     * @param stage map stage
     */
    void init(Stage stage, MainController mainController) {
        // Database db = new Database();
        stationManager = new StationManager(mainController);
        stationDAO = new StationDAO();
        javaScriptBridge = new JavaScriptBridge(this::getStationFromClick, this::getLatLongFromClick);
        this.mainController = mainController;
        // set custom cell factory for list view
        initMap();
    }

    /**
     * Initialises map
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        //Load the html file directly into the web engine for relative paths.
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader()
                .getResource("html/leaflet_osm_map.html")).toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        JSObject window = (JSObject) webEngine.executeScript("window");
                        window.setMember("javaScriptBridge", javaScriptBridge);
                        // get a reference to the js object that has a reference to the js methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap");

                        // add sale markers
                        addStationsOnMap();

                    }
                });


    }

    public void mapJourney(Journey journey) {
        ArrayList<String> waypoints = new ArrayList<>();
        waypoints.add(journey.getStart());
        for (int stationID : journey.getStations()) {
            Station station = stationDAO.queryStation(stationID);
            waypoints.add(station.getLatitude() + "#" + station.getLongitude());
        }
        waypoints.add(journey.getEnd());
        String waypointString =  Utils.convertArrayToString(waypoints.toArray(String[]::new), "//");
        javaScriptConnector.call("mapJourney", waypointString.substring(0, waypointString.length()-2));
    }

    /**
     * add station markers on map if date first operational are not null (not built yet)
     */
    private void addStationsOnMap() {
        Station[] stations = stationManager.getAllStations();
        for (Station station: stations) {
            if (station != null && station.getDateFirstOperational() != null) {
                addStationMark(station);
            }
        }
    }

    /**
     * Adds route to map, calling the underlying js function
     */
    private void addRoute () {
        routeDisplayed = true;
        javaScriptConnector.call("addRoute");
    }

    /**
     * Removes route from map, calling the underlying js function
     */
    private void removeRoute () {
        routeDisplayed = false;
        javaScriptConnector.call("removeRoute");
    }

    /**
     * Simple toggle to hide or display the route on click
     */
    public void toggleRoute() {
        if (routeDisplayed) {
            removeRoute();
        } else {
            addRoute();
        }
    }

    /**
     * Add station to map
     * @param station station object to be added
     */
    private void addStationMark(Station station) {
        javaScriptConnector.call("addMarker", station.getOBJECTID(), station.getShortDescription(), station.getLatitude(), station.getLongitude());
    }

    /**
     * gets station from clicking on a map pointer
     * To be called from {@link JavaScriptBridge} to get the selected charger on click in js
     * @param id id of charger to add
     */
    public boolean getStationFromClick(int id) {
        mainController.setSelectedStation(id);
        mainController.setNoteText();
        mainController.setStationText();
        return true;
    }

    /**
     * Gets the lat and long from clicking on the map
     * To be called from {@link JavaScriptBridge} to get the relevant coordinates
     * @param lat lat of click
     * @param lng long of click
     * @return whether the operation was successful
     */
    public boolean getLatLongFromClick(double lat, double lng) {
        callback.operation(lat, lng);
        //Resets the callback so the previous function is no longer called.
        callback = null;
        return true;
    }

    public void setCallback(GetLatLongInterface callback) {
        this.callback = callback;
    }

    public void clearRoute() {
        javaScriptConnector.call("clearRoute");
    }
}
