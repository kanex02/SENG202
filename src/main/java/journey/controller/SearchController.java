package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import jdk.jshell.execution.Util;
import journey.data.QueryStation;
import journey.data.Utils;
import journey.repository.StationDAO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchController {
    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> chargerBoxSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private TextField searchLat;
    @FXML private TextField searchLong;
    @FXML private Label warningLabel;

    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList("", "Mixed", "AC", "DC");

    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList("", "Yes", "No");

    private MainController mainController;
    private StationDAO stationDAO;
    Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");
    Pattern digit = Pattern.compile("[0-9]");

    /**
     * Searches for relevant stations based on users search inputs
     */
    @FXML private void search() {
        boolean valid = errorCheck();
        if (valid) {
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
            if (searchLat.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                    & searchLong.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                    & distanceSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
                searchStation.setLatitude(Double.parseDouble(searchLat.getText()));
                searchStation.setLongitude(Double.parseDouble(searchLong.getText()));
                searchStation.setRange(Double.parseDouble(distanceSearch.getText()));
            }

            mainController.setCurrentStations(stationDAO.query(searchStation));
            mainController.viewMap();
            mainController.viewTable();
        }

    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the searching lat and long is updated.
     */
    @FXML private void clickSearch() {
        mainController.onlyMap();
        mainController.openMap();
        mainController.getMapViewController().setCallback((lat, lng) -> {
            searchLat.setText(String.valueOf(lat));
            searchLong.setText(String.valueOf(lng));
            mainController.reenable();
            mainController.openSearch();
            return true;
        });
    }

    public boolean errorCheck() {
        boolean valid = true;
        String address = addressSearch.getText();
        String name = nameSearch.getText();
        String operator = operatorSearch.getText();
        String timeLimit = timeSearch.getText();
        String range = distanceSearch.getText();

        Matcher addressHasSpecial = special.matcher(address);
        Matcher nameHasSpecial = special.matcher(name);
        Matcher nameHasDigit = digit.matcher(name);
        Matcher operatorHasSpecial = special.matcher(operator);
        Matcher operatorHasDigit = digit.matcher(operator);

        //address check
        if (addressHasSpecial.find() && !address.equals("")) {
            valid = false;
            warningLabel.setText("Address must only have A-Z and 0-9");
        }

        //name check
        if (nameHasDigit.find() || nameHasSpecial.find() && !name.equals("")) {
            valid = false;
            warningLabel.setText("Name cannot have special characters or integers");
        }

        //operator check
        if (operatorHasDigit.find() || operatorHasSpecial.find() && !operator.equals("")) {
            valid = false;
            warningLabel.setText("Operator cannot have special characters or integers");
        }

        //time limit check
        if (!Utils.isInt(timeLimit) && !timeLimit.equals("")) {
            valid = false;
            warningLabel.setText("Time limit must be an integer!");
        }

        //range check
        if (!Utils.isInt(range) && !range.equals("")) {
            valid = false;
            warningLabel.setText("Range needs to be an integer!");
        }

        return valid;
    }

    /**
     * Initialises the search pane.

     * @param mainController the main controller.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        stationDAO = new StationDAO();

        chargerBoxSearch.setItems(chargerTypeSearchOptions);
        chargerBoxSearch.setValue("");

        attractionSearch.setItems(yesNoMaybeSo);
    }
}
