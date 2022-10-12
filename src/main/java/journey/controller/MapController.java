package journey.controller;

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

import java.util.ArrayList;
import java.util.Objects;

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
        stationDAO = new StationDAO();
        javaScriptBridge = new JavaScriptBridge(this::getStationFromClick,
                this::getLatLongFromClick,
                this::changeLatLong,
                this::addToRoute,
                this::editWaypoint,
                this::insertWaypoint);
        this.mainController = mainController;
        // set custom cell factory for list view
        initMap();
    }

    private boolean insertWaypoint(double lat, double lng, int position) {
        mainController.insertWaypoint(lat, lng, position);
        return true;
    }

    public void clearWaypoint(int i) {
        javaScriptConnector.call("clearMiscMarker", i);
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

    public boolean addToRoute(double lat, double lng) {
        return true;
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


    public void mapJourneyFromLatLng(String[] waypoints) {
        String waypointString =  Utils.convertArrayToString(waypoints, "//");
        javaScriptConnector.call("mapJourney", waypointString.substring(0, waypointString.length() - 2));
    }

    /**
     * add station markers on map if date first operational are not null (not built yet).
     */
    public void addStationsOnMap() {
        javaScriptConnector.call("clearMap");
        Station[] stations = mainController.getStations();
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


    public void clearSearch() {
        javaScriptConnector.call("clearMiscMarker", "search");
    }

    public void clearWaypoints() {
        javaScriptConnector.call("clearWaypoints");
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
        mainController.updateNote();
        return true;
    }

    public boolean editWaypoint(Double lat, Double lng, int position) {
        mainController.editWaypoint(lat, lng, position);
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
            javaScriptConnector.call("addMiscMarker", lat, lng, label);
            callback.operation(lat, lng);
        }
        //Resets the callback so the previous function is no longer called.
        callback = null;
        label = null;
        return true;
    }

    public void addMiscMarker(double lat, double lng, String label) {
        javaScriptConnector.call("addMiscMarker", lat, lng, label);
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

        if ("search".equals(label)) {
            mainController.changeSearchLatLong(addr.getAddress());
            mainController.refreshSearch();
        } else if (Utils.isInt(label)) {
            mainController.editWaypoint(lat, lng, Integer.parseInt(label));
        }
        return true;
    }

    public void clearRoute() {
        javaScriptConnector.call("removeRoute");
    }
}
