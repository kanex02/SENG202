package journey.controller;

import javafx.beans.value.ObservableObjectValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import journey.business.JavaScriptBridge;
import journey.business.StationManager;
import journey.data.Station;
import netscape.javascript.JSObject;

import java.util.Objects;


public class MapController {
    @FXML private WebView webView;
    private WebEngine webEngine;
    private StationManager stationManager;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;
    private boolean routeDisplayed = false;
    private MainController mainController;



    /**
     * Initialise map
     * @param stage map stage
     */
    void init(Stage stage, MainController mainController) {
        // Database db = new Database();
        stationManager = new StationManager();
        javaScriptBridge = new JavaScriptBridge(this::getStationFromClick);
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
        javaScriptConnector.call("addMarker", station.getOBJECTID(), station.getDescription(), station.getLatitude(), station.getLongitude());
    }


    /**
     * gets station from clicking on a map pointer
     * To be called from {@link JavaScriptBridge} to get the selected charger on click in js
     * @param id id of charger to add
     */
    public boolean getStationFromClick(int id) {
        MainController.setSelectedStation(id);
        mainController.setNoteText();
        return true;
    }
}
