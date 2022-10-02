package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import journey.business.searchAutocomplete;
import journey.data.Journey;
import journey.data.QueryResult;
import journey.data.Utils;
import journey.data.Vehicle;
import journey.repository.JourneyDAO;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
import java.util.ArrayList;

/**
 * Class to handle creating a journey given a start, end and chargers along the way
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

    private MainController mainController;
    private MapController mapViewController;
    private JourneyDAO journeyDAO;
    private StationDAO stationDAO;
    private VehicleDAO vehicleDAO;
    private final ArrayList<Integer> journeyStations = new ArrayList<>();

    public void updateSelectedStation(int selectedStation) {
        selectedStationField.setText(stationDAO.queryStation(selectedStation).getAddress());
    }

    /**
     * Provides all the users vehicles to choose from for a journey

     */
    public void populateVehicleDropdown() {
        QueryResult data = vehicleDAO.getVehicles(mainController.getCurrentUser());
        ObservableList<String> vehicles = FXCollections.observableArrayList();
        for (Vehicle vehicle : data.getVehicles()) {
            String newString = vehicle.getStringRepresentation();
            vehicles.add(newString);
        }
        selectVehicleComboBox.setItems(vehicles);
    }

    /**
     * Add station to a given journey

     * @param event "add" button pressed event
     */
    @FXML private void selectStation(Event event) {
        int selectedStation = mainController.getSelectedStation();
        if(selectedStation != -1 && (journeyStations.isEmpty() || journeyStations.get(journeyStations.size()-1) != selectedStation)) {
            journeyStations.add(selectedStation);
            ObservableList<String> visitedStations = visitedStationsList.getItems();
            visitedStations.add(stationDAO.queryStation(selectedStation).getAddress());
            visitedStationsList.setItems(visitedStations);
        }
        event.consume();
    }

    /**
     * Ensures all fields are filled and valid then adds the filed journey

     * @param event addJourney button pressed
     */
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

    /**
    * Function to control the autocomplete for the start search box
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
        searchAutocomplete search = new searchAutocomplete();
        ArrayList<String> results = search.getMatchingAddresses(address);

        // Event handler for each button - should update the text in the field
        EventHandler<ActionEvent> event = actionEvent -> {
            String addr = ((Button)actionEvent.getSource()).getText();
            changeJourneyStart(addr);
            // clear vbox after clicking button
            matchAddr.getChildren().clear();
            // Close the scroll pane
            addrScroll.setVisible(false);
        };

        // Fill buttons on VBox
        for(String addr : results) {
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
     * initialises the create journey controller, and scroll pane for autocomplete

     * @param stage current stage
     * @param mainController Main Controller to be inserted into
     */
    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        this.mapViewController = mainController.getMapViewController();
        this.journeyDAO = new JourneyDAO();
        this.stationDAO = new StationDAO();
        this.vehicleDAO = new VehicleDAO();

        // disable scroll pane at start
        startAddrScroll.setVisible(false);
        endAddrScroll.setVisible(false);

        // Set up event listeners for text areas
        startAddr.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER)
            {
                autoComplete(true);
            } else if(keyEvent.getCode() == KeyCode.BACK_SPACE){
                startAddrScroll.setVisible(false);
            }
        });

        endAddr.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER)
            {
                autoComplete(false);
            } else if(keyEvent.getCode() == KeyCode.BACK_SPACE){
                endAddrScroll.setVisible(false);
            }
        });

        populateVehicleDropdown();
    }
}
