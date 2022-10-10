package journey.controller;

import java.util.ArrayList;
import java.util.StringJoiner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import journey.Utils;
import journey.business.SearchAutocomplete;
import journey.data.Journey;
import journey.data.Station;
import journey.data.Vehicle;
import journey.repository.JourneyDAO;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
import journey.service.CreateJourneyService;

/**
 * Class to handle creating a journey given a start, end and chargers along the way.
 */
public class CreateJourneyController {
    @FXML private TextField startAddr;
    @FXML private TextField endAddr;
    @FXML private TextField selectedStationField;
    @FXML private ListView<String> visitedStationsList;
    @FXML private ComboBox<String> selectVehicleComboBox;
    @FXML private Label journeyWarningLabel;
    @FXML private VBox matchAddrStart;
    @FXML private VBox matchAddrEnd;
    @FXML private ScrollPane startAddrScroll;
    @FXML private ScrollPane endAddrScroll;
    private Double startLat;
    private Double startLng;
    private Double endLat;
    private Double endLng;
    private MainController mainController;
    private MapController mapViewController;
    private JourneyDAO journeyDAO;
    private StationDAO stationDAO;
    private VehicleDAO vehicleDAO;
    private ArrayList<String> waypoints;
    private final ArrayList<Integer> journeyStations = new ArrayList<>();

    public void updateSelectedStation(int selectedStation) {
        selectedStationField.setText(stationDAO.queryStation(selectedStation).getAddress());
    }

    public void addRouteWaypoint(Double lat, Double lng, int position) {
        if (position < waypoints.size()) {
            waypoints.set(position, lat+"#"+lng);
        } else {
            waypoints.add(lat+"#"+lng);
        }
    }

    /**
     * Provides all the users vehicles to choose from for a journey.

     */
    public void populateVehicleDropdown() {
        Vehicle[] data = vehicleDAO.getVehicles(mainController.getCurrentUser());
        ObservableList<String> vehicles = FXCollections.observableArrayList();
        for (Vehicle vehicle : data) {
            String newString = vehicle.getStringRepresentation();
            vehicles.add(newString);
        }
        selectVehicleComboBox.setItems(vehicles);
    }

    /**
     * Ensures all fields are filled and valid then adds the filed journey.

     * @param event addJourney button pressed
     */
    @FXML private void addJourney(Event event) {
        String vehicleChoice = selectVehicleComboBox.getValue();
        String start = startAddr.getText();
        String end = endAddr.getText();
        int userID = mainController.getCurrentUser().getId();

        // Check if the inputs are valid
        Boolean[] valid = CreateJourneyService.checkJourney(vehicleChoice, start, end);

        StringJoiner errors = new StringJoiner("\n");

        if (!valid[0]) {
            errors.add("Please select a vehicle");
        }

        if (!valid[1]) {
            errors.add("Start location invalid");
        }

        if (!valid[2]) {
            errors.add("End location invalid");
        }

        journeyWarningLabel.setText(errors.toString());

        boolean validJourney = true;
        for (Boolean bool : valid) {
            if (!bool) {
                validJourney = false;
                break;
            }
        }

        if (validJourney) {
            journeyWarningLabel.setText("");
            selectVehicleComboBox.setValue("");
            startAddr.setText("");
            endAddr.setText("");
            selectedStationField.setText("");
            visitedStationsList.setItems(FXCollections.observableArrayList());
            String[] vehicle = vehicleChoice.split(": ");
            String date = Utils.getDate();
            Journey journey = new Journey(start, end, vehicle[0], userID, date, journeyStations);
            journeyDAO.addJourney(journey);
            mapViewController.clearJourneyMarkers();
            mainController.updatePlannedJourneys();
            event.consume();
        }
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the journey start lat and long is updated.
     */
    @FXML private void clickStart() {
        mainController.openMap();
        mapViewController.setCallback((lat, lng) -> {
            changeJourneyStart(lat,lng);
            return true;
        }, "start");
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the journey end lat and long is updated.
     */
    @FXML private void clickEnd() {
        mainController.openMap();
        mapViewController.setCallback((lat, lng) -> {
            changeJourneyEnd(lat, lng);
            return true;
        }, "end");
    }

    public void changeJourneyStart(String addr) {
        startAddr.setText(addr);
        String[] latLng = Utils.locToLatLng(addr).split("#");
        startLat = Double.valueOf(latLng[0]);
        endLat = Double.valueOf(latLng[1]);
        updateJourney();
    }

    public void changeJourneyStart(Double lat, Double lng) {
        startLat = lat;
        startLng = lng;
        startAddr.setText(Utils.latLngToAddr(lat, lng));
        updateJourney();
    }

    public void changeJourneyEnd(String addr) {
        endAddr.setText(addr);
        String[] latLng = Utils.locToLatLng(addr).split("#");
        endLng = Double.valueOf(latLng[0]);
        endLng = Double.valueOf(latLng[1]);
        updateJourney();
    }

    public void changeJourneyEnd(Double lat, Double lng) {
        endLat = lat;
        endLng = lng;
        endAddr.setText(Utils.latLngToAddr(lat, lng));
        updateJourney();
    }

    private void updateJourney() {
        if (!startAddr.getText().isBlank() && !endAddr.getText().isBlank()) {
            mainController.clearStart();
            mainController.clearEnd();
            mainController.mapJourneyFromLatLng(new String[]{startLat+"#"+startLng, endLat+"#"+endLng});
        }
    }

    /**
     * Function to control the autocomplete for the start search box.

     * @param start true if the autocomplete is for the start address,
     *              false for the end address
     */
    public void autoComplete(boolean start) {

        //determine if editing start or end
        TextField addrTextField;
        VBox matchAddr;
        ScrollPane addrScroll;
        if (start) {
            addrTextField = startAddr;
            matchAddr = matchAddrStart;
            addrScroll = startAddrScroll;
        } else {
            addrTextField = endAddr;
            matchAddr = matchAddrEnd;
            addrScroll = endAddrScroll;
        }
        ArrayList<Button> buttonList = new ArrayList<>();
        String address = addrTextField.getText();
        SearchAutocomplete search = new SearchAutocomplete();
        ArrayList<String> results = search.getMatchingAddresses(address);

        // Event handler for each button - should update the text in the field
        EventHandler<ActionEvent> event = actionEvent -> {
            String addr = ((Button) actionEvent.getSource()).getText();
            changeJourneyStart(addr);
            // clear vbox after clicking button
            matchAddr.getChildren().clear();
            // Close the scroll pane
            addrScroll.setVisible(false);
        };

        // Fill buttons on VBox
        for (String addr : results) {
            Button newButton = new Button(addr);
            newButton.setOnAction(event);
            buttonList.add(newButton);
        }

        matchAddr.getChildren().clear(); //remove all Buttons that are currently in the container
        matchAddr.getChildren().addAll(buttonList); //then add all Buttons just created

        // Once buttons are populated, show list
        addrScroll.setVisible(true);
    }


    /**
     * initialises the create journey controller, and scroll pane for autocomplete.

     * @param mainController Main Controller to be inserted into
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.mapViewController = mainController.getMapViewController();
        this.journeyDAO = new JourneyDAO();
        this.stationDAO = new StationDAO();
        this.vehicleDAO = new VehicleDAO();

//        // disable scroll pane at start
//        startAddrScroll.setVisible(false);
//        endAddrScroll.setVisible(false);

//        // Set up event listeners for text areas
//        startAddr.setOnKeyPressed(keyEvent -> {
//            if (keyEvent.getCode() == KeyCode.ENTER) {
//                autoComplete(true);
//            } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
//                startAddrScroll.setVisible(false);
//            }
//        });
//
//        endAddr.setOnKeyPressed(keyEvent -> {
//            if (keyEvent.getCode() == KeyCode.ENTER) {
//                utoComplete(false);
//            } else if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
//                endAddrScroll.setVisible(false);
//            }
//        });

        populateVehicleDropdown();
    }
}
