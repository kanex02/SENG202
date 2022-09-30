package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.data.Journey;
import journey.data.QueryResult;
import journey.data.Utils;
import journey.data.Vehicle;
import journey.repository.JourneyDAO;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;

import java.util.ArrayList;


public class CreateJourneyController {
    @FXML private TextField startAddr;
    @FXML private TextField endAddr;
    @FXML private TextField selectedStationField;
    @FXML private ListView<String> visitedStationsList;
    @FXML private ComboBox<String> selectVehicleComboBox;
    @FXML private Label journeyWarningLabel;

    private MainController mainController;
    private MapController mapViewController;
    private JourneyDAO journeyDAO;
    private StationDAO stationDAO;
    private VehicleDAO vehicleDAO;
    private final ArrayList<Integer> journeyStations = new ArrayList<>();


    public void updateSelectedStation(int selectedStation) {
        selectedStationField.setText(stationDAO.queryStation(selectedStation).getAddress());
    }

    public void populateVehicleDropdown() {
        QueryResult data = vehicleDAO.getVehicles(mainController.getCurrentUser());
        ObservableList<String> vehicles = FXCollections.observableArrayList();
        for (Vehicle vehicle : data.getVehicles()) {
            String newString = vehicle.getStringRepresentation();
            vehicles.add(newString);
        }
        selectVehicleComboBox.setItems(vehicles);
    }

    @FXML private void selectStation(Event event) {
        int selectedStation = mainController.getSelectedStation();
        if(selectedStation != -1 && (journeyStations.size() == 0 || journeyStations.get(journeyStations.size()-1) != selectedStation)) {
            journeyStations.add(selectedStation);
            ObservableList<String> visitedStations = visitedStationsList.getItems();
            visitedStations.add(stationDAO.queryStation(selectedStation).getAddress());
            visitedStationsList.setItems(visitedStations);
        }
        event.consume();
    }


    @FXML private void addJourney(Event event) {
        boolean valid = true;
        String end = endAddr.getText();
        String start = startAddr.getText();
        int userID = mainController.getCurrentUser().getId();
        String vehicleChoice = selectVehicleComboBox.getValue();
        if (vehicleChoice.equals("")|| start.equals("") || end.equals("")) {
            journeyWarningLabel.setText("Fill all fields");
            valid = false;
        }
        if (Utils.locToLatLng(start).equals("0.0#0.0")) {
            journeyWarningLabel.setText("Start Address Invalid");
            valid = false;
        }
        if (Utils.locToLatLng(end).equals("0.0#0.0")) {
            journeyWarningLabel.setText("End Address Invalid");
            valid = false;
        }
        if (valid) {
            journeyWarningLabel.setText("");
            selectVehicleComboBox.setValue("");
            visitedStationsList.setItems(FXCollections.observableArrayList());
            String[] vehicle = vehicleChoice.split(": ");
            String date = Utils.getDate();
            Journey journey = new Journey(start, end, vehicle[0], userID, date, journeyStations);
            journeyDAO.addJourney(journey);
            mapViewController.clearJourneyMarkers();
            event.consume();
        }
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the journey start lat and long is updated.
     */
    @FXML private void clickStart() {
        mainController.onlyMap();
        mainController.openMap();
        mapViewController.setCallback((lat, lng) -> {
            String addr = Utils.latLngToAddr(lat, lng);
            startAddr.setText(addr);
            mainController.reenable();
            return true;
        }, "start");
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the journey end lat and long is updated.
     */
    @FXML private void clickEnd() {
        mainController.onlyMap();
        mainController.openMap();
        mapViewController.setCallback((lat, lng) -> {
            String addr = Utils.latLngToAddr(lat, lng);
            endAddr.setText(addr);
            mainController.reenable();
            return true;
        }, "end");
    }

    public void changeJourneyStart(String addr) {
        startAddr.setText(addr);
    }

    public void changeJourneyEnd(String addr) {
        endAddr.setText(addr);
    }

    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        this.mapViewController = mainController.getMapViewController();
        this.journeyDAO = new JourneyDAO();
        this.stationDAO = new StationDAO();
        this.vehicleDAO = new VehicleDAO();
        populateVehicleDropdown();
    }
}
