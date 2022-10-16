package journey.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import journey.Utils;
import journey.data.Journey;
import journey.data.Vehicle;
import journey.repository.JourneyDAO;
import journey.repository.VehicleDAO;
import journey.service.CreateJourneyService;

import java.io.BufferedInputStream;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Class to handle creating a journey given a start, end and chargers along the way.
 */
public class CreateJourneyController {
    @FXML private TextField address0;
    @FXML private TextField address1;
    @FXML private ComboBox<String> selectVehicleComboBox;
    @FXML private AnchorPane journeyPane;
    @FXML private AnchorPane row1;
    @FXML private AnchorPane row2;
    @FXML private ImageView destination;
    @FXML private Pane iconPane;
    @FXML private ImageView firstCircle;
    @FXML private ImageView firstEllipses;
    @FXML private Label journeyWarningLabel;

    private MainController mainController;
    private JourneyDAO journeyDAO;
    private VehicleDAO vehicleDAO;
    private final ArrayList<String> waypoints = new ArrayList<>();
    private final ArrayList<TextField> waypointAddresses = new ArrayList<>();
    private final ArrayList<AnchorPane> waypointRows = new ArrayList<>();
    private final ArrayList<ImageView> circleIcons = new ArrayList<>();
    private final ArrayList<ImageView> ellipsesIcons = new ArrayList<>();
    // Search after 0.5 seconds
    private final PauseTransition pause = new PauseTransition(Duration.seconds(0.5));
    private Image ellipses;
    private Image circle;
    private Image closeImage;
    private static final String TEXT_FILL_RED = "-fx-text-fill: red";
    private final String textCss = (new File(Objects.requireNonNull(
            getClass().getClassLoader().getResource("gui/textFields.css"))
            .getFile()))
            .toURI().toString();
    private static final DropShadow DROPSHADOW = new DropShadow();

    static {
            DROPSHADOW.setBlurType(BlurType.ONE_PASS_BOX);
            DROPSHADOW.setOffsetY(4);
            DROPSHADOW.setColor(new Color(0.23, 0.23, 0.23, 0.25));
        }

    /**
     * Adds a new waypoint at the end of the current list.

     * @param lat latitude
     * @param lng longitude
     */
    public void addNewWaypoint(double lat, double lng) {
        int position = (int) waypoints.stream().filter(waypoint -> !waypoint.isBlank()).count();

        // make sure a waypoint isn't added twice in a row
        if (position == 0 || !waypoints.get(position - 1).equals(lat + "#" + lng)) {
            // inserts a waypoint at the end of the list
            addWaypointToJourney(lat, lng, position);
        }
    }

    /**
     * Inserts a waypoint into the route.

     * @param lat latitude.
     * @param lng longitude.
     * @param position position within route.
     */
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

    /**
     * Adds a waypoint to a journey given the lat and lng and the position of the stop
     * @param lat The latitude of the waypoint
     * @param lng The longitude of the waypoint
     * @param position The position in the journey
     */
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

    /**
     * Updates the journey list by retrieving the waypoint at the given position and
     * updating the test for the waypoint addresses
     * @param position The position of the waypoint to update in the journey
     */
    private void updateJourneyList(int position) {
        String waypoint = waypoints.get(position);
        String[] latLng = waypoint.split("#");
        String address = Utils.latLngToAddr(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]));
        waypointAddresses.get(position).setText(address);
    }

    /**
     * Helper function that deletes an icon
     */
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

    /**
     * Helper function that adds an icon to the plan a journey panel.
     * @param i index of the icon to add
     */
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

    /**
     * Produces an anchor pane at the index i and reshuffles the rest.
     * @param i the index of the anchor pane.
     * @return anchor pane
     */
    private AnchorPane nthWaypoint(int i) {
        addIcon(i);
        ImageView cancel1 = new ImageView(closeImage);
        cancel1.setFitHeight(24);
        cancel1.setFitWidth(24);
        cancel1.setPreserveRatio(true);


        AnchorPane stationRow = new AnchorPane();
        stationRow.setId("row" + i);
        stationRow.setPrefHeight(32);
        stationRow.setPrefWidth(270);
        stationRow.setLayoutY(10d + 60 * i);
        AnchorPane.setRightAnchor(stationRow, 10d);
        AnchorPane.setLeftAnchor(stationRow, 0d);

        TextField address = new TextField();
        HBox row = new HBox();
        Button removeWaypoint = new Button();
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
            if (keyEvent.getCharacter().charAt(0) == 13) {
                pause.setOnFinished(event -> typeNth(i));
                pause.playFromStart();
            }
        });
        // Search on focus lost
        address.focusedProperty().addListener((observableValue, oldBool, newBool) -> {
            if (Boolean.FALSE.equals(newBool)) {
                typeNth(i);
            }
        });

        removeWaypoint.setPrefHeight(32);
        removeWaypoint.setPrefWidth(32);
        removeWaypoint.setStyle("-fx-background-color: #FFFFFF;");
        removeWaypoint.setGraphic(cancel1);
        removeWaypoint.setSnapToPixel(true);
        removeWaypoint.setEffect(DROPSHADOW);
        removeWaypoint.setUserData(String.valueOf(i));
        removeWaypoint.setOnAction(this::removeNth);
        AnchorPane.setRightAnchor(removeWaypoint, 0d);
        AnchorPane.setTopAnchor(removeWaypoint, 0d);
        AnchorPane.setBottomAnchor(removeWaypoint, 0d);
        HBox.setHgrow(removeWaypoint, Priority.NEVER);

        waypointRows.add(stationRow);
        return stationRow;
    }

    /**
     * Gets the position of the station in the route when clicked from its id and sets the callback function.
     * @param event JavaFX event
     */
    @FXML private void clickNth(Event event) {
        journeyWarningLabel.setStyle(TEXT_FILL_RED);
        journeyWarningLabel.setText("");
        //Gets the position of the station in the route from its id
        int i = Integer.parseInt(((Node) event.getSource()).getId().substring(7));
        mainController.getMapViewController().setCallback((lat, lng) -> {
            addWaypointToJourney(lat, lng, i);
            return true;
        }, String.valueOf(i));
    }

    /**
     * Called as callback when there is a change in one of the journey test boxes.
     * Runs all necessary operations to get the address.
     * @param i position of the marker in the journey.
     */
    private void typeNth(int i) {
        journeyWarningLabel.setStyle(TEXT_FILL_RED);
        journeyWarningLabel.setText("");
        String address = waypointAddresses.get(i).getText();

        if (!address.isBlank()) {
            mainController.getMapViewController().setCallback(null, null);
            String latLngString = Utils.locToLatLng(address);

            if (!latLngString.equals("0.0#0.0")) {
                String[] latLng = latLngString.split("#");
                mainController.addMiscMarkerToMap(Double.parseDouble(latLng[0]),
                        Double.parseDouble(latLng[1]), String.valueOf(i));
                addWaypointToJourney(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]), i);
            }
        }
    }

    /**
     * Wrapper function to call removeNth(int index).
     * Used to get the index.
     * @param event JavaFX event
     */
    @FXML private void removeNth(ActionEvent event) {
        int index = Integer.parseInt((String) ((Node) event.getSource()).getUserData());
        removeNth(index);
    }

    /**
     * Removes the nth waypoint from the journey.
     * @param index index of the waypoint
     */
    private void removeNth(int index) {
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
                    mainController.addMiscMarkerToMap(Double.parseDouble(latLng[0]),
                            Double.parseDouble(latLng[1]), String.valueOf(i));
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
    private void populateVehicleDropdown() {
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
     */
    @FXML private void addJourney() {
        journeyWarningLabel.setStyle(TEXT_FILL_RED);
        journeyWarningLabel.setText("");
        String warnings = CreateJourneyService.checkJourney(selectVehicleComboBox.getValue(),
                waypoints);
        if (warnings.isBlank()) {
            journeyWarningLabel.setStyle("-fx-text-fill: green");
            journeyWarningLabel.setText("Saved!");
            Journey journey = new Journey(selectVehicleComboBox.getValue().split(":")[0],
                    mainController.getCurrentUser().getId(), LocalDate.now().toString(), waypoints);
            journeyDAO.addJourney(journey);
            mainController.updatePlannedJourneys();
            // Remove all waypoints
            int size = waypoints.size() - 1;
            for (; size >= 0; size--) {
                removeNth(size);
            }
        } else {
            journeyWarningLabel.setText(warnings);
        }
    }

    /**
     * Maps the current waypoints.
     */
    public void updateJourney() {
        if (waypoints.stream().filter(x -> !x.isBlank()).count() >= 2) {
            mainController.clearWaypoints();
            mainController.mapJourneyFromLatLng(waypoints.toArray(new String[0]));
        }
    }

    /**
     * initialise all the images and insert close buttons into the buttons.
     */
    private void initImages() {
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
        closeImage = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/cancel.png"))
                )
        );

        ImageView cancel1 = new ImageView(closeImage);
        cancel1.setFitHeight(24);
        cancel1.setFitWidth(24);
        cancel1.setPreserveRatio(true);
        ImageView cancel2 = new ImageView(closeImage);
        cancel2.setFitHeight(24);
        cancel2.setFitWidth(24);
        cancel2.setPreserveRatio(true);
        ((Button) ((HBox) row1.getChildren().get(0)).getChildren().get(1)).setGraphic(cancel1);
        ((Button) ((HBox) row2.getChildren().get(0)).getChildren().get(1)).setGraphic(cancel2);
    }


    /**
     * initialises the CreateJourneyController, and scroll pane for autocomplete.

     * @param mainController Main Controller to be inserted into.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.journeyDAO = new JourneyDAO();
        this.vehicleDAO = new VehicleDAO();

        waypoints.add("");
        waypoints.add("");

        waypointAddresses.add(address0);
        waypointAddresses.add(address1);

        address0.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            // The keyCode is UNDEFINED and comparing strings doesn't work
            if (keyEvent.getCharacter().charAt(0) == 13) {
                pause.setOnFinished(event -> typeNth(0));
                pause.playFromStart();
            }
        });
        address0.focusedProperty().addListener((observableValue, oldBool, newBool) -> {
            if (Boolean.FALSE.equals(newBool)) {
                typeNth(0);
            }
        });
        address1.addEventFilter(KeyEvent.KEY_TYPED, keyEvent -> {
            if (keyEvent.getCharacter().charAt(0) == 13) {
                pause.setOnFinished(event -> typeNth(1));
                pause.playFromStart();
            }
        });
        address1.focusedProperty().addListener((observableValue, oldBool, newBool) -> {
            if (Boolean.FALSE.equals(newBool)) {
                typeNth(1);
            }
        });
        initImages();
        waypointRows.add(row1);
        waypointRows.add(row2);

        Vehicle vehicle = vehicleDAO.getSelectedVehicle(mainController.getCurrentUser());

        if (vehicle != null) {
            selectVehicleComboBox.getSelectionModel().select(
                    vehicle.getStringRepresentation()
            );
        }

        selectVehicleComboBox.setOnMouseClicked(mouseEvent -> {
            journeyWarningLabel.setStyle(TEXT_FILL_RED);
            journeyWarningLabel.setText("");
        });

        populateVehicleDropdown();
    }
}
