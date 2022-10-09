package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import journey.Utils;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

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
        String reg = profileController.getMyProfileController().getSelectedVehicle();
        currentVehicle = vehicleDAO.queryVehicle(reg, profileController.getMyProfileController().getCurrentUser().getId());
        registrationTextBox.setText(reg);
        makeTextBox.setText(currentVehicle.getMake());
        modelTextBox.setText(currentVehicle.getModel());
        yearTextBox.setText(Integer.toString(currentVehicle.getYear()));
        chargerBox.setValue(currentVehicle.getChargerType());
        connectorBox.setValue(currentVehicle.getConnectorType());
        vehicleDAO.removeVehicle(reg, profileController.getMyProfileController().getCurrentUser().getId());
    }

    public boolean isValid() {
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
        } else if (vehicleDAO.queryVehicle(registration, profileController.getMyProfileController().getCurrentUser().getId()) != null) {
            regWarningLabel.setText("A vehicle with this registration already exists for this user!");
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
            valid = false;
        } else if (make.length() > 20) {
            makeWarningLabel.setText("Make cannot be more than 20 characters long");
            valid = false;
        }

        //model validation
        Matcher modelHasDigit = digit.matcher(model);
        Matcher modelHasSpecial = special.matcher(model);
        if (modelHasSpecial.find() || modelHasDigit.find()) {
            modelWarningLabel.setText("Cannot contain digits or special characters");
            valid = false;
        } else if (model.equals("")) {
            modelWarningLabel.setText("Please enter a model");
            valid = false;
        } else if (model.length() > 20) {
            modelWarningLabel.setText("Model cannot be more than 20 characters long");
            valid = false;
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
        if (chargerTypeChoice == null || chargerTypeChoice.equals("")) {
            currentWarningLabel.setText("Please select a current type");
        }

        //connector validation
        if (connectorTypeChoice == null || connectorTypeChoice.equals("")) {
            connectorWarningLabel.setText("Please select a connector type");
        }

        return valid;
    }

    /**
     * Selete the old version of the vehicle and create a new vehicle in the database with the updated
     * values.
     */
    @FXML public void saveVehicle() {
        if (isValid()) {
            String currentVehicle = profileController.getMyProfileController().getSelectedVehicle();
            vehicleDAO.removeVehicle(currentVehicle, profileController.getMyProfileController().getCurrentUser().getId());
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
            vehicleDAO.setVehicle(newVehicle, profileController.getMyProfileController().getCurrentUser());
            profileController.getMyProfileController().setSelectedVehicle(reg);
            profileController.setVehicle();
            profileController.setVehicles();
            profileController.getMyProfileController().viewRegisterVehicles();
        }
    }


    @FXML public void revertChanges() {
        profileController.getMyProfileController().viewRegisterVehicles();
    }


    /**
     * initialise the stage with the edit vehicle popup.

     * @param myProfileController instance of the main controller
     */
    public void init(MyProfileController myProfileController) {
        this.profileController = myProfileController.getProfileController();
        vehicleDAO = new VehicleDAO();
        chargerBox.setItems(chargerTypeOptions);
        connectorBox.setItems(connectorTypeOptions);
        if (profileController.getMyProfileController().getSelectedVehicle() != null) {
            fillCurrentVehicle();
        }
    }
}
