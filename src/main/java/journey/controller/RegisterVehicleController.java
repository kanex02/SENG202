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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;


/**
 * Controller for the registerVehicle FXML allows registering a vehicle and does error checking on the inputs given.
 */
public class RegisterVehicleController {

    private static final Logger log = LogManager.getLogger();
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
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private ChoiceBox<String> connectorBox;
    @FXML private Label successLabel;
    @FXML private Label regWarningLabel;
    @FXML private Label makeWarningLabel;
    @FXML private Label modelWarningLabel;
    @FXML private Label yearWarningLabel;
    @FXML private Label currentWarningLabel;
    @FXML private Label connectorWarningLabel;
    @FXML private TextField registrationTextBox;
    @FXML private TextField yearTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    private static final String WARNINGCOLOUR = "-fx-background-color: red, #efefef";
    private static final String ERRORTEXTBOXSTYLE = "-fx-background-color: #b9b9b9, #efefef";
    private String chargerTypeChoice;
    private String connectorTypeChoice;
    private VehicleDAO vehicleDAO;
    private ProfileMainController profileMainController;

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
                profileMainController.getCurrentUser().getId()) != null) {
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
     * Run when the user presses the register vehicle button.
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type.
     */
    @FXML private void registerVehicle() {
        //get information about the vehicles and reset to null values
        clearWarnings();
        boolean valid = isValid();

        if (valid) {
            chargerTypeChoice();
            connectorTypeChoice();
            String registration = registrationTextBox.getText();
            String year = yearTextBox.getText();
            String make = makeTextBox.getText();
            String model = modelTextBox.getText();
            clearTextFields();
            clearWarnings();
            int intYear = Integer.parseInt(year);
            Vehicle newVehicle = new Vehicle(intYear, make, model, chargerTypeChoice,
                    registration, connectorTypeChoice);
            successLabel.setText("Successfully registered a vehicle");
            // Send vehicle to database
            try {
                vehicleDAO.setVehicle(newVehicle, profileMainController.getCurrentUser());
                profileMainController.setVehicle();
                profileMainController.getProfileController().makeButtonsVisible();
                profileMainController.populateVehicleTable();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }

    private void clearWarnings() {
        regWarningLabel.setText("");
        makeWarningLabel.setText("");
        modelWarningLabel.setText("");
        yearWarningLabel.setText("");
        currentWarningLabel.setText("");
        connectorWarningLabel.setText("");
    }

    private void clearTextFields() {
        registrationTextBox.setText("");
        yearTextBox.setText("");
        makeTextBox.setText("");
        modelTextBox.setText("");
        chargerBox.setValue("");
        connectorBox.setValue("");
    }



    /**
     * Function run when charger combo box choice is changed.
     * Used to set the value that is stored.
     */
    @FXML private void chargerTypeChoice() {
        chargerTypeChoice = chargerBox.getValue();
    }

    /**
     * Function run when charger combo box choice is changed.
     * Used to set the value that is stored.
     */
    @FXML private void connectorTypeChoice() {
        connectorTypeChoice = connectorBox.getValue();
    }

    /**
     * Register vehicle controller initialiser.

     * @param profileMainController parent controller
     */
    public void init(ProfileMainController profileMainController) {
        this.profileMainController = profileMainController;
        vehicleDAO = new VehicleDAO();
        chargerBox.setItems(chargerTypeOptions);
        connectorBox.setItems(connectorTypeOptions);

        // Set listeners for text boxes to clear the warnings
        registrationTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            registrationTextBox.setStyle(ERRORTEXTBOXSTYLE);
            regWarningLabel.setText("");
        }));

        makeTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            makeTextBox.setStyle(ERRORTEXTBOXSTYLE);
            makeWarningLabel.setText("");
        }));

        modelTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            modelTextBox.setStyle(ERRORTEXTBOXSTYLE);
            modelWarningLabel.setText("");
        }));

        yearTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            yearTextBox.setStyle(ERRORTEXTBOXSTYLE);
            yearWarningLabel.setText("");
        }));

        chargerBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            chargerBox.setStyle(ERRORTEXTBOXSTYLE);
            currentWarningLabel.setText("");
        }));

        connectorBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            connectorBox.setStyle(ERRORTEXTBOXSTYLE);
            connectorWarningLabel.setText("");
        }));
    }

}
