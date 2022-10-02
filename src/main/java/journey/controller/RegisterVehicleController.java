package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.data.Utils;
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
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private Label warningLabel;
    @FXML private TextField registrationTextBox;
    @FXML private TextField yearTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    private VehicleDAO vehicleDAO;
    private MainController mainController;
    private String chargerTypeChoice;
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

    /**
     * Run when the user presses the register vehicle button.
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type.

     * @param event register vehicle button pressed
     */
    @FXML
    private void registerVehicle(Event event) {
        //get information about the vehicles and reset to null values
        boolean valid = true;
        String registration = registrationTextBox.getText();
        String year = yearTextBox.getText();
        String make = makeTextBox.getText();
        String model = modelTextBox.getText();
        chargerTypeChoice();

        if (Objects.equals(year, "") || Objects.equals(registration, "")
                || Objects.equals(make, "") || Objects.equals(model, "")
                || Objects.equals(chargerTypeChoice, "")) {
            warningLabel.setText("Fill all fields");
            valid = false;
        }

        int intYear = 0;
        if (Utils.isInt(year)) {
            intYear = Integer.parseInt(year);
            String date = Utils.getDate();
            int currentYear = Integer.parseInt(date.split("/")[2]);
            if (intYear > currentYear || intYear < 1996) {
                warningLabel.setText("Year is out of range");
                valid = false;
            }
        } else {
            warningLabel.setText("Year must be an integer");
            valid = false;
        }

        Matcher makeHasDigit = digit.matcher(make);
        Matcher makeHasSpecial = special.matcher(make);
        Matcher modelHasDigit = digit.matcher(model);
        Matcher modelHasSpecial = special.matcher(model);

        if (makeHasSpecial.find() || makeHasDigit.find()) {
            warningLabel.setText("Make entry is invalid. It must only contain characters A-Z.");
            valid = false;
        }

        if (modelHasSpecial.find() || modelHasDigit.find()) {
            warningLabel.setText("Model entry is invalid. It must only contain characters A-Z.");
            valid = false;
        }

        if (valid) {
            registrationTextBox.setText("");
            yearTextBox.setText("");
            makeTextBox.setText("");
            modelTextBox.setText("");
            warningLabel.setText("");
            chargerBox.setValue("");
            Vehicle newVehicle = new Vehicle(intYear, make, model, chargerTypeChoice, registration);
            // Send vehicle to database
            vehicleDAO.setVehicle(newVehicle, mainController.getCurrentUser());
            mainController.updateVehicles();
            event.consume();
        }
    }


    /**
     * Function run when charger combo box choice is changed.
     * Used to set the value that is stored.
     */
    @FXML private void chargerTypeChoice() {
        chargerTypeChoice = chargerBox.getValue();
    }

    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        chargerBox.setItems(chargerTypeOptions);
        vehicleDAO = new VehicleDAO();
    }

}
