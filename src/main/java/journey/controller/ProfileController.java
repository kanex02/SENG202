package journey.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import journey.data.Vehicle;
import journey.repository.UserDAO;
import journey.repository.VehicleDAO;
import journey.service.LoginService;

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
    @FXML private Button editCurrentVehicle;
    @FXML private Button deleteCurrentVehicle;
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
    private final UserDAO userDAO = new UserDAO();

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
        Vehicle v = vehicleDAO.getSelectedVehicle(profileController.getCurrentUser());
        if (v == null) {
            vehicle.setText("");
        } else {
            vehicle.setText(v.getRegistration());
        }
    }

    /**
     * Allows user to edit their name.
     * (Checks name meets standards through login service).
     */
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

    /**
     * Resets name changes back to before they had been changed.
     */
    @FXML private void revertNameChanges() {
        editName.setText(profileController.getCurrentUser().getName());
    }

    /**
     * Check there is a currently selected vehicle then sets that
     * into the register vehicle text boxes.
     */
    @FXML private void editCurrentVehicle() {
        profileController.loadEditVehicle();
    }

    /**
     * Delete the currently selected vehicle from the database.
     */
    @FXML public void deleteCurrentVehicle() {
        String reg = vehicleDAO.getSelectedVehicle(profileController.getCurrentUser()).getRegistration();
        vehicleDAO.removeVehicle(reg, profileController.getCurrentUser().getId());
        setVehicles();
        setVehicle();
        makeButtonsInvisible();
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

    public void makeButtonsVisible() {
        editCurrentVehicle.setVisible(true);
        deleteCurrentVehicle.setVisible(true);
    }

    public void makeButtonsInvisible() {
        editCurrentVehicle.setVisible(false);
        deleteCurrentVehicle.setVisible(false);
    }

    /**
     * Initialises the profile popup with User's registered vehicles in a table view.
     */
    public void init(MyProfileController profileController) {
        this.profileController = profileController;
        vehicleDAO = new VehicleDAO();
        editName.setVisible(false);
        revertNameChanges.setVisible(false);
        setName();
        setVehicles();
        setVehicle();
        if (vehicleDAO.getSelectedVehicle(profileController.getCurrentUser()) == null) {
            makeButtonsInvisible();
        }

        vehicleTable.getSelectionModel().selectedItemProperty().addListener(((ObservableValue<? extends Vehicle> observable, Vehicle oldVehicle, Vehicle newVehicle) -> {
            if (newVehicle != null) {
                Vehicle old = vehicleDAO.getSelectedVehicle(profileController.getCurrentUser());
                if (old != null) {
                    if (!newVehicle.getRegistration().equals(vehicleDAO.getSelectedVehicle(profileController.getCurrentUser()).getRegistration())) {
                        vehicleDAO.changeSelectedVehicle(profileController.getCurrentUser(), newVehicle.getRegistration());
                        setVehicle();
                    }
                }  else {
                    vehicleDAO.changeSelectedVehicle(profileController.getCurrentUser(), newVehicle.getRegistration());
                    setVehicle();
                    makeButtonsVisible();
                }
            }
        }));
    }

}
