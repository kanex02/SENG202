package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import journey.data.QueryResult;
import journey.data.Station;


/**
 * A service to load data into the table viewer. TODO: Figure out how to get expanding rows.
 */
public class TableController {

    @FXML private TableColumn<Station, String> addressCol;
    @FXML private TableColumn<Station, Boolean> attractionCol;
    @FXML private TableColumn<Station, Integer> carparksCol;
    @FXML private TableColumn<Station, Integer> connectorsCol;
    @FXML private TableColumn<Station, String> currentTypeCol;
    @FXML private TableColumn<Station, Boolean> isFreePark;
    @FXML private TableColumn<Station, Boolean> isFreeCharge;
    @FXML private TableColumn<Station, Integer> latCol;
    @FXML private TableColumn<Station, Integer> longCol;
    @FXML private TableColumn<Station, String> nameCol;
    @FXML private TableColumn<Station, String> operatorCol;
    @FXML private TableColumn<Station, Integer> timeLimitCol;
    @FXML private TableView<Station> stationTable;
    @FXML private AnchorPane tableParent;

    private MainController mainController;

    /**
     * Imports the data.
     */
    public void getData() {
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("hasTouristAttraction"));
        carparksCol.setCellValueFactory(new PropertyValueFactory<>("carParkCount"));
        connectorsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfConnectors"));
        currentTypeCol.setCellValueFactory(new PropertyValueFactory<>("currentType"));
        isFreePark.setCellValueFactory(new PropertyValueFactory<>("hasCarParkCost"));
        isFreeCharge.setCellValueFactory(new PropertyValueFactory<>("hasChargingCost"));
        latCol.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longCol.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        operatorCol.setCellValueFactory(new PropertyValueFactory<>("operator"));
        timeLimitCol.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
        QueryResult data = mainController.getStations();
        ObservableList<Station> stations = FXCollections.observableArrayList(data.getStations());
        stationTable.setItems(stations);
    }

    /**
     * Initialises the table.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        getData();
        stationTable.maxWidthProperty().bind(tableParent.widthProperty());
        stationTable.maxHeightProperty().bind(tableParent.heightProperty());

        stationTable.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldStation, newStation) -> {
                    mainController.setSelectedStation(newStation.getOBJECTID());
                    mainController.updateNoteText();
                    mainController.setStationText();
            }
        );
    }
}
