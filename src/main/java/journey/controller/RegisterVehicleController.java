package journey.controller;

import java.util.Objects;
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



    private String chargerTypeChoice;
    private String connectorTypeChoice;
    private VehicleDAO vehicleDAO;
    private ProfileMainController profileMainController;

    /**
     * Error checking for entering a vehicle.

     * @return whether result passed error checking or not (true/false).
     */
    public boolean isValid() {
        boolean valid = true;
        String registration = registrationTextBox.getText();
        chargerTypeChoice();
        connectorTypeChoice();

        //registration validation
        if (Objects.equals(registration, "")) {
            regWarningLabel.setText("Please enter a registration");
            registrationTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (!registration.matches(Utils.getCharacterDigit())) {
            regWarningLabel.setText("Cannot contain special characters");
            registrationTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (registration.length() > 6) {
            regWarningLabel.setText("Cannot be more than 6 characters");
            registrationTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (vehicleDAO.queryVehicle(registration, profileMainController.getCurrentUser().getId()) != null) {
            regWarningLabel.setText("A vehicle with this registration already exists for this user!");
            registrationTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        }

        String make = makeTextBox.getText();
        //make validation
        if (!make.matches(Utils.getCharacterOnly())) {
            makeWarningLabel.setText("Cannot contain digits or special characters");
            makeTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (make.equals("")) {
            makeWarningLabel.setText("Please enter a model");
            makeTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (make.length() > 20) {
            makeWarningLabel.setText("Make cannot be more than 20 characters long");
            makeTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        }

        String model = modelTextBox.getText();
        //model validation
        if (!model.matches(Utils.getCharacterDigit())) {
            modelWarningLabel.setText("Cannot contain special characters");
            modelTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (model.equals("")) {
            modelWarningLabel.setText("Please enter a model");
            modelTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else if (model.length() > 20) {
            modelWarningLabel.setText("Model cannot be more than 20 characters long");
            modelTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        }

        String year = yearTextBox.getText();
        //year validation
        int intYear;
        if (year.equals("")) {
            yearWarningLabel.setText("Please enter a year");
            yearTextBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        } else {
            if (Utils.isInt(year)) {
                intYear = Integer.parseInt(year);
                String date = Utils.getDate();
                int currentYear = Integer.parseInt(date.split("/")[2]);
                if (intYear > currentYear || intYear < 1996) {
                    yearWarningLabel.setText("Year is out of range");
                    yearTextBox.setStyle("-fx-background-color: red, #efefef");
                    valid = false;
                }
            } else {
                yearWarningLabel.setText("Year must be an integer");
                yearTextBox.setStyle("-fx-background-color: red, #efefef");
                valid = false;
            }
        }

        //current validation
        if (chargerTypeChoice == null || chargerTypeChoice.equals("")) {
            currentWarningLabel.setText("Please select a current type");
            chargerBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        }

        //connector validation
        if (connectorTypeChoice == null || connectorTypeChoice.equals("")) {
            connectorWarningLabel.setText("Please select a connector type");
            connectorBox.setStyle("-fx-background-color: red, #efefef");
            valid = false;
        }
        successLabel.setText("");
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

    public void clearWarnings() {
        regWarningLabel.setText("");
        makeWarningLabel.setText("");
        modelWarningLabel.setText("");
        yearWarningLabel.setText("");
        currentWarningLabel.setText("");
        connectorWarningLabel.setText("");
    }

    public void clearTextFields() {
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
            registrationTextBox.setStyle("-fx-background-color: #b9b9b9, #efefef");
            regWarningLabel.setText("");
        }));

        makeTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            makeTextBox.setStyle("-fx-background-color: #b9b9b9, #efefef");
            makeWarningLabel.setText("");
        }));

        modelTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            modelTextBox.setStyle("-fx-background-color: #b9b9b9, #efefef");
            modelWarningLabel.setText("");
        }));

        yearTextBox.textProperty().addListener(((observableValue, s, t1) -> {
            yearTextBox.setStyle("-fx-background-color: #b9b9b9, #efefef");
            yearWarningLabel.setText("");
        }));

        chargerBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            chargerBox.setStyle("-fx-background-color: #b9b9b9, #efefef");
            currentWarningLabel.setText("");
        }));

        connectorBox.getSelectionModel().selectedItemProperty().addListener(((observableValue, s, t1) -> {
            connectorBox.setStyle("-fx-background-color: #b9b9b9, #efefef");
            connectorWarningLabel.setText("");
        }));
    }

}
