package journey.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import journey.Utils;
import journey.data.QueryStation;
import journey.data.Vehicle;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
import journey.service.StationsService;

import java.io.BufferedInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controller for search FXML allows searching and error checking on searches for stations.
 */
public class SearchController {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> operatorSearch;
    @FXML private ChoiceBox<String> currentSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private Label warningLabel;
    @FXML private MenuButton connectorsMenu;
    @FXML private Button placeMarkerButton;
    @FXML private ImageView placeMarkerImage;
    @FXML private ImageView questionImage;
    @FXML private Button removeMarkerButton;
    @FXML private CheckBox favouritedCheckMark;
    @FXML private ImageView rangeHelpImage;
    @FXML private Label rangeHelpLabel;
    private final ArrayList<CustomMenuItem> connectors = new ArrayList<>();
    private ArrayList<String> connectorsList = new ArrayList<>();
    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList("", "Mixed", "AC", "DC");
    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList("", "Yes", "No");
    private MainController mainController;
    private StationDAO stationDAO;
    private String addressLatLng = "";
    private StationsService stationsService;

    /**
     * Gets users current vehicle from the database
     * and inputs its current type and charger type into the search.
     */
    @FXML private void filterByCurrVehicle() {
        ArrayList<String> selectedConnectors = new ArrayList<>();
        String myCar = mainController.getSelectedVehicle();
        Vehicle v = vehicleDAO.queryVehicle(myCar, mainController.getCurrentUser().getId());
        if (v != null) {
            currentSearch.setValue(v.getChargerType());
            for (CustomMenuItem customMenuItem : connectors) {
                CheckBox connector = (CheckBox) customMenuItem.getContent();
                if (!connector.getText().equals(v.getConnectorType())) {
                    connector.setSelected(false);
                } else {
                    connector.setSelected(true);
                    selectedConnectors.add(connector.getText());
                }
            }
            connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
            connectorsList = selectedConnectors;
        } else {
            warningLabel.setText("You have not got any vehicle selected!");
        }
    }

    /**
     * Ensures multi-select for the connectors list behaves correctly.
     * (No duplicate values are entered into arrayList).
     */
    @FXML private void connectorsMultiSelect() {
        ArrayList<String> selectedConnectors = new ArrayList<>();

        for (CustomMenuItem customMenuItem : connectors) {
            CheckBox connector = (CheckBox) customMenuItem.getContent();
            connector.selectedProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (Boolean.TRUE.equals(newValue)) {
                    selectedConnectors.add(connector.getText());
                    connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
                } else {
                    selectedConnectors.remove(connector.getText());
                    connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
                }
                search();
            }));
        }
        connectorsList = selectedConnectors;
    }

    /**
     * Get a connectors list for querying the database.

     * @return connectors to find from database.
     */
    private String[] getConnectorsAsStrArr() {
        String[] connectorsToFind = new String[connectorsList.size()];
        for (int i = 0; i < connectorsList.size(); i++) {
            connectorsToFind[i] = connectorsList.get(i);
        }
        return connectorsToFind;
    }

    /**
     * Called on update of range field in FXML.
     * Used to update the range circle.
     */
    private void updateRange() {
        if (!addressLatLng.isEmpty()) {
            String[] latLng = addressLatLng.split("#");
            double lat = Double.parseDouble(latLng[0]);
            double lng = Double.parseDouble(latLng[1]);

            removeRangeIndicator();
            addRangeIndicator(lat, lng);

            search();
        }

    }

    /**
     * Adds a circle at the lat and lng to indicate the range of the search.
     */
    public void addRangeIndicator(double lat, double lng) {
        String range = distanceSearch.getText();
        if (!range.isBlank() && Utils.isInt(range) && !(Integer.parseInt(range) < 0
                || Integer.parseInt(range) > 1600)) {
            int radius = Integer.parseInt(range);
            mainController.getMapViewController().addRangeIndicator(lat, lng, radius);
        }
    }

    /**
     * Removes a range indicator from the map.
     */
    private void removeRangeIndicator() {
        mainController.getMapViewController().removeRangeIndicator();
    }

    /**
     * Searches for relevant stations based on users search inputs.
     */
    @FXML public void search() {
        String name = nameSearch.getText();
        String timeLimit = timeSearch.getText();
        String range = distanceSearch.getText();
        String errors = StationsService.errorCheck(addressLatLng, name, timeLimit, range);
        boolean favourited = favouritedCheckMark.isSelected();


        if (errors.isBlank()) {
            warningLabel.setText("");
            QueryStation queryStation = StationsService.createQueryStation(nameSearch.getText(),
                    operatorSearch.getValue(),
                    currentSearch.getValue(),
                    getConnectorsAsStrArr(),
                    attractionSearch.getValue(),
                    timeLimit,
                    addressLatLng,
                    range,
                    favourited);
            mainController.setCurrentStations(stationsService.filterBy(queryStation));
        } else {
            warningLabel.setText(errors);
            mainController.setCurrentStations(stationDAO.getAll(mainController.getCurrentUser()));
        }

    }

    /**
     * Clears the search.
     */
    @FXML private void clearSearch() {
        addressSearch.setText("");
        nameSearch.setText("");
        operatorSearch.setValue("");
        currentSearch.setValue("");
        timeSearch.setText("");
        attractionSearch.setValue("");
        distanceSearch.setText("50");
        for (CustomMenuItem connector : connectors) {
            ((CheckBox) connector.getContent()).setSelected(false);
        }
        connectorsMenu.setText("");
        warningLabel.setText("");

        removeRangeMarker();

        mainController.clearSearch();
    }

    /**
     * Gets the coordinates of the next click on the map. A callback
     * function is passed in, so when the map is clicked the searching
     * lat and long is updated.
     */
    @FXML private void clickSearch() {
        mainController.openMap();
        mainController.getMapViewController().setCallback((lat, lng) -> {
            String addr = Utils.latLngToAddr(lat, lng);
            addressSearch.setText(addr);
            return true;
        }, "search");
    }

    /**
     * Function to place a marker on the map when the "Place marker" button selected on the search.
     */
    @FXML private void clickToPlaceMarker() {
        mainController.openMap();
        mainController.getMapViewController().setCallback((lat, lng) -> {
            addRangeIndicator(lat, lng);
            addressLatLng = lat + "#" + lng;
            search();
            removeMarkerButton.setDisable(false);
            return true;
        }, "search");

    }


    /**
     * Called to remove the range marker from the map.
     * Only called if a range marker is on the map.
     */
    @FXML private void removeRangeMarker() {

        addressLatLng = "";
        mainController.clearSearchMarkerFromMap();
        removeMarkerButton.setDisable(true);
        removeRangeIndicator();
        search();
    }

    /**
     * Added to the tooltip so that the user can click or hover to display.
     */
    @FXML private void clickToolTip() {
        final Tooltip customTooltip = new Tooltip(
                "Click the map to place a marker after clicking on the green marker button.");
        rangeHelpLabel.setTooltip(customTooltip);
        customTooltip.setAutoHide(true);

    }

    /**
     * Changes the lat and long in the search controller.

     * @param lat the latitude.
     * @param lng the longitude.
     */
    public void changeSearchLatLong(double lat, double lng) {
        addressLatLng = lat + "#" + lng;
    }

    /**
     * Add listeners to the search fields.
     * Note: the functions are not abstracted out as they share a common pause timer.
     */
    private void addListeners() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.1));

        addressSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> search());
            pause.playFromStart();
        });

        nameSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> search());
            pause.playFromStart();
        });

        currentSearch.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> search()));
        operatorSearch.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> search()));

        // the listeners for connectors are set in connectorsMultiSelect

        timeSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> search());
            pause.playFromStart();
        });

        attractionSearch.getSelectionModel().selectedItemProperty().addListener(
                ((observable, oldValue, newValue) -> search()));

        distanceSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> updateRange());
            pause.playFromStart();
        });

        favouritedCheckMark.selectedProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> search());
            pause.playFromStart();
        });
    }

    /**
     * Initialises choice boxes and their options.
     */
    private void choiceBoxInit() {
        currentSearch.setItems(chargerTypeSearchOptions);
        currentSearch.setValue("");

        ObservableList<String> operators = (stationDAO.getAllOperators());
        operatorSearch.setItems(operators);

        attractionSearch.setItems(yesNoMaybeSo);
    }
    /**
     * Insert images into search fxml.
     */
    private void insertImages() {
        Image img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/question.png"))
                ));
        (questionImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/green-marker.png"))
                ));
        (placeMarkerImage).setImage(img);
    }

    /**
     * Initialises the search pane.

     * @param mainController the main controller.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.stationsService = mainController.getStationsService();

        stationDAO = new StationDAO();
        choiceBoxInit();
        insertImages();
        rangeHelpLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        rangeHelpLabel.setGraphic(questionImage);

        Tooltip rangeHelpTooltip = new Tooltip(
                "Click the map to place a marker after clicking on the green marker button.");
        rangeHelpTooltip.setShowDelay(new Duration(0.0));
        rangeHelpLabel.setTooltip(rangeHelpTooltip);


        List<String> connectorsAvailable = new ArrayList<>();
        connectorsAvailable.add("Type 2 Socketed");
        connectorsAvailable.add("CHAdeMO");
        connectorsAvailable.add("Type 2 Tethered");
        connectorsAvailable.add("Type 2 CCS");
        connectorsAvailable.add("Type 1 Tethered");

        for (String connector : connectorsAvailable) {
            CheckBox checkBox = new CheckBox(connector);
            CustomMenuItem checkBoxItem = new CustomMenuItem(checkBox);
            checkBoxItem.setHideOnClick(false);
            checkBox.setPrefWidth(215);
            connectors.add(checkBoxItem);
        }
        connectorsMenu.getItems().addAll(connectors);
        connectorsMultiSelect();


        placeMarkerButton.setGraphic(placeMarkerImage);
        placeMarkerButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        addListeners();
    }
}
