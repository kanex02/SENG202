package journey.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import journey.data.Database;

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

    public void setVehicles(Stage stage) {
        vehicles.setText(String.valueOf(Database.getCurrentUser().getVehicles().get(1)));
    }


}
