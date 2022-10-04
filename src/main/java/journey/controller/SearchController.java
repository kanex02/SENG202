package journey.controller;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;
import journey.data.QueryStation;
import journey.Utils;
import journey.data.Vehicle;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
//import journey.service.SearchService;

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
    final ArrayList<CheckMenuItem> connectors = new ArrayList<>();
    ArrayList<String> connectorsList = new ArrayList<>();


    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList("", "Mixed", "AC", "DC");

    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList("", "Yes", "No");

    private MainController mainController;
    private StationDAO stationDAO;

    @FXML public void myCar() {
        ArrayList<String> selectedConnectors = new ArrayList<>();
        String myCar = mainController.getSelectedVehicle();
        Vehicle v = vehicleDAO.queryVehicle(myCar);
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


    public String[] getConnectors() {
        String[] connectorsToFind = new String[connectorsList.size()];
        for (int i = 0; i < connectorsList.size(); i++) {
            connectorsToFind[i] = connectorsList.get(i);
        }
        return connectorsToFind;
    }

    /**
     * Searches for relevant stations based on users search inputs.
     * TODO: reduce external API calls. Currently calling geolocation 3 times for each address search.
     */
    @FXML public void search() {
        String errors = errorCheck();
        if (errors == null || errors.matches("")) {
            String[] latLng = Utils.locToLatLng(addressSearch.getText()).split("#");
            mainController.addMiscMarkerToMap(Double.parseDouble(latLng[0]), Double.parseDouble(latLng[1]), "search");
            warningLabel.setText("");
//            QueryStation queryStation = SearchService.createQueryStation(nameSearch.getText(),
//                    operatorSearch.getText(),
//                    currentSearch.getValue(),
//                    getConnectors(),
//                    attractionSearch.getValue(),
//                    timeSearch.getText(),
//                    addressSearch.getText(),
//                    distanceSearch.getText());
//            mainController.setCurrentStations(stationDAO.query(queryStation));
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
        for (CheckMenuItem connector : connectors) { connector.setSelected(false); }
        connectorsMenu.setText("");
        mainController.clearSearch();
        warningLabel.setText("");
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
     * checks all inputs are given in a way that the database can understand

     * @return A string of errors
     */
    public String errorCheck() {
        StringBuilder errors = new StringBuilder();
        String address = addressSearch.getText();
        String name = nameSearch.getText();
        String operator = operatorSearch.getText();
        String timeLimit = timeSearch.getText();
        String range = distanceSearch.getText();

        //name check
        if (!name.matches("[a-zA-Z ]*")) {
            errors.append("Name cannot have special characters or integers\n");
        }

        //operator check
        if (!operator.matches("[a-zA-Z ]*")) {
            errors.append("Operator cannot have special characters or integers\n");
        }
        //time limit check
        if (!Utils.isInt(timeLimit) && !timeLimit.equals("")) {
            errors.append("Time limit must be an integer!\n");
        }

        //range check
        if (!Utils.isInt(range) && !range.equals("")) {
            errors.append("Range needs to be an integer!\n");
        }

        // range address check
        if (Utils.locToLatLng(address).equals("0.0#0.0") && !address.isBlank()) {
            errors.append("Address does not exist!\n");
        }
        return errors.toString();
    }


    public void changeSearchLatLong(String addr) {
        addressSearch.setText(addr);
    }

    /**
     * Add listeners to the search fields.
     * Note: the functions are not abstracted out as they share a common pause timer.
     */
    private void addListeners() {
        PauseTransition pause = new PauseTransition(Duration.seconds(0.3));

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
        stationDAO = new StationDAO();

        currentSearch.setItems(chargerTypeSearchOptions);
        currentSearch.setValue("");

        attractionSearch.setItems(yesNoMaybeSo);


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

        addListeners();
    }
}
