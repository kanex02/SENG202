package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import journey.data.Database;
import journey.data.QueryResult;
import journey.data.Station;


/**
 * A service to load data into the table viewer. TODO: Figure out how to get expanding rows.
 */
public class PreviousJourneyController {

    @FXML private TableColumn<Station, String> addressCol;
    @FXML private TableColumn<Station, String> nameCol;
    @FXML private TableColumn<Station, String> currentTypeCol;
    @FXML private TableColumn<Station, Integer> connectorsCol;
    @FXML private TableColumn<Station, Integer> carparksCol;
    @FXML private TableColumn<Station, Integer> timeLimitCol;
    @FXML private TableColumn<Station, Boolean> attractionCol;
    @FXML private TableColumn<Station, Boolean> isFreeCharge;
    @FXML private TableColumn<Station, Boolean> isFreePark;
    @FXML private TableView<Station> stationTable;
    @FXML private AnchorPane tableParent;

    /**
     * Imports the data.

     * @param stage The stage to import into.
     */
    public void getData(Stage stage) {
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("hasTouristAttraction"));
        carparksCol.setCellValueFactory(new PropertyValueFactory<>("carParkCount"));
        connectorsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfConnectors"));
        currentTypeCol.setCellValueFactory(new PropertyValueFactory<>("currentType"));
        isFreePark.setCellValueFactory(new PropertyValueFactory<>("hasCarParkCost"));
        isFreeCharge.setCellValueFactory(new PropertyValueFactory<>("hasChargingCost"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        timeLimitCol.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
        QueryResult data = MainController.getStations();
        ObservableList<Station> stations = FXCollections.observableArrayList(data.getStations());
        stationTable.setItems(stations);
    }

    /**
     * Initialises the table.

     * @param stage The stage to init.
     */
    public void init(Stage stage) {
        getData(stage);
        stationTable.maxWidthProperty().bind(tableParent.widthProperty());
        stationTable.maxHeightProperty().bind(tableParent.heightProperty());

        stationTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldStation, newStation) -> {
            MainController.setSelectedStation(newStation.getOBJECTID());
        }));
    }
}
