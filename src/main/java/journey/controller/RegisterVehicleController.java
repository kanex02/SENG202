package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.Utils;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Controller for the registerVehicle FXML allows registering a vehicle and does error checking on the inputs given
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
    private MainController mainController;
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");


    /**
     * Run when the user presses the register vehicle button.
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type.
     */
    @FXML private void registerVehicle() {
        //get information about the vehicles and reset to null values
        boolean valid = true;
        String registration = registrationTextBox.getText();
        String year = yearTextBox.getText();
        String make = makeTextBox.getText();
        String model = modelTextBox.getText();
        chargerTypeChoice();
        connectorTypeChoice();

        //registration validation
        Matcher regHasSpecial = special.matcher(registration);
        if (Objects.equals(registration, "")) {
            regWarningLabel.setText("Please enter a registration");
            valid = false;
        } else if ( regHasSpecial.find() ) {
            regWarningLabel.setText("Cannot contain special characters");
            valid = false;
        } else if (registration.length() > 6) {
            regWarningLabel.setText("Cannot be more than 6 characters");
            valid = false;
        }

        //make validation
        Matcher makeHasDigit = digit.matcher(make);
        Matcher makeHasSpecial = special.matcher(make);
        if (makeHasSpecial.find() || makeHasDigit.find()) {
            makeWarningLabel.setText("Cannot contain digits or special characters");
            valid = false;
        } else if (make.equals("")) {
            makeWarningLabel.setText("Please enter a model");
        }

        //model validation
        Matcher modelHasDigit = digit.matcher(model);
        Matcher modelHasSpecial = special.matcher(model);
        if (modelHasSpecial.find() || modelHasDigit.find()) {
            modelWarningLabel.setText("Cannot contain digits or special characters");
            valid = false;
        } else if (model.equals("")) {
            modelWarningLabel.setText("Please enter a model");
        }

        //year validation
        int intYear = 0;
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
        if (chargerTypeChoice.equals("")) {
            currentWarningLabel.setText("Please select a current type");
        }

        //connector validation
        if (connectorTypeChoice.equals("")) {
            connectorWarningLabel.setText("Please select a connector type");
        }

        if (valid) {
            registrationTextBox.setText("");
            yearTextBox.setText("");
            makeTextBox.setText("");
            modelTextBox.setText("");
            chargerBox.setValue("");
            connectorBox.setValue("");
            regWarningLabel.setText("");
            makeWarningLabel.setText("");
            modelWarningLabel.setText("");
            yearWarningLabel.setText("");
            currentWarningLabel.setText("");
            connectorWarningLabel.setText("");
            Vehicle newVehicle = new Vehicle(intYear, make, model, chargerTypeChoice, registration, connectorTypeChoice);
            // Send vehicle to database
            try {
                vehicleDAO.setVehicle(newVehicle, mainController.getCurrentUser());
                mainController.populateVehicleDropdown();
            } catch (Exception e) {
                log.error(e);
            }
        }
    }



    /**
     * Function run when charger combo box choice is changed.
     * Used to set the value that is stored.
     */
    @FXML private void chargerTypeChoice() {
        chargerTypeChoice = chargerBox.getValue();
    }


    @FXML private void connectorTypeChoice() {
        connectorTypeChoice = connectorBox.getValue();
    }

    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        vehicleDAO = new VehicleDAO();
        chargerBox.setItems(chargerTypeOptions);
        connectorBox.setItems(connectorTypeOptions);

    }

}
