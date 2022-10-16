package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;
import journey.service.VehicleService;


/**
 * Controller for editing existing vehicles.
 */
public class EditVehicleController {
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private ChoiceBox<String> connectorBox;
    @FXML private TextField registrationTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    @FXML private TextField yearTextBox;
    @FXML private Label regWarningLabel;
    @FXML private Label makeWarningLabel;
    @FXML private Label modelWarningLabel;
    @FXML private Label yearWarningLabel;
    @FXML private Label currentWarningLabel;
    @FXML private Label connectorWarningLabel;
    private static final String WARNINGCOLOUR = "-fx-background-color: red, #efefef";
    private String chargerTypeChoice;
    private String connectorTypeChoice;
    private ProfileController profileController;
    private VehicleDAO vehicleDAO;
    private Vehicle currentVehicle;
    private String currentError;
    private String connectorError;
    private String regError;
    private String makeError;
    private String modelError;
    private String yearError;

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
        registrationTextBox.setText(currentVehicle.getRegistration());
        makeTextBox.setText(currentVehicle.getMake());
        modelTextBox.setText(currentVehicle.getModel());
        yearTextBox.setText(Integer.toString(currentVehicle.getYear()));
        chargerBox.setValue(currentVehicle.getChargerType());
        connectorBox.setValue(currentVehicle.getConnectorType());
    }

    /**
     * Display warning labels for invalid input.
     */
    private void displayErrors() {
        Vehicle exists = vehicleDAO.queryVehicle(registrationTextBox.getText(),
                profileController.getProfileMainController().getCurrentUser().getId());

        chargerTypeChoice();
        connectorTypeChoice();
        currentError = VehicleService.currentValid(chargerTypeChoice);
        connectorError = VehicleService.connectorValid(connectorTypeChoice);
        regError = VehicleService.regValid(registrationTextBox.getText(), exists);
        makeError = VehicleService.makeValid(makeTextBox.getText());
        modelError = VehicleService.modelValid(modelTextBox.getText());
        yearError = VehicleService.yearValid(yearTextBox.getText());

        currentWarningLabel.setText(currentError);
        connectorWarningLabel.setText(connectorError);
        regWarningLabel.setText(regError);
        makeWarningLabel.setText(makeError);
        modelWarningLabel.setText(modelError);
        yearWarningLabel.setText(yearError);

        if (!regError.equals("")) {
            registrationTextBox.setStyle(WARNINGCOLOUR);
        }
        if (!makeError.equals("")) {
            makeTextBox.setStyle(WARNINGCOLOUR);
        }
        if (!modelError.equals("")) {
            modelTextBox.setStyle(WARNINGCOLOUR);
        }
        if (!yearError.equals("")) {
            yearTextBox.setStyle(WARNINGCOLOUR);
        }
        if (!connectorError.equals("")) {
            connectorBox.setStyle(WARNINGCOLOUR);
        }
        if (!currentError.equals("")) {
            chargerBox.setStyle(WARNINGCOLOUR);
        }
    }



    /**
     * Delete the old version of the vehicle and create a new vehicle in the database with the updated
     * values. If invalid enter old vehicle back into the database.
     */
    @FXML private void saveVehicle() {
        regWarningLabel.setText("");
        makeWarningLabel.setText("");
        modelWarningLabel.setText("");
        yearWarningLabel.setText("");
        currentWarningLabel.setText("");
        connectorWarningLabel.setText("");
        vehicleDAO.removeVehicle(currentVehicle.getRegistration(),
                profileController.getProfileMainController().getCurrentUser().getId());
        displayErrors();
        boolean valid = VehicleService.isValid(regError, makeError, modelError, yearError, currentError, connectorError);
        if (valid) {
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
            vehicleDAO.setVehicle(newVehicle, profileController.getProfileMainController().getCurrentUser());
            profileController.setVehicle();
            profileController.setVehicles();
            profileController.getProfileMainController().viewRegisterVehicles();
        } else {
            vehicleDAO.setVehicle(currentVehicle, profileController.getProfileMainController().getCurrentUser());
        }
    }

    /**
     * Cancels changes made to vehicle and returns to register vehicle screen.
     */
    @FXML private void cancelChanges() {
        profileController.getProfileMainController().viewRegisterVehicles();
    }


    /**
     * initialise the stage with the edit vehicle popup.

     * @param profileMainController instance of the main controller
     */
    public void init(ProfileMainController profileMainController) {
        this.profileController = profileMainController.getProfileController();
        vehicleDAO = new VehicleDAO();
        chargerBox.setItems(chargerTypeOptions);
        connectorBox.setItems(connectorTypeOptions);
        this.currentVehicle = vehicleDAO.getSelectedVehicle(
                profileController.getProfileMainController().getCurrentUser());
        if (currentVehicle != null) {
            fillCurrentVehicle();
        }
    }
}
