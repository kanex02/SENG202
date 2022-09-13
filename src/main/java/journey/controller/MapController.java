package journey.controller;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import journey.business.StationManager;
// import journey.data.JavaScriptBridge;
// import org.apache.logging.log4j.LogManager;
// import org.apache.logging.log4j.Logger;
import journey.data.Station;
import netscape.javascript.JSObject;

import java.util.Objects;


public class MapController {
    @FXML private WebView webView;
    private WebEngine webEngine;
    private StationManager stationManager;
    private JSObject javaScriptConnector;



    /**
     * Initialise map
     * @param stage map stage
     */
    void init(Stage stage) {
        // Database db = new Database();
        stationManager = new StationManager();
        initMap();
    }

    /**
     * Initialises map
     */
    private void initMap() {
        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        //Load the html file directly into the web engine for relative paths.
        webEngine.load(Objects.requireNonNull(getClass().getClassLoader().getResource("html/leaflet_osm_map.html")).toExternalForm());

        webEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    // if javascript loads successfully
                    if (newState == Worker.State.SUCCEEDED) {
                        // set our bridge object
                        // JSObject window = (JSObject) webEngine.executeScript("window");
                        // window.setMember("javaScriptBridge", javaScriptBridge);
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
     * Add station to map
     * @param station station object to be added
     */
    private void addStationMark(Station station) {
        javaScriptConnector.call("addMarker", station.getDescription(), station.getLatitude(), station.getLongitude());
    }
}
