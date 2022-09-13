//package journey.controller;
//
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.stage.Stage;
//import journey.data.Database;
//import journey.data.QueryResult;
//import journey.data.Station;
//
//
///**
// * A service to load data into the table viewer. TODO: Figure out how to get expanding rows.
// */
//public class TableService {
//
//    @FXML private TableColumn<Station, String> addressCol;
//
//    @FXML private TableColumn<Station, Boolean> attractionCol;
//
//    @FXML private TableColumn<Station, Integer> carparksCol;
//
//    @FXML private TableColumn<Station, Integer> connectorsCol;
//
//    @FXML private TableColumn<Station, String> currentTypeCol;
//
//    @FXML private TableColumn<Station, Boolean> isFreePark;
//
//    @FXML private TableColumn<Station, Boolean> isFreeCharge;
//
//    @FXML private TableColumn<Station, Integer> latCol;
//
//    @FXML private TableColumn<Station, Integer> longCol;
//
//    @FXML private TableColumn<Station, String> nameCol;
//
//    @FXML private TableColumn<Station, String> operatorCol;
//
//    @FXML private TableColumn<Station, Integer> timeLimitCol;
//
//    @FXML private TableView<Station> stationTable;
//
//    @FXML private Button closeButton;
//
//
//    /**
//     * Closes the scene.
//     */
//    public void closeScene() {
//        Stage stage = (Stage) closeButton.getScene().getWindow();
//        stage.close();
//    }
//
//    /**
//     * Imports the data.
//
//     * @param stage The stage to import into.
//     */
//    public void getData(Stage stage) {
//        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
//        attractionCol.setCellValueFactory(new PropertyValueFactory<>("hasTouristAttraction"));
//        carparksCol.setCellValueFactory(new PropertyValueFactory<>("carParkCount"));
//        connectorsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfConnectors"));
//        currentTypeCol.setCellValueFactory(new PropertyValueFactory<>("currentType"));
//        isFreePark.setCellValueFactory(new PropertyValueFactory<>("hasCarParkCost"));
//        isFreeCharge.setCellValueFactory(new PropertyValueFactory<>("hasChargingCost"));
//        latCol.setCellValueFactory(new PropertyValueFactory<>("latitude"));
//        longCol.setCellValueFactory(new PropertyValueFactory<>("longitude"));
//        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
//        operatorCol.setCellValueFactory(new PropertyValueFactory<>("operator"));
//        timeLimitCol.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
//        QueryResult data = Database.catchEmAll();
//        ObservableList<Station> stations = FXCollections.observableArrayList(data.getStations());
//        stationTable.setItems(stations);
//    }
//
//    /**
//     * Initialises the table.
//
//     * @param stage The stage to init.
//     */
//    public void init(Stage stage) {
//        getData(stage);
//
//        // Selects the station that was previously selected.
//        if (MainController.getSelectedStationFromTable() != -1) {
//            for (int i = 0; i < stationTable.getItems().size(); i++) {
//                if (stationTable.getItems().get(i).getOBJECTID() == MainController.getSelectedStationFromTable()) {
//                    stationTable.getSelectionModel().select(i);
//                    break;
//                }
//            }
//        }
//        stationTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldStation, newStation) -> {
//            MainController.setSelectedStationFromTable(newStation.getOBJECTID());
//            System.out.println(MainController.getSelectedStationFromTable());
//        }));
//    }
//}
