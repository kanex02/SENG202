package journey.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import journey.Utils;
import journey.data.Vehicle;
import journey.repository.UserDAO;
import journey.repository.VehicleDAO;
import journey.service.LoginService;
import org.apache.logging.log4j.core.util.JsonUtils;

/**
 * Controller for the profile popup
 */
public class ProfileController {
    private MyProfileController profileController;
    private VehicleDAO vehicleDAO;
    @FXML private Label name;
    @FXML private Label vehicle;
    @FXML private Label warningLabel;
    @FXML private TextField editName;
    @FXML private Button editNameButton;
    @FXML private Button revertNameChanges;
    @FXML private TableColumn<Vehicle, String> registrationCol;
    @FXML private Label nameWarning;
    @FXML private TableColumn<Vehicle, String> makeCol;

    @FXML private TableColumn<Vehicle, String> modelCol;

    @FXML private TableColumn<Vehicle, Integer> yearCol;

    @FXML private TableColumn<Vehicle, String> chargerTypeCol;
    @FXML private TableColumn<Vehicle, String> connectorTypeCol;

    @FXML private TableView<Vehicle> vehicleTable;
    private boolean editing = false;
    private UserDAO userDAO = new UserDAO();

    public MyProfileController getMyProfileController() {
        return profileController;
    }

    /**
     * Sets the text field to the name of the current user in the profile box
     */
    public void setName() {
        name.setText(profileController.getCurrentUser().getName());
    }

    /**
     * Sets the text field to the registration of the currently used vehicle
     */
    public void setVehicle() {
        String reg = profileController.getSelectedVehicle();
        if (reg == null) {
            vehicle.setText("");
        } else {
            vehicle.setText(profileController.getSelectedVehicle());
        }
    }

    @FXML private void editName() {
        if (editing) { //save the new name
            String newName = editName.getText();
            int id = profileController.getCurrentUser().getId();

            if (!LoginService.checkUser(newName)) {
                nameWarning.setText("Name cannot contain digits or special characters");
            } else if (userDAO.nameInDB(newName) && !newName.equals(profileController.getCurrentUser().getName())) {
                nameWarning.setText("This name is already in use, please pick another!");
            } else if (newName.length() > 20) {
                nameWarning.setText("Name is too long, must be less than 20 characters");
            } else {
                editNameButton.setText("Edit");
                revertNameChanges.setVisible(false);
                editName.setVisible(false);
                if (!newName.equals(profileController.getCurrentUser().getName())) {
                    userDAO.updateUserName(id, newName);
                }
                profileController.setCurrentUser(userDAO.getUser(id));
                setName();
                nameWarning.setText("");
                editing = false;
            }
        } else { //edit the current name
            editName.setText(profileController.getCurrentUser().getName());
            editNameButton.setText("Save");
            revertNameChanges.setVisible(true);
            editName.setVisible(true);
            editing = true;
        }
    }

    @FXML private void revertNameChanges() {
        editName.setText(profileController.getCurrentUser().getName());
    }

    /**
     * Check there is a currently selected vehicle then sets that
     * into the register vehicle text boxes.
     */
    @FXML private void editCurrentVehicle() {
        if (profileController.getSelectedVehicle() == null) {
            warningLabel.setText("You haven't selected a vehicle");
        } else {
            profileController.loadEditVehicle();
        }
    }

    /**
     * Delete the currently selected vehicle from the database.
     */
    @FXML public void deleteCurrentVehicle() {
        String reg = profileController.getSelectedVehicle();
        vehicleDAO.removeVehicle(reg, profileController.getCurrentUser().getId());
        profileController.setSelectedVehicle(null);
    }



    /**
     * Retrieves the vehicles from the database and puts their information into the table.
     */
    public void setVehicles() {
        registrationCol.setCellValueFactory(new PropertyValueFactory<>("Registration"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("Make"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("Model"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        chargerTypeCol.setCellValueFactory(new PropertyValueFactory<>("ChargerType"));
        connectorTypeCol.setCellValueFactory(new PropertyValueFactory<>("ConnectorType"));
        Vehicle[] data = vehicleDAO.getVehicles(profileController.getCurrentUser());
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(data);
        vehicleTable.setItems(vehicles);
    }

    /**
     * Initialises the profile popup with User's registered vehicles in a table view.

     * @param stage current stage
     */
    public void init(Stage stage, MyProfileController profileController) {
        this.profileController = profileController;
        vehicleDAO = new VehicleDAO();
        editName.setVisible(false);
        revertNameChanges.setVisible(false);
        setName();
        setVehicles();
        setVehicle();

        vehicleTable.getSelectionModel().selectedItemProperty().addListener(((ObservableValue<? extends Vehicle> observable, Vehicle oldVehicle, Vehicle newVehicle) -> {
            if (newVehicle != null) {
                profileController.setSelectedVehicle(newVehicle.getRegistration());
                setVehicle();
            }
        }));
    }

}
