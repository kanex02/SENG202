package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import journey.data.QueryStation;
import journey.data.Utils;
import journey.data.Vehicle;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;

import java.util.ArrayList;
import java.util.List;

public class SearchController {
    private VehicleDAO vehicleDAO = new VehicleDAO();

    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> chargerBoxSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private TextField latSearch;
    @FXML private TextField longSearch;
    @FXML private Label warningLabel;
    @FXML private CheckBox myCarCheckBox;
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
        if (myCarCheckBox.isSelected()) {
            String myCar = mainController.getSelectedVehicle();
            Vehicle v = vehicleDAO.queryVehicle(myCar);
            chargerBoxSearch.setValue(v.getChargerType());
            distanceSearch.setText("300");

            selectedConnectors.clear();
            for (CheckMenuItem connector : connectors) {
                if (!connector.getText().contains(v.getConnectorType()) ) {
                    connector.setSelected(false);
                } else {
                    connector.setSelected(true);
                    selectedConnectors.add(connector.getText());
                }
            }
            connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
        } else {
            chargerBoxSearch.setValue("");
            connectors.get(0).setSelected(false);
            for (CheckMenuItem connector : connectors) {
                connector.setSelected(false);
            }
            connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
            for (CheckMenuItem connector : connectors) {
                connector.selectedProperty().addListener(((observableValue, oldValue, newValue) -> {
                    if (newValue) {
                        selectedConnectors.add(connector.getText());
                        connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
                    } else {
                        selectedConnectors.remove(connector.getText());
                        connectorsMenu.setText(Utils.convertArrayListToString(selectedConnectors, ", "));
                    }
                }));
            }
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
     * Searches for relevant stations based on users search inputs
     */
    @FXML public void search() {
        String errors = errorCheck();
        if (errors == null || errors.matches("")) {
            warningLabel.setText("");
            QueryStation searchStation = new QueryStation();
            searchStation.setAddress(addressSearch.getText());
            searchStation.setName(nameSearch.getText());
            searchStation.setOperator(operatorSearch.getText());
            searchStation.setCurrentType(chargerBoxSearch.getValue());
            searchStation.setConnectors(getConnectors());
            String attractionSearchRes = attractionSearch.getValue();
            if (attractionSearchRes != null) {
                boolean hasAttraction = (attractionSearchRes.equals("Yes"));
                searchStation.setHasTouristAttraction(hasAttraction);
            }
            if (timeSearch.getText().matches("\\d+")) {
                searchStation.setMaxTime(Integer.parseInt(timeSearch.getText()));
            }
            if (latSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                    & longSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                    & distanceSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
                searchStation.setLatitude(Double.parseDouble(latSearch.getText()));
                searchStation.setLongitude(Double.parseDouble(longSearch.getText()));
                searchStation.setRange(Double.parseDouble(distanceSearch.getText()));
            }

            mainController.setCurrentStations(stationDAO.query(searchStation));
        } else {
            warningLabel.setText(errors);
        }

    }

    @FXML private void clearSearch() {
        addressSearch.setText("");
        nameSearch.setText("");
        operatorSearch.setText("");
        chargerBoxSearch.setValue("");
        timeSearch.setText("");
        attractionSearch.setValue("");
        distanceSearch.setText("");
        latSearch.setText("");
        longSearch.setText("");
        //connectorBoxSearch.setValue("");
        myCarCheckBox.setSelected(false);
        mainController.clearSearch();
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the searching lat and long is updated.
     */
    @FXML private void clickSearch() {
        mainController.onlyMap();
        mainController.openMap();
        mainController.getMapViewController().setCallback((lat, lng) -> {
            latSearch.setText(String.valueOf(lat));
            longSearch.setText(String.valueOf(lng));
            mainController.reenable();
            mainController.openSearch();
            return true;
        }, "search");
    }

    public String errorCheck() {
        StringBuilder errors = new StringBuilder();
        String address = addressSearch.getText();
        String name = nameSearch.getText();
        String operator = operatorSearch.getText();
        String timeLimit = timeSearch.getText();
        String range = distanceSearch.getText();


        //address check
        if (!address.matches("[0-9|a-z|A-Z| ]*")) {
            errors.append("Address must only have A-Z and 0-9\n");
        }

        //name check
        if (!name.matches("[a-z|A-Z| ]*")) {
            errors.append("Name cannot have special characters or integers\n");
        }

        //operator check
        if (!operator.matches("[a-z|A-Z| ]*")) {
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

        return errors.toString();
    }

    public void changeSearchLatLong(double lat, double lng) {
        latSearch.setText(String.valueOf(lat));
        longSearch.setText(String.valueOf(lng));
    }


    /**
     * Initialises the search pane.
     * @param stage
     * @param mainController the main controller.
     */
    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        stationDAO = new StationDAO();

        chargerBoxSearch.setItems(chargerTypeSearchOptions);
        chargerBoxSearch.setValue("");
        //connectorsMultiSelect();

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
    }
}
