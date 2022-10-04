package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;

/**
 * Controller for editing existing vehicles.
 */
public class EditVehicleController {
    @FXML private Button closeButton;
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private ChoiceBox<String> connectorBox;
    @FXML private TextField registrationTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    @FXML private TextField yearTextBox;
    private String chargerTypeChoice;
    private String connectorTypeChoice;
    private MainController mainController;
    private VehicleDAO vehicleDAO;
    private Vehicle currentVehicle;

    private static final ObservableList<String> chargerTypeOptions =
            FXCollections.observableArrayList(
                    "AC",
                    "DC"
            );


    private static final ObservableList<String> connectorTypeOptions =
            FXCollections.observableArrayList(
                    "Type 2 Socketed",
                    "CHAdeMO",
                    "Type 2 Tethered",
                    "Type 2 CCS",
                    "Type 1 Tethered"
            );

    /**
     * Close the edit/delete vehicle popup.
     */
    public void closeScene() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        mainController.setVehicle();
        stage.close();
    }

    /**
     * Set the charger type variable to the value currently in the current dropdown.
     */
    @FXML private void chargerTypeChoice() {
        chargerTypeChoice = chargerBox.getValue();
    }

    /**
     * Set the connector type variable to the value currently in the connector dropdown.
     */
    @FXML private void connectorTypeChoice() {
        connectorTypeChoice = connectorBox.getValue();
    }

    /**
     * Fill the popup with the details of the currently selected vehicle.
     */
    public void fillCurrentVehicle() {
        String reg = mainController.getSelectedVehicle();
        currentVehicle = vehicleDAO.queryVehicle(reg);
        registrationTextBox.setText(reg);
        makeTextBox.setText(currentVehicle.getMake());
        modelTextBox.setText(currentVehicle.getModel());
        yearTextBox.setText(Integer.toString(currentVehicle.getYear()));
        chargerBox.setValue(currentVehicle.getChargerType());
        connectorBox.setValue(currentVehicle.getConnectorType());
        vehicleDAO.removeVehicle(reg);
    }

    /**
     * Selete the old version of the vehicle and create a new vehicle in the database with the updated
     * values.
     */
    @FXML public void saveVehicle() {
        String currentVehicle = mainController.getSelectedVehicle();
        vehicleDAO.removeVehicle(currentVehicle);
        String reg = registrationTextBox.getText();
        String make = makeTextBox.getText();
        String model = modelTextBox.getText();
        String year = yearTextBox.getText();
        int intYear = Integer.parseInt(year);
        chargerTypeChoice();
        connectorTypeChoice();
        String current = chargerTypeChoice;
        String connector = connectorTypeChoice;
        Vehicle newVehicle = new Vehicle(intYear, make, model, current, reg, connector);
        vehicleDAO.setVehicle(newVehicle, mainController.getCurrentUser());
        mainController.populateVehicleDropdown();
        mainController.setSelectedVehicle(reg);
    }

    /**
     * delete the currently selected vehicle from the database.
     */
    @FXML public void deleteVehicle() {
        String reg = mainController.getSelectedVehicle();
        currentVehicle = vehicleDAO.queryVehicle(reg);
        vehicleDAO.removeVehicle(reg);
        mainController.setSelectedVehicle(null);
        registrationTextBox.setText("");
        makeTextBox.setText("");
        modelTextBox.setText("");
        yearTextBox.setText("");
        chargerBox.setValue("");
        connectorBox.setValue("");
    }

    /**
     * initialise the stage with the edit vehicle popup.

     * @param mainController instance of the main controller
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        vehicleDAO = new VehicleDAO();
        chargerBox.setItems(chargerTypeOptions);
        connectorBox.setItems(connectorTypeOptions);
        if (mainController.getSelectedVehicle() != null) {
            fillCurrentVehicle();
        }
    }
}