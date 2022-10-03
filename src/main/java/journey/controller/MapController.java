package journey.controller;

import java.util.ArrayList;
import java.util.Objects;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import journey.Utils;
import journey.business.GetLatLongInterface;
import journey.business.JavaScriptBridge;
import journey.business.NominatimGeolocationManager;
import journey.data.GeoCodeResult;
import journey.data.Journey;
import journey.data.Station;
import journey.repository.StationDAO;
import netscape.javascript.JSObject;

/**
 * Controller for displaying Open Street Maps through JavaFX webview.

 * @author Morgan English with slight changes/additions form Daniel Neal
 */
public class MapController {
    @FXML private WebView webView;
    private WebEngine webEngine;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;
    private boolean routeDisplayed = false;
    private MainController mainController;
    private GetLatLongInterface callback;
    private StationDAO stationDAO;
    private String label;

    /**
     * Initialise map class.
     */
    void init(MainController mainController) {
        // Database db = new Database();
        stationDAO = new StationDAO();
        javaScriptBridge = new JavaScriptBridge(this::getStationFromClick,
                this::getLatLongFromClick,
                this::changeLatLong);
        this.mainController = mainController;
        // set custom cell factory for list view
        initMap();
    }

    /**
     * Initialises map.
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
                        // get a reference to the js object that has a reference to the js
                        // methods we need to use in java
                        javaScriptConnector = (JSObject) webEngine.executeScript("jsConnector");

                        javaScriptConnector.call("initMap");

                        // add sale markers
                        addStationsOnMap();
                    }
                });
    }



    /**
     * Maps a journey.

     * @param journey Journey to map
     */
    public void mapJourney(Journey journey) {
        ArrayList<String> waypoints = new ArrayList<>();
        waypoints.add(Utils.locToLatLng(journey.getStart()));
        for (int stationID : journey.getStations()) {
            Station station = stationDAO.queryStation(stationID);
            waypoints.add(station.getLatitude() + "#" + station.getLongitude());
        }
        waypoints.add(Utils.locToLatLng(journey.getEnd()));
        String waypointString =  Utils.convertArrayToString(waypoints.toArray(String[]::new), "//");
        javaScriptConnector.call("mapJourney", waypointString.substring(0, waypointString.length() - 2));
        routeDisplayed = true;
    }

    /**
     * add station markers on map if date first operational are not null (not built yet).
     */
    public void addStationsOnMap() {
        javaScriptConnector.call("clearMap");
        Station[] stations = mainController.getStations().getStations();
        for (Station station : stations) {
            if (station != null && station.getDateFirstOperational() != null) {
                addStationMark(station);
            }
        }
    }

    /**
     * Adds route to map, calling the underlying js function.
     */
    private void addRoute() {
        routeDisplayed = true;
        javaScriptConnector.call("addRoute");
    }

    /**
     * Removes route from map, calling the underlying js function.
     */
    private void removeRoute() {
        routeDisplayed = false;
        javaScriptConnector.call("removeRoute");
    }

    /**
     * Simple toggle to hide or display the route on click.
     */
    public void toggleRoute() {
        if (routeDisplayed) {
            removeRoute();
        } else {
            addRoute();
        }
    }

    public void clearSearch() {
        javaScriptConnector.call("clearSearch");
    }

    /**
     * Add station to map.

     * @param station station object to be added
     */
    private void addStationMark(Station station) {
        javaScriptConnector.call("addMarker", station.getOBJECTID(),
                station.getShortDescription(), station.getLatitude(), station.getLongitude());
    }

    /**
     * gets station from clicking on a map pointer.
     * To be called from {@link JavaScriptBridge} to get the selected charger on click in js

     * @param id id of charger to add
     */
    public boolean getStationFromClick(int id) {
        mainController.setSelectedStation(id);
        mainController.updateNoteText();
        mainController.setStationText();
        return true;
    }

    /**
     * Gets the lat and long from clicking on the map.
     * To be called from {@link JavaScriptBridge} to get the relevant coordinates.

     * @param lat lat of click
     * @param lng long of click
     * @return whether the operation was successful
     */
    public boolean getLatLongFromClick(double lat, double lng) {
        if (callback != null) {
            callback.operation(lat, lng);
            javaScriptConnector.call("addMiscMarker", lat, lng, label);
        }
        //Resets the callback so the previous function is no longer called.
        callback = null;
        label = null;
        return true;
    }

    public void setCallback(GetLatLongInterface callback, String label) {
        this.callback = callback;
        this.label = label;
    }

    public void clearJourneyMarkers() {
        javaScriptConnector.call("clearMiscMarker", "start");
        javaScriptConnector.call("clearMiscMarker", "end");
    }

    /**
     * Updates the lat and long of a marker.
     * To be called from {@link JavaScriptBridge} at the end of a drag.

     * @param lat new latitude
     * @param lng new longitude
     * @param label type of marker (search, journey start, or journey end)
     * @return whether the operation was successful
     */
    public boolean changeLatLong(double lat, double lng, String label) {
        NominatimGeolocationManager nomMan = new NominatimGeolocationManager();
        GeoCodeResult addr = nomMan.queryLatLng(lat, lng);
        switch (label) {
            case ("search") -> {
                mainController.changeSearchLatLong(addr.getAddress());
                mainController.refreshSearch();
            }
            case ("start") -> mainController.changeJourneyStart(addr.getAddress());
            case ("end") -> mainController.changeJourneyEnd(addr.getAddress());
            default -> { }
        }
        return true;
    }

    public void clearRoute() {
        javaScriptConnector.call("clearRoute");
    }
}
