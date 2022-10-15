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
     * Error checking for entering a vehicle.

     * @return whether result passed error checking or not (true/false).
     */
    private boolean isValid() {
        boolean valid = true;
        String registration = registrationTextBox.getText();
        String year = yearTextBox.getText();
        String make = makeTextBox.getText();
        String model = modelTextBox.getText();
        chargerTypeChoice();
        connectorTypeChoice();

        //registration validation
        if (Objects.equals(registration, "")) {
            regWarningLabel.setText("Please enter a registration");
            valid = false;
        } else if (!registration.matches(Utils.getCharacterDigit())) {
            regWarningLabel.setText("Cannot contain special characters");
            valid = false;
        } else if (registration.length() > 6) {
            regWarningLabel.setText("Cannot be more than 6 characters");
            valid = false;
        } else if (vehicleDAO.queryVehicle(registration,
                profileController.getProfileMainController().getCurrentUser().getId()) != null) {
            regWarningLabel.setText("A vehicle with this registration already exists for this user!");
            valid = false;
        }

        //make validation
        if (!make.matches(Utils.getCharacterOnly())) {
            makeWarningLabel.setText("Cannot contain digits or special characters");
            valid = false;
        } else if (make.equals("")) {
            makeWarningLabel.setText("Please enter a model");
            valid = false;
        } else if (make.length() > 20) {
            makeWarningLabel.setText("Make cannot be more than 20 characters long");
            valid = false;
        }

        //model validation
        if (!model.matches(Utils.getCharacterDigit())) {
            modelWarningLabel.setText("Cannot contain special characters");
            valid = false;
        } else if (model.equals("")) {
            modelWarningLabel.setText("Please enter a model");
            valid = false;
        } else if (model.length() > 20) {
            modelWarningLabel.setText("Model cannot be more than 20 characters long");
            valid = false;
        }

        //year validation
        int intYear;
        if (year.equals("")) {
            yearWarningLabel.setText("Please enter a year");
            valid = false;
        } else {
            if (Utils.isInt(year)) {
                intYear = Integer.parseInt(year);
                String date = Utils.getDate();
                int currentYear = Integer.parseInt(date.split("/")[2]);
                if (intYear > currentYear || intYear < 1996) {
                    yearWarningLabel.setText("Year is out of range");
                    valid = false;
                }
            } else {
                yearWarningLabel.setText("Year must be an integer");
                valid = false;
            }
        }

        //current validation
        if (chargerTypeChoice == null || chargerTypeChoice.equals("")) {
            currentWarningLabel.setText("Please select a current type");
            valid = false;
        }

        //connector validation
        if (connectorTypeChoice == null || connectorTypeChoice.equals("")) {
            connectorWarningLabel.setText("Please select a connector type");
            valid = false;
        }

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
