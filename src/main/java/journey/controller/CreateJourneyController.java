package journey.controller;

import java.io.BufferedInputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import journey.Utils;
import journey.data.Vehicle;
import journey.repository.JourneyDAO;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;

import javafx.scene.image.Image;

import javax.swing.text.IconView;

/**
 * Class to handle creating a journey given a start, end and chargers along the way.
 */
public class CreateJourneyController {
    @FXML private TextField address0;
    @FXML private TextField address1;
    @FXML private TextField selectedStationField;

    @FXML private ListView<String> visitedStationsList;
    @FXML private ComboBox<String> selectVehicleComboBox;
    @FXML private Label journeyWarningLabel;
    @FXML private VBox matchAddrStart;
    @FXML private VBox matchAddrEnd;
    @FXML private ScrollPane journeyScrollPane;
    @FXML private AnchorPane journeyPane;
    @FXML private AnchorPane row1;
    @FXML private AnchorPane row2;
    @FXML private ImageView destination;
    @FXML private Pane iconPane;
    @FXML private ImageView firstCircle;
    @FXML private ImageView firstEllipses;
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
    private final ArrayList<TextField> waypointAddresses = new ArrayList<>();
    private final ArrayList<AnchorPane> waypointRows = new ArrayList<>();
    private final ArrayList<ImageView> circleIcons = new ArrayList<>();
    private final ArrayList<ImageView> ellipsesIcons = new ArrayList<>();
    // Search after 0.5 seconds
    PauseTransition pause = new PauseTransition(Duration.seconds(0.5));

    private Image ellipses;
    private Image circle;
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

    public void insertWaypoint(double lat, double lng, int position) {
        // Add one more row and move the addresses over
        journeyPane.getChildren().add(nthWaypoint(waypoints.size()));

        for (int i = waypoints.size(); i > position; i--) {
            waypointAddresses.get(i).setText(waypointAddresses.get(i - 1).getText());
        }

        // Insert
        waypoints.add(position, lat + "#" + lng);
        updateJourneyList(position);
    }

    /**
     * To be called from the map, similar to addWaypointToJourney except the
     * journey is not redrawn.

     * @param lat lat of waypoint
     * @param lng long of waypoint
     * @param position number in route
     */
    public void editWaypoint(Double lat, Double lng, int position) {
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

    private void updateJourneyList(int position) {
        String waypoint = waypoints.get(position);
        String[] latLng = waypoint.split("#");
        String address = Utils.latLngToAddr(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
        waypointAddresses.get(position).setText(address);
    }

    private void deleteIcon() {
        iconPane.getChildren().remove(circleIcons.remove(0));
        iconPane.getChildren().remove(ellipsesIcons.remove(0));
        for (ImageView imageView : circleIcons) {
            imageView.setLayoutY(imageView.getLayoutY() - 60);
        }
        for (ImageView imageView : ellipsesIcons) {
            imageView.setLayoutY(imageView.getLayoutY() - 60);
        }
        destination.setLayoutY(destination.getLayoutY() - 60);
    }

    private void addIcon(int i) {
        destination.setLayoutY(destination.getLayoutY() + 60);
        ImageView circleView = new ImageView();
        circleView.setFitHeight(17);
        circleView.setFitWidth(17);
        circleView.setLayoutX(8);
        circleView.setLayoutY(19d + 60 * (i - 1));
        circleView.setPickOnBounds(true);
        circleView.setPreserveRatio(true);
        circleView.setImage(circle);

        ImageView ellipsesView = new ImageView();
        ellipsesView.setFitHeight(32);
        ellipsesView.setFitWidth(32);
        ellipsesView.setLayoutX(0);
        ellipsesView.setLayoutY(40d + 60 * (i - 1));
        ellipsesView.setPickOnBounds(true);
        ellipsesView.setPreserveRatio(true);
        ellipsesView.setImage(ellipses);

        circleIcons.add(circleView);
        ellipsesIcons.add(ellipsesView);
        iconPane.getChildren().add(circleView);
        iconPane.getChildren().add(ellipsesView);
    }

    private AnchorPane nthWaypoint(int i) {
        addIcon(i);
        AnchorPane stationRow = new AnchorPane();
        HBox row = new HBox();
        TextField address = new TextField();
        Button removeWaypoint = new Button();

        stationRow.setId("row" + i);
        stationRow.setPrefHeight(32);
        stationRow.setPrefWidth(270);
        stationRow.setLayoutY(10d + 60 * i);
        AnchorPane.setRightAnchor(stationRow, 10d);
        AnchorPane.setLeftAnchor(stationRow, 0d);

        row.getChildren().add(address);
        row.getChildren().add(removeWaypoint);
        stationRow.getChildren().add(row);

        AnchorPane.setLeftAnchor(row, 0d);
        AnchorPane.setRightAnchor(row, 0d);
        AnchorPane.setTopAnchor(row, 0d);
        AnchorPane.setBottomAnchor(row, 0d);

        address.setPrefHeight(32);
        address.setPrefWidth(138);
        address.getStylesheets().add(textCss);
        address.setPromptText("Click map or type address");
        address.setId("address" + i);
        address.setOnMouseClicked(this::clickNth);
        waypointAddresses.add(address);
        AnchorPane.setRightAnchor(address, 0d);
        AnchorPane.setTopAnchor(address, 0d);
        HBox.setHgrow(address, Priority.ALWAYS);
        HBox.setMargin(address, new Insets(0, 10, 0, 0));
        address.addEventFilter(KeyEvent.KEY_PRESSED, keyEvent -> {
            if ((int) keyEvent.getCharacter().charAt(0) == 13) {
                typeNth(i);
            }
        });

        removeWaypoint.setPrefHeight(32);
        removeWaypoint.setPrefWidth(32);
        removeWaypoint.setStyle("-fx-background-color: #FFFFFF;");
        removeWaypoint.setText("X");
        removeWaypoint.setSnapToPixel(true);
        removeWaypoint.setEffect(dropShadow);
        removeWaypoint.setUserData(String.valueOf(i));
        removeWaypoint.setOnAction(this::removeNth);
        AnchorPane.setRightAnchor(removeWaypoint, 0d);
        AnchorPane.setTopAnchor(removeWaypoint, 0d);
        AnchorPane.setBottomAnchor(removeWaypoint, 0d);
        HBox.setHgrow(removeWaypoint, Priority.NEVER);

        waypointRows.add(stationRow);
        return stationRow;
    }

    @FXML private void clickNth(Event event) {
        //Gets the position of the station in the route from its id
        int i = Integer.parseInt(((Node) event.getSource()).getId().substring(7));
        mapViewController.setCallback((lat, lng) -> {
            addWaypointToJourney(lat, lng, i);
            return true;
        }, String.valueOf(i));
    }

    private void typeNth(int i) {
        String address = waypointAddresses.get(i).getText();
        pause.setOnFinished(event -> {
            String latLngString = Utils.locToLatLng(address);

            if (!latLngString.equals("0.0#0.0")) {
                String[] latLng = latLngString.split("#");
                mainController.addMiscMarkerToMap(Double.parseDouble(latLng[0]),
                        Double.parseDouble(latLng[1]), String.valueOf(i));
                addWaypointToJourney(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]), i);
            }
        });
        pause.playFromStart();
    }

    @FXML private void removeNth(ActionEvent event) {
        int index = Integer.parseInt((String) ((Node) event.getSource()).getUserData());

        if (waypoints.size() <= 2) {
            waypoints.set(index, "");
            waypointAddresses.get(index).setText("");
            mainController.clearWaypoint(index);
            mainController.clearRoute();

            // Add markers back to map
            int i = 0;
            for (String waypoint : waypoints) {
                if (!waypoint.isBlank()) {
                    String[] latLng = waypoint.split("#");
                    mainController.addMiscMarkerToMap(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]), String.valueOf(i));
                }
                i++;
            }
            return;
        }

        waypoints.remove(index);

        // Shuffle addresses back down
        for (int i = index; i < waypoints.size(); i++) {
            waypointAddresses.get(i).setText(waypointAddresses.get(i + 1).getText());
        }

        waypointAddresses.remove(waypointAddresses.size() - 1);
        journeyPane.getChildren().remove(waypointRows.get(waypointRows.size() - 1));
        waypointRows.remove(waypointRows.size() - 1);
        deleteIcon();
        updateJourney();
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
        waypointAddresses.add(address0);
        waypointAddresses.add(address1);

        address0.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            // The keyCode is UNDEFINED and comparing strings doesn't work
            if ((int) keyEvent.getCharacter().charAt(0) == 13) {
                typeNth(0);
                keyEvent.consume();
            }
        });
        address1.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if ((int) keyEvent.getCharacter().charAt(0) == 13) {
                typeNth(1);
            }
        });

        waypointRows.add(row1);
        waypointRows.add(row2);

        circleIcons.add(firstCircle);
        ellipsesIcons.add(firstEllipses);

        circle = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/Circle.png"))
                )
        );

        ellipses = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/dots.png"))
                )
        );

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
