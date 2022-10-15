package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import journey.Utils;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;

import java.util.Objects;


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
     * Check a vehicle's registration input is valid.

     * @param valid input is valid.
     * @return if input is valid.
     */
    private boolean regValid(boolean valid) {
        String registration = registrationTextBox.getText();
        if (Objects.equals(registration, "")) {
            regWarningLabel.setText("Please enter a registration");
            registrationTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (!registration.matches(Utils.getCharacterDigit())) {
            regWarningLabel.setText("Cannot contain special characters");
            registrationTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (registration.length() > 6) {
            regWarningLabel.setText("Cannot be more than 6 characters");
            registrationTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (vehicleDAO.queryVehicle(registration, 
                profileController.getProfileMainController().getCurrentUser().getId()) != null) {
            regWarningLabel.setText("A vehicle with this registration already exists for this user!");
            registrationTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        }
        return valid;
    }

    /**
     * Check a vehicle's make and model input is valid.

     * @param valid input is valid.
     * @return if input is valid.
     */
    private boolean makeModelValid(boolean valid) {
        String make = makeTextBox.getText();
        String model = modelTextBox.getText();
        //make validation
        if (!make.matches(Utils.getCharacterOnly())) {
            makeWarningLabel.setText("Cannot contain digits or special characters");
            makeTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (make.equals("")) {
            makeWarningLabel.setText("Please enter a model");
            makeTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (make.length() > 20) {
            makeWarningLabel.setText("Make cannot be more than 20 characters long");
            makeTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        }
        //model validation
        if (!model.matches(Utils.getCharacterDigit())) {
            modelWarningLabel.setText("Cannot contain special characters");
            modelTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (model.equals("")) {
            modelWarningLabel.setText("Please enter a model");
            modelTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else if (model.length() > 20) {
            modelWarningLabel.setText("Model cannot be more than 20 characters long");
            modelTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        }
        return valid;
    }

    /**
     * Check a vehicle's year input is valid.

     * @param valid input is valid.
     * @return if input is valid.
     */
    private boolean yearValid(boolean valid) {
        String year = yearTextBox.getText();
        int intYear;
        if (year.equals("")) {
            yearWarningLabel.setText("Please enter a year");
            yearTextBox.setStyle(WARNINGCOLOUR);
            valid = false;
        } else {
            if (Utils.isInt(year)) {
                intYear = Integer.parseInt(year);
                String date = Utils.getDate();
                int currentYear = Integer.parseInt(date.split("/")[2]);
                if (intYear > currentYear || intYear < 1996) {
                    yearWarningLabel.setText("Year is out of range");
                    yearTextBox.setStyle(WARNINGCOLOUR);
                    valid = false;
                }
            } else {
                yearWarningLabel.setText("Year must be an integer");
                yearTextBox.setStyle(WARNINGCOLOUR);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Check a vehicle's current and connectors input is valid.

     * @param valid input is valid.
     * @return if input is valid.
     */
    private boolean currentConnectorValid(boolean valid) {
        //current validation
        if (chargerTypeChoice == null || chargerTypeChoice.equals("")) {
            currentWarningLabel.setText("Please select a current type");
            chargerBox.setStyle(WARNINGCOLOUR);
            valid = false;
        }

        //connector validation
        if (connectorTypeChoice == null || connectorTypeChoice.equals("")) {
            connectorWarningLabel.setText("Please select a connector type");
            connectorBox.setStyle(WARNINGCOLOUR);
            valid = false;
        }
        return valid;
    }

    /**
     * Error checking for entering a vehicle.

     * @return whether result passed error checking or not (true/false).
     */
    private boolean isValid() {
        boolean valid = true;
        chargerTypeChoice();
        connectorTypeChoice();

        //registration validation
        valid = regValid(valid);

        //make and model validation
        valid = makeModelValid(valid);

        //year validation
        valid = yearValid(valid);

        //current and connector validation
        valid = currentConnectorValid(valid);

        return valid;
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
        if (isValid()) {
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
