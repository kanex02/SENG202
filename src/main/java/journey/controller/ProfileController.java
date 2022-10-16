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
 * Controller for the profile popup.
 */
public class ProfileController {
    private ProfileMainController profileMainController;
    private VehicleDAO vehicleDAO;
    @FXML private Label name;
    @FXML private Label vehicle;
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
    @FXML private Button goBackButton;

    private boolean editing = false;
    private boolean confirming = true;
    private final UserDAO userDAO = new UserDAO();


    public ProfileMainController getProfileMainController() {
        return profileMainController;
    }

    /**
     * Sets the text field to the name of the current user in the profile box.
     */
    private void setName() {
        name.setText(profileMainController.getCurrentUser().getName());
    }

    /**
     * Sets the text field to the registration of the currently used vehicle.
     */
    public void setVehicle() {
        Vehicle v = vehicleDAO.getSelectedVehicle(profileMainController.getCurrentUser());
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
            int id = profileMainController.getCurrentUser().getId();

            if (Boolean.FALSE.equals(LoginService.checkUser(newName))) {
                nameWarning.setText("Name cannot contain digits or special characters");
            } else if (userDAO.nameInDB(newName) && !newName.equals(profileMainController.getCurrentUser().getName())) {
                nameWarning.setText("This name is already in use, please pick another!");
            } else if (newName.length() > 20) {
                nameWarning.setText("Name is too long, must be less than 20 characters");
            } else {
                editNameButton.setText("Edit");
                revertNameChanges.setVisible(false);
                editName.setVisible(false);
                if (!newName.equals(profileMainController.getCurrentUser().getName())) {
                    userDAO.updateUserName(id, newName);
                }
                profileMainController.setCurrentUser(userDAO.getUser(id));
                setName();
                nameWarning.setText("");
                editing = false;
            }
        } else { //edit the current name
            editName.setText(profileMainController.getCurrentUser().getName());
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
        editName.setText(profileMainController.getCurrentUser().getName());
        editNameButton.setText("Edit");
        revertNameChanges.setVisible(false);
        editName.setVisible(false);
        editing = false;
    }

    /**
     * Check there is a currently selected vehicle then sets that
     * into the register vehicle text boxes.
     */
    @FXML private void editCurrentVehicle() {
        profileMainController.loadEditVehicle();
    }

    /**
     * Delete the currently selected vehicle from the database.
     */
    @FXML private void deleteCurrentVehicle() {
        if (confirming) {
            deleteCurrentVehicle.setText("Confirm delete?");
            goBackButton.setVisible(true);
            confirming = false;
        } else {
            String reg = vehicleDAO.getSelectedVehicle(profileMainController.getCurrentUser()).getRegistration();
            vehicleDAO.removeVehicle(reg, profileMainController.getCurrentUser().getId());
            setVehicles();
            setVehicle();
            makeButtonsInvisible();
            goBackButton();
            deleteCurrentVehicle.setText("Delete");
            confirming = true;
        }
    }

    /**
     * Undo the delete call instead of confirming delete
     */
    @FXML private void goBackButton() {
        if (!confirming) {
            deleteCurrentVehicle.setText("Delete");
            goBackButton.setVisible(false);
            confirming = true;
        }
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
        Vehicle[] data = vehicleDAO.getVehicles(profileMainController.getCurrentUser());
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(data);
        vehicleTable.setItems(vehicles);
    }

    public void makeButtonsVisible() {
        editCurrentVehicle.setVisible(true);
        deleteCurrentVehicle.setVisible(true);
    }

    private void makeButtonsInvisible() {
        editCurrentVehicle.setVisible(false);
        deleteCurrentVehicle.setVisible(false);
    }



    /**
     * Initialises the profile popup with User's registered vehicles in a table view.
     */
    public void init(ProfileMainController profileController) {
        this.profileMainController = profileController;
        vehicleDAO = new VehicleDAO();
        editName.setVisible(false);
        revertNameChanges.setVisible(false);
        setName();
        setVehicles();
        setVehicle();
        if (vehicleDAO.getSelectedVehicle(profileController.getCurrentUser()) == null) {
            makeButtonsInvisible();
        }
        goBackButton.setVisible(false);

        vehicleTable.getSelectionModel().selectedItemProperty().addListener(
                ((ObservableValue<? extends Vehicle> observable, Vehicle oldVehicle, Vehicle newVehicle) -> {
                if (newVehicle != null) {
                    Vehicle old = vehicleDAO.getSelectedVehicle(profileController.getCurrentUser());
                    if (old != null) {
                        if (!newVehicle.getRegistration().equals(vehicleDAO.getSelectedVehicle(
                                profileController.getCurrentUser()).getRegistration())) {
                            vehicleDAO.changeSelectedVehicle(profileController.getCurrentUser(),
                                    newVehicle.getRegistration());
                            setVehicle();
                        }
                    }  else {
                        vehicleDAO.changeSelectedVehicle(profileController.getCurrentUser(),
                                newVehicle.getRegistration());
                        setVehicle();
                        makeButtonsVisible();
                    }
                }
            }));
    }
}
