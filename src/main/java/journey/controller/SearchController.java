package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import journey.data.QueryStation;
import journey.repository.StationDAO;

public class SearchController {
    @FXML private AnchorPane searchPane;
    @FXML private HBox searchRow;
    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> chargerBoxSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private TextField searchLat;
    @FXML private TextField searchLong;

    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList("", "Mixed", "AC", "DC");

    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList("", "Yes", "No");

    private MainController mainController;
    private StationDAO stationDAO;

    /**
     * Searches for relevant stations based on users search inputs

     * @param event search button pressed
     */
    @FXML private void search(Event event) {
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

    /**
     * Initialises the search pane.

     * @param stage stage to initiate into.
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
