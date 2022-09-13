package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import journey.data.Database;
import journey.data.QueryResult;
import journey.data.Station;

public class ProfileController {

    @FXML private Button closeButton;
    @FXML private Label name;
    @FXML private Label vehicles;
    public void closeScene() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

   public void setName(Stage stage) {
        name.setText(Database.getCurrentUser().getName());
    }

//    public void setVehicles(Stage stage) {
//        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
//        attractionCol.setCellValueFactory(new PropertyValueFactory<>("hasTouristAttraction"));
//        carparksCol.setCellValueFactory(new PropertyValueFactory<>("carParkCount"));
//        connectorsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfConnectors"));
//        currentTypeCol.setCellValueFactory(new PropertyValueFactory<>("currentType"));
//        isFreePark.setCellValueFactory(new PropertyValueFactory<>("hasCarParkCost"));
//        QueryResult data = Database.catchEmAll();
//        ObservableList<Station> stations = FXCollections.observableArrayList(data.getStations());
//        stationTable.setItems(stations);
//    }


}
