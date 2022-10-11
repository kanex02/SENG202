package journey.controller;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.StringJoiner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import journey.Utils;
import journey.business.SearchAutocomplete;
import journey.data.Journey;
import journey.data.Station;
import journey.data.Vehicle;
import journey.repository.JourneyDAO;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
import journey.service.CreateJourneyService;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class to handle creating a journey given a start, end and chargers along the way.
 */
public class CreateJourneyController {
    @FXML private TextField a;
    @FXML private TextField b;
    @FXML private TextField selectedStationField;
    @FXML private ListView<String> visitedStationsList;
    @FXML private ComboBox<String> selectVehicleComboBox;
    @FXML private Label journeyWarningLabel;
    @FXML private VBox matchAddrStart;
    @FXML private VBox matchAddrEnd;
    @FXML private ScrollPane journeyScrollPane;
    @FXML private AnchorPane journeyPane;
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
    private ArrayList<TextField> waypointAddresses;
    private final ArrayList<Integer> journeyStations = new ArrayList<>();
    private final Image ellipses = new Image(new File("/images/dots.png").toURI().toString());
    private final Image circle = new Image(new File("/images/Circle.png").toURI().toString());
    private final String textCss = (new File(Objects.requireNonNull(
            getClass().getClassLoader().getResource("gui/textFields.css"))
            .getFile()))
            .toURI().toString();
    private final DropShadow dropShadow = new DropShadow() {{
                setBlurType(BlurType.ONE_PASS_BOX);
                setOffsetY(4);
                setColor(new Color(0.23, 0.23, 0.23, 0.25));
    }};

    public void updateSelectedStation(int selectedStation) {
        selectedStationField.setText(stationDAO.queryStation(selectedStation).getAddress());
    }

    /**
     * To be called from the map, similar to addWaypointToJourney except the
     * journey is not redrawn.

     * @param lat lat of waypoint
     * @param lng long of waypoint
     * @param position number in route
     */
    public void addRouteWaypoint(Double lat, Double lng, int position) {
        if (position >= 2 && position >= waypoints.size()) {
            journeyPane.getChildren().add(nthWaypoint(position));
        }

        for (int i = waypoints.size(); i <= position; i++) {
            waypoints.add("");
        }

        waypoints.set(position, lat + "#" + lng);

        updateJourneyList(position);
    }

    private void addWaypointToJourney(Double lat, Double lng, int position) {
        if (position >= 2 && position >= waypoints.size()) {
            journeyPane.getChildren().add(nthWaypoint(position));
        }

        for (int i = waypoints.size(); i <= position; i++) {
            waypoints.add("");
        }

        waypoints.set(position, lat + "#" + lng);
        updateJourney();
        updateJourneyList(position);
    }

    public void appendWaypoint(double lat, double lng) {
        addWaypointToJourney(lat, lng, waypoints.size());
    }

    private void updateJourneyList(int position) {
        String waypoint = waypoints.get(position);
        String[] latLng = waypoint.split("#");
        String address = Utils.latLngToAddr(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
        waypointAddresses.get(position).setText(address);
    }

    private AnchorPane nthWaypoint(int i) {
        AnchorPane stationRow = new AnchorPane();
        HBox row = new HBox();
        TextField address = new TextField();
        Button removeWaypoint = new Button();

        row.getChildren().add(address);
        row.getChildren().add(removeWaypoint);
        stationRow.getChildren().add(row);

        stationRow.setPrefHeight(32);
        stationRow.setPrefWidth(270);
        stationRow.setLayoutY(10d + 60 * i);
        AnchorPane.setRightAnchor(stationRow, 10d);
        AnchorPane.setLeftAnchor(stationRow, 0d);

        AnchorPane.setLeftAnchor(row, 0d);
        AnchorPane.setRightAnchor(row, 0d);
        AnchorPane.setTopAnchor(row, 0d);
        AnchorPane.setBottomAnchor(row, 0d);

        address.setPrefHeight(32);
        address.setPrefWidth(138);
        address.getStylesheets().add(textCss);
        address.setPromptText("Click map or type address");
        address.setId(Character.toString('a' + i));
        address.setOnMouseClicked(this::clickNth);
        waypointAddresses.add(address);
        AnchorPane.setRightAnchor(address, 0d);
        AnchorPane.setTopAnchor(address, 0d);
        HBox.setHgrow(address, Priority.ALWAYS);
        HBox.setMargin(address, new Insets(0, 10, 0, 0));

        removeWaypoint.setPrefHeight(32);
        removeWaypoint.setPrefWidth(32);
        removeWaypoint.setStyle("-fx-background-color: #FFFFFF;");
        removeWaypoint.setText("X");
        removeWaypoint.setSnapToPixel(true);
        removeWaypoint.setEffect(dropShadow);
        AnchorPane.setRightAnchor(removeWaypoint, 0d);
        AnchorPane.setTopAnchor(removeWaypoint, 0d);
        AnchorPane.setBottomAnchor(removeWaypoint, 0d);
        HBox.setHgrow(removeWaypoint, Priority.NEVER);

        return stationRow;
    }

    @FXML private void clickNth(Event event) {
        System.out.println("clicked");
        //Gets the position of the station in the route from its id
        int i = (((Node) event.getSource()).getId()).toCharArray()[0] - 'a';
        mapViewController.setCallback((lat, lng) -> {
            System.out.println(i);
            addWaypointToJourney(lat, lng, i);
            System.out.println("DONE?");
            return true;
        }, String.valueOf(i));
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
//        String vehicleChoice = selectVehicleComboBox.getValue();
//        String start = startAddr.getText();
//        String end = endAddr.getText();
//        int userID = mainController.getCurrentUser().getId();
//
//        // Check if the inputs are valid
//        Boolean[] valid = CreateJourneyService.checkJourney(vehicleChoice, start, end);
//
//        StringJoiner errors = new StringJoiner("\n");
//
//        if (!valid[0]) {
//            errors.add("Please select a vehicle");
//        }
//
//        if (!valid[1]) {
//            errors.add("Start location invalid");
//        }
//
//        if (!valid[2]) {
//            errors.add("End location invalid");
//        }
//
//        journeyWarningLabel.setText(errors.toString());
//
//        boolean validJourney = true;
//        for (Boolean bool : valid) {
//            if (!bool) {
//                validJourney = false;
//                break;
//            }
//        }
//
//        if (validJourney) {
//            journeyWarningLabel.setText("");
//            selectVehicleComboBox.setValue("");
//            startAddr.setText("");
//            endAddr.setText("");
//            selectedStationField.setText("");
//            visitedStationsList.setItems(FXCollections.observableArrayList());
//            String[] vehicle = vehicleChoice.split(": ");
//            String date = Utils.getDate();
//            Journey journey = new Journey(start, end, vehicle[0], userID, date, journeyStations);
//            journeyDAO.addJourney(journey);
//            mapViewController.clearJourneyMarkers();
//            mainController.updatePlannedJourneys();
//            event.consume();
//        }
    }

    private void updateJourney() {
        if (waypoints.stream().filter(x -> !x.isBlank()).count() >= 2) {
            mainController.clearWaypoints();
            mainController.mapJourneyFromLatLng(waypoints.toArray(new String[0]));
        }
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

        waypoints = new ArrayList<>();
        waypointAddresses = new ArrayList<TextField>();
        waypointAddresses.add(a);
        waypointAddresses.add(b);

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
