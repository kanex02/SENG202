package journey.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.ImageCursor;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for search FXML allows searching and error checking on searches for stations.
 */
public class SearchController {
    private final VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> currentSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private Label warningLabel;
    @FXML private MenuButton connectorsMenu;
    @FXML private Button placeMarkerButton;
    @FXML private ImageView placeMarkerImage;
    @FXML private Button removeMarkerButton;
    @FXML private CheckBox favouritedCheckMark;
    @FXML private ImageView rangeHelpImage;
    @FXML private Label rangeHelpLabel;
    final ArrayList<CheckMenuItem> connectors = new ArrayList<>();
    ArrayList<String> connectorsList = new ArrayList<>();

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
    @FXML public void myCar() {
        ArrayList<String> selectedConnectors = new ArrayList<>();
        String myCar = mainController.getSelectedVehicle();
        Vehicle v = vehicleDAO.queryVehicle(myCar, mainController.getCurrentUser().getId());
        if (v != null) {
            currentSearch.setValue(v.getChargerType());
            for (CheckMenuItem connector : connectors) {
                if (!connector.getText().equals(v.getConnectorType()) ) {
                    connector.setSelected(false);
                } else {
                    connector.setSelected(true);
                    selectedConnectors.add(connector.getText());
                }
            }
            connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
            connectorsList = selectedConnectors;
        }
    }

    /**
     * Ensures multi-select for the connectors list behaves correctly.
     * (No duplicate values are entered into arrayList).
     */
    @FXML public void connectorsMultiSelect() {
        ArrayList<String> selectedConnectors = new ArrayList<>();

        for (CheckMenuItem connector : connectors) {
            connector.selectedProperty().addListener(((observableValue, oldValue, newValue) -> {
                if (newValue) {
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
    public String[] getConnectors() {
        String[] connectorsToFind = new String[connectorsList.size()];
        for (int i = 0; i < connectorsList.size(); i++) {
            connectorsToFind[i] = connectorsList.get(i);
        }
        return connectorsToFind;
    }

    /**
     * Called on update of range field in FXML
     * Used to update the range circle
     */
    public void updateRange() {
        String[] latLng = addressLatLng.split("#");
        double lat = Double.parseDouble(latLng[0]);
        double lng = Double.parseDouble(latLng[1]);

        removeRangeIndicator();
        addRangeIndicator(lat, lng);

        search();

    }

    /**
     * Adds a circle at the lat and lng to indicate the range of the search
     */
    public void addRangeIndicator(double lat, double lng) {
        String range = distanceSearch.getText();
        if(!range.isBlank()) {
            int radius = Integer.parseInt(range);
            mainController.getMapViewController().addRangeIndicator(lat, lng, radius);
        }
    }

    public void removeRangeIndicator() {
        mainController.getMapViewController().removeRangeIndicator();
    }

    /**
     * Searches for relevant stations based on users search inputs.
     */
    @FXML public void search() {
        // TODO: move out of Util
        String name = nameSearch.getText();
        String operator = operatorSearch.getText();
        String timeLimit = timeSearch.getText();
        String range = distanceSearch.getText();
        String errors = StationsService.errorCheck(addressLatLng, name, operator, timeLimit, range);
        boolean favourited = favouritedCheckMark.isSelected();


        if (errors.isBlank()) {
            warningLabel.setText("");
            QueryStation queryStation = StationsService.createQueryStation(nameSearch.getText(),
                    operator,
                    currentSearch.getValue(),
                    getConnectors(),
                    attractionSearch.getValue(),
                    timeLimit,
                    addressLatLng,
                    range,
                    favourited);
            mainController.setCurrentStations(stationsService.filterBy(queryStation));
        } else {
            warningLabel.setText(errors);
            mainController.setCurrentStations(stationDAO.getAll());
        }

    }

    /**
     * clears the search
     */
    @FXML private void clearSearch() {
        addressSearch.setText("");
        nameSearch.setText("");
        operatorSearch.setText("");
        currentSearch.setValue("");
        timeSearch.setText("");
        attractionSearch.setValue("");
        distanceSearch.setText("50");
        for (CheckMenuItem connector : connectors) {
            connector.setSelected(false);
        }
        connectorsMenu.setText("");
        mainController.clearSearch();
        warningLabel.setText("");
        removeRangeIndicator();
        removeMarkerButton.setDisable(true);

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
     * Function to place a marker on the map when the "Place marker" button selected on the search
     */
    @FXML private void clickToPlaceMarker() {
        mainController.openMap();
        mainController.getMapViewController().setCallback((lat, lng) -> {
            addRangeIndicator(lat, lng);
            addressLatLng = lat+"#"+lng;
            search();
            removeMarkerButton.setDisable(false);
            return true;
        }, "search");

    }


    /**
     * Called to remove the range marker from the map.
     * Only called if a range marker is on the map
     */
    @FXML private void removeRangeMarker() {

        addressLatLng = "";
        mainController.clearSearchMarkerFromMap();
        removeMarkerButton.setDisable(true);
        removeRangeIndicator();
        search();
    }

    /**
     * Changes the lat and long in the search controller
     * @param lat the latitude
     * @param lng the longitude
     */
    public void changeSearchLatLong(double lat, double lng) {
        addressLatLng = lat+"#"+lng;
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

        operatorSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> search());
            pause.playFromStart();
        });

        currentSearch.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> search()));

        // the listeners for connectors are set in connectorsMultiSelect

        timeSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            pause.setOnFinished(event -> search());
            pause.playFromStart();
        });

        attractionSearch.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> search()));

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
     * Initialises the search pane.

     * @param mainController the main controller.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        this.stationsService = mainController.getStationsService();

        stationDAO = new StationDAO();

        currentSearch.setItems(chargerTypeSearchOptions);
        currentSearch.setValue("");

        attractionSearch.setItems(yesNoMaybeSo);

        rangeHelpLabel.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        rangeHelpLabel.setGraphic(rangeHelpImage);

        Tooltip rangeHelpTooltip = new Tooltip("Click the map to place a marker after clicking on the green marker button.");

        rangeHelpLabel.setTooltip(rangeHelpTooltip);

        List<String> connectorsAvailable = new ArrayList<>();
        connectorsAvailable.add("Type 2 Socketed");
        connectorsAvailable.add("CHAdeMO");
        connectorsAvailable.add("Type 2 Tethered");
        connectorsAvailable.add("Type 2 CCS");
        connectorsAvailable.add("Type 1 Tethered");

        for (String connector: connectorsAvailable) {
            connectors.add(new CheckMenuItem(connector));
        }
        connectorsMenu.getItems().addAll(connectors);
        connectorsMultiSelect();

        placeMarkerButton.setGraphic(placeMarkerImage);
        placeMarkerButton.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        addListeners();
    }
}
