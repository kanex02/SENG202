package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import journey.data.Database;
import journey.data.QueryResult;
import journey.data.Station;
import journey.data.Vehicle;

public class ProfileController {

    @FXML private Button closeButton;
    @FXML private Label name;
    @FXML private TableColumn<Vehicle, String> registrationCol;

    @FXML private TableColumn<Vehicle, String> makeCol;

    @FXML private TableColumn<Vehicle, String> modelCol;

    @FXML private TableColumn<Vehicle, Integer> yearCol;

    @FXML private TableColumn<Vehicle, String> chargerTypeCol;

    @FXML private TableView<Vehicle> vehicleTable;
    public void closeScene() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

   public void setName(Stage stage) {
        name.setText(Database.getCurrentUser().getName());
    }

    public void setVehicles(Stage stage) {
        registrationCol.setCellValueFactory(new PropertyValueFactory<>("Registration"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("Make"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("Model"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        chargerTypeCol.setCellValueFactory(new PropertyValueFactory<>("ChargerType"));
        QueryResult data = Database.getVehicles();
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(data.getVehicles());
        vehicleTable.setItems(vehicles);
    }


}
