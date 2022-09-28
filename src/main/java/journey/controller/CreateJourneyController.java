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
import java.util.Objects;


public class CreateJourneyController {
    @FXML private TextField startLat;
    @FXML private TextField startLong;
    @FXML private TextField endLat;
    @FXML private TextField endLong;
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
        String end = endLat.getText() + "#" + endLong.getText();
        String start = startLat.getText() + "#" + startLong.getText();
        int userID = mainController.getCurrentUser().getId();
        String vehicleChoice = selectVehicleComboBox.getValue();
        if (Objects.equals(vehicleChoice, null) || start.equals("lat#long") || end.equals("lat#long")) {
            journeyWarningLabel.setText("Fill all fields");
            valid = false;
        }

        if (valid) {
            journeyWarningLabel.setText("");
            startLat.setText("lat");
            startLong.setText("long");
            endLat.setText("lat");
            endLong.setText("long");
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
            startLat.setText(String.valueOf(lat));
            startLong.setText(String.valueOf(lng));
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
            endLat.setText(String.valueOf(lat));
            endLong.setText(String.valueOf(lng));
            mainController.reenable();
            return true;
        }, "end");
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
