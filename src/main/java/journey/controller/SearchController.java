package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.business.NominatimGeolocationManager;
import journey.data.GeoLocationResult;
import journey.data.QueryStation;
import journey.Utils;
import journey.repository.StationDAO;

/**
 * Controller for search FXML allows searching and error checking on searches for stations.
 */
public class SearchController {
    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> chargerBoxSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private TextField addrSearch;
    @FXML private Label warningLabel;

    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList("", "Mixed", "AC", "DC");

    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList("", "Yes", "No");

    private MainController mainController;
    private StationDAO stationDAO;

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
            String attractionSearchRes = attractionSearch.getValue();
            if (attractionSearchRes != null) {
                boolean hasAttraction = (attractionSearchRes.equals("Yes"));
                searchStation.setHasTouristAttraction(hasAttraction);
            }
            if (timeSearch.getText().matches("\\d+")) {
                searchStation.setMaxTime(Integer.parseInt(timeSearch.getText()));
            }
            if (distanceSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
                NominatimGeolocationManager nomMan = new NominatimGeolocationManager();
                GeoLocationResult geoLoc = nomMan.queryAddress(addrSearch.getText());
                searchStation.setLatitude(geoLoc.getLat());
                searchStation.setLongitude(geoLoc.getLng());
                searchStation.setRange(Double.parseDouble(distanceSearch.getText()));

            }
            mainController.setCurrentStations(stationDAO.query(searchStation));
        } else {
            warningLabel.setText(errors);
        }

    }

    /**
     * clears the search
     */
    @FXML private void clearSearch() {
        addressSearch.setText("");
        nameSearch.setText("");
        operatorSearch.setText("");
        chargerBoxSearch.setValue("");
        timeSearch.setText("");
        attractionSearch.setValue("");
        distanceSearch.setText("");
        addrSearch.setText("");
        mainController.clearSearch();
    }

    /**
     * Gets the coordinates of the next click on the map. A callback
     * function is passed in, so when the map is clicked the searching
     * lat and long is updated.
     */
    @FXML private void clickSearch() {
        mainController.onlyMap();
        mainController.openMap();
        mainController.getMapViewController().setCallback((lat, lng) -> {
            String addr = Utils.latLngToAddr(lat, lng);
            addrSearch.setText(addr);
            mainController.reenable();
            mainController.openSearch();
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
        String rangeAddr = addrSearch.getText();


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
        // range address check
        if (Utils.locToLatLng(rangeAddr).equals("0.0#0.0")) {
            errors.append("Address does not exist!\n");
        }
        return errors.toString();
    }


    public void changeSearchLatLong(String addr) {
        addrSearch.setText(addr);
    }

    /**
     * Initialises the search pane.

     * @param stage current stage
     * @param mainController the main controller.
     */
    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        stationDAO = new StationDAO();

        chargerBoxSearch.setItems(chargerTypeSearchOptions);
        chargerBoxSearch.setValue("");

        attractionSearch.setItems(yesNoMaybeSo);
    }
}
