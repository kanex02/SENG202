package journey.controller;

import javafx.stage.Stage;

public class EditVehicleController {
    MainController mainController;

    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        mainController.openEditVehicle();
    }
}
