package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private static final Logger log = LogManager.getLogger();
    private MainController mainController;
    private VehicleDAO vehicleDAO;
    private Vehicle currentVehicle;
    private CreateJourneyController recordJourneyController;

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
     * close the edit/delete vehicle popup
     */
    public void closeScene() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        mainController.setVehicle(stage);
        stage.close();
    }

    /**
     * set the charger type variable to the value currently in the current dropdown
     * @param event
     */
    @FXML private void chargerTypeChoice(Event event) {
        chargerTypeChoice = chargerBox.getValue();
        event.consume();
    }

    /**
     * set the connector type varibale to the value currently in the connector dropdown
     * @param event
     */
    @FXML private void connectorTypeChoice(Event event) {
        connectorTypeChoice = connectorBox.getValue();
        event.consume();
    }

    /**
     * fill the popup with the details of the currently selected vehicle
     */
    public void fillCurrentVehicle() {
        String reg = mainController.getSelectedVehicle();
        currentVehicle = vehicleDAO.queryVehicle(reg);
        registrationTextBox.setText(reg);
        makeTextBox.setText(currentVehicle.getMake());
        modelTextBox.setText(currentVehicle.getMake());
        yearTextBox.setText(Integer.toString(currentVehicle.getYear()));
        chargerBox.setValue(currentVehicle.getChargerType());
        connectorBox.setValue(currentVehicle.getConnectorType());
        vehicleDAO.removeVehicle(reg);
    }

    /**
     * delete the old version of the vehicle and create a new vehicle in the database with the updated
     * values
     * @param event event of clicking the save button
     */
    @FXML public void saveVehicle(Event event) {
        String currentVehicle = mainController.getSelectedVehicle();
        vehicleDAO.removeVehicle(currentVehicle);
        String reg = registrationTextBox.getText();
        String make = makeTextBox.getText();
        String model = modelTextBox.getText();
        String year = yearTextBox.getText();
        int intYear = Integer.parseInt(year);
        chargerTypeChoice(event);
        connectorTypeChoice(event);
        String current = chargerTypeChoice;
        String connector = connectorTypeChoice;
        Vehicle newVehicle = new Vehicle(intYear, make, model, current, reg, connector);
        try {
            vehicleDAO.setVehicle(newVehicle, mainController.getCurrentUser());
            recordJourneyController.populateVehicleDropdown();
        } catch (Exception e) {
            log.error(e);
        }
        mainController.setSelectedVehicle(reg);
        event.consume();
    }

    /**
     * delete the currently selected vehicle from the database
     * @param event
     */
    @FXML public void deleteVehicle(Event event) {
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
     * initialise the stage with the edit vehicle popup
     * @param stage
     * @param mainController
     */
    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        vehicleDAO = new VehicleDAO();
        chargerBox.setItems(chargerTypeOptions);
        connectorBox.setItems(connectorTypeOptions);
        if (!mainController.getSelectedVehicle().equals(null)) {
            fillCurrentVehicle();
        }
    }
}
