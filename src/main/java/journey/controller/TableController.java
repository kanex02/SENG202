package journey.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import journey.data.Station;


/**
 * A service to load data into the table viewer.
 */
public class TableController {

    @FXML private TableColumn<Station, String> addressCol;
    @FXML private TableColumn<Station, String> attractionCol;
    @FXML private TableColumn<Station, Integer> carparksCol;
    @FXML private TableColumn<Station, Integer> connectorsCol;
    @FXML private TableColumn<Station, String> currentTypeCol;
    @FXML private TableColumn<Station, String> isFreePark;
    @FXML private TableColumn<Station, String> isFreeCharge;
    @FXML private TableColumn<Station, Integer> latCol;
    @FXML private TableColumn<Station, Integer> longCol;
    @FXML private TableColumn<Station, String> nameCol;
    @FXML private TableColumn<Station, String> operatorCol;
    @FXML private TableColumn<Station, String> timeLimitCol;
    @FXML private TableColumn<Station, Integer> ratingCol;
    @FXML private TableColumn<Station, String> favouriteCol;

    @FXML private TableView<Station> stationTable;
    @FXML private AnchorPane tableParent;
    private static final String UNLIMITED = "Unlimited"; 
    private MainController mainController;

    /**
     * Imports the data.
     */
    public void getData() {
        // Custom filtering for values of unlimited within the time limit column.
        timeLimitCol.setComparator((s, t1) -> {
            if (s.equals(UNLIMITED)) {
                return 1;
            }
            if (t1.equals(UNLIMITED)) {
                return -1;
            }
            int firstIntegerValue = Integer.parseInt(s);
            int secondIntegerValue = Integer.parseInt(t1);
            return firstIntegerValue - secondIntegerValue;
        });
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        attractionCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(Boolean.TRUE.equals(cellData.getValue().getHasTouristAttraction()) ? "Yes" : "No"));
        carparksCol.setCellValueFactory(new PropertyValueFactory<>("carParkCount"));
        connectorsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfConnectors"));
        currentTypeCol.setCellValueFactory(new PropertyValueFactory<>("currentType"));
        isFreePark.setCellValueFactory(cellData ->
                new SimpleStringProperty(Boolean.TRUE.equals(cellData.getValue().hasCarParkCost()) ? "Yes" : "No"));
        isFreeCharge.setCellValueFactory(cellData ->
                new SimpleStringProperty(Boolean.TRUE.equals(cellData.getValue().hasChargingCost()) ? "Yes" : "No"));
        latCol.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longCol.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        operatorCol.setCellValueFactory(new PropertyValueFactory<>("operator"));
        timeLimitCol.setCellValueFactory(cellData -> {
            int value = cellData.getValue().getMaxTime();
            return new SimpleStringProperty(value == 0 ? UNLIMITED : Integer.toString(value));
        });
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        favouriteCol.setCellValueFactory(cellData ->
                new SimpleStringProperty(Boolean.TRUE.equals(cellData.getValue().getFavourite()) ? "Yes" : "No"));
        Station[] data = mainController.getStations();
        ObservableList<Station> stations = FXCollections.observableArrayList(data);

        stationTable.setItems(stations);
    }


    public void clearSelections() {
        stationTable.getSelectionModel().clearSelection();
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
                    if (newStation != null) {
                        mainController.setSelectedStation(newStation.getObjectid());
                        mainController.setSelectedStationID(newStation.getObjectid(), newStation.getFavourite());
                    }
                });
    }
}
