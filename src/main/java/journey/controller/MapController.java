package journey.controller;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import journey.Utils;
import journey.business.GetLatLongInterface;
import journey.business.JavaScriptBridge;
import journey.data.Journey;
import journey.data.Station;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;



/**
 * Controller for displaying Open Street Maps through JavaFX webview.

 * @author Morgan English with changes/additions from Daniel Neal
 */
public class MapController {
    @FXML private WebView webView;
    @FXML private Button toggleRouteButton;
    @FXML private AnchorPane legendWrapper;
    @FXML private Button legendButton;
    @FXML private ImageView legendAImage;
    @FXML private ImageView legendBImage;
    @FXML private ImageView legendCImage;
    @FXML private ImageView legendDImage;
    @FXML private ImageView legendEImage;
    private JavaScriptBridge javaScriptBridge;
    private JSObject javaScriptConnector;
    private boolean routeDisplayed = false;
    private boolean routeEditable = true;
    private MainController mainController;
    private GetLatLongInterface callback;
    private String label;
    private boolean showLegend;
    private static final Logger log = LogManager.getLogger();

    /**
     * Legend button for all the different markers.
     */
    @FXML private void legendButton() {
        if (showLegend) {
            legendWrapper.setVisible(false);
            legendButton.setText("Show Legend");
            showLegend = false;
        } else {
            legendWrapper.setVisible(true);
            legendButton.setText("Hide Legend");
            showLegend = true;
        }
    }

    private void insertLegendImg() {
        Image img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/blue-marker.png"))
                ));
        (legendAImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/marker-icon-2x-violet.png"))
                ));
        (legendBImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/marker-icon-2x-gold.png"))
                ));
        (legendCImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/marker-icon-2x-green.png"))
                ));
        (legendDImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/marker-icon-2x-red.png"))
                ));
        (legendEImage).setImage(img);

    }

    /**
     * View the marker legend.
     */
    private void viewLegend() {
        try {
            FXMLLoader legendLoader = new FXMLLoader(MapController.class.getResource("/fxml/legend.fxml"));
            Parent legendParent = legendLoader.load();
            ((MapController) legendLoader.getController()).insertLegendImg();
            legendWrapper.getChildren().add(legendParent);
            AnchorPane.setTopAnchor(legendParent, 0d);
            AnchorPane.setBottomAnchor(legendParent, 0d);
            AnchorPane.setLeftAnchor(legendParent, 0d);
            AnchorPane.setRightAnchor(legendParent, 0d);
            legendButton.setText("Hide Legend");
            showLegend = true;
        } catch (IOException e) {
            log.error(e);

        }
    }

    /**
     * Insert Waypoint.

     * @param lat latitude
     * @param lng longitude
     * @param position position
     * @return true
     */
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
        WebEngine webEngine = webView.getEngine();
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
                        addStationsOnMap();
                    }
                });
    }

    /**
     * Add a new waypoint to the route.

     * @param lat latitude.
     * @param lng longitude.
     * @return true
     */
    public boolean addToRoute(double lat, double lng) {
        mainController.addNewWaypoint(lat, lng);
        return true;
    }

    /**
     * Maps a journey.

     * @param journey Journey to map
     */
    public void mapJourney(Journey journey) {
        List<String> waypoints = journey.getWaypoints();
        if (waypoints.size() >= 2) {
            String waypointString = Utils.convertArrayToString(waypoints.toArray(String[]::new), "//");
            javaScriptConnector.call("mapJourney", waypointString.substring(0, waypointString.length() - 2), false);
            routeDisplayed = true;
            routeEditable = false;
            setToggle(true);
        }
    }

    /**
     * Calls function to set the previous marker to be a favourite so that the colour of the marker is
     * correct after submitting a note.
     */
    public void setPrevMarkerFavourite() {
        javaScriptConnector.call("setPrevMarkerFavourite", true);
    }

    /**
     * Sets the selected station id to the currently selected station.
     * @param id the id of the selected station
     */
    public void setSelectedStationID(int id, boolean favourite) {
        javaScriptConnector.call("setSelectedMarkerId", id, favourite);
    }

    /**
     * Map a journey currently being planned.

     * @param waypoints the waypoints of the journey
     */
    public void mapJourneyFromLatLng(String[] waypoints) {
        String waypointString =  Utils.convertArrayToString(waypoints, "//");
        routeEditable = true;
        javaScriptConnector.call("mapJourney", waypointString.substring(0, waypointString.length() - 2), true);
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
     * Function to call to add the range indicator circle on the map.

     * @param lat The latitude of the circle.
     * @param lng The longitude of the circle.
     * @param radius The radius of the circle.
     */
    public void addRangeIndicator(double lat, double lng, int radius) {
        javaScriptConnector.call("addRangeIndicator", lat, lng, radius);
    }

    /**
     * Removes the range indicator marker from the map.
     */
    public void removeRangeIndicator() {
        javaScriptConnector.call("removeRangeIndicator");
    }

    /**
     * Sets the toggle button to be visible/not visible.

     * @param toggleOn visible/not visible.
     */
    private void setToggle(boolean toggleOn) {
        toggleRouteButton.setVisible(toggleOn);
    }

    /**
     * Adds route to map, calling the underlying js function.
     */
    private void addRoute() {
        routeDisplayed = true;
        javaScriptConnector.call("addRoute", routeEditable);
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
    @FXML private void toggleRoute() {
        if (routeDisplayed) {
            removeRoute();
            toggleRouteButton.setText("Display Route");
        } else {
            addRoute();
            toggleRouteButton.setText("Hide Route");
        }
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
        javaScriptConnector.call("addMarker", station.getObjectid(),
                station.getShortDescription(), station.getLatitude(), station.getLongitude(), station.getFavourite());
    }

    /**
     * gets station from clicking on a map pointer.
     * To be called from {@link JavaScriptBridge} to get the selected charger on click in js

     * @param id id of charger to add
     */
    public boolean getStationFromClick(int id) {
        mainController.setSelectedStation(id);
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

    /**
     * Updates the lat and long of a marker.
     * To be called from {@link JavaScriptBridge} at the end of a drag.

     * @param lat new latitude
     * @param lng new longitude
     * @param label type of marker (search, journey start, or journey end)
     * @return whether the operation was successful
     */
    public boolean changeLatLong(double lat, double lng, String label) {
        if ("search".equals(label)) {
            mainController.changeSearchLatLong(lat, lng);
            removeRangeIndicator();
            mainController.addRangeIndicator(lat, lng);
            mainController.refreshSearch();
        } else if (Utils.isInt(label)) {
            mainController.editWaypoint(lat, lng, Integer.parseInt(label));
        }
        return true;
    }

    /**
     * Calls remove route in the javascript class.
     */
    public void clearRoute() {
        javaScriptConnector.call("removeRoute");
    }

    /**
     * Initialise map class.
     */
    public void init(MainController mainController) {
        javaScriptBridge = new JavaScriptBridge(this::getStationFromClick,
                this::getLatLongFromClick,
                this::changeLatLong,
                this::addToRoute,
                this::editWaypoint,
                this::insertWaypoint);
        this.mainController = mainController;
        // set custom cell factory for list view
        setToggle(false);
        initMap();
        viewLegend();

    }
}
