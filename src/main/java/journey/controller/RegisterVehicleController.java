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
    private static final String WARNINGCOLOUR = "-fx-background-color: red, #efefef";
    private static final String ERRORTEXTBOXSTYLE = "-fx-background-color: #b9b9b9, #efefef";
    private String chargerTypeChoice;
    private String connectorTypeChoice;
    private VehicleDAO vehicleDAO;
    private ProfileMainController profileMainController;
    private String currentError;
    private String connectorError;
    private String regError;
    private String makeError;
    private String modelError;
    private String yearError;

    /**
     * Display warning labels for invalid input.
     */
    private void displayErrors() {
        Vehicle exists = vehicleDAO.queryVehicle(registrationTextBox.getText(),
                profileMainController.getProfileController().getProfileMainController().getCurrentUser().getId());

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
     * Run when the user presses the register vehicle button.
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type.
     */
    @FXML private void registerVehicle() {
        //get information about the vehicles and reset to null values
        clearWarnings();
        successLabel.setText("");
        displayErrors();
        boolean valid = VehicleService.isValid(regError, makeError, modelError, yearError, currentError, connectorError);
        if (valid) {
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

    /**
     * Clears all the warning labels
     */
    private void clearWarnings() {
        regWarningLabel.setText("");
        makeWarningLabel.setText("");
        modelWarningLabel.setText("");
        yearWarningLabel.setText("");
        currentWarningLabel.setText("");
        connectorWarningLabel.setText("");
    }

    /**
     * Clears all the text fields
     */
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
