package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import journey.data.*;
import journey.repository.UserDAO;
import journey.repository.VehicleDAO;

/**
 * Controller for the profile popup
 */
public class ProfileController {
    private UserDAO userDAO;
    private VehicleDAO vehicleDAO;
    @FXML private Button closeButton;
    @FXML private Label name;
    @FXML private TableColumn<Vehicle, String> registrationCol;

    @FXML private TableColumn<Vehicle, String> makeCol;

    @FXML private TableColumn<Vehicle, String> modelCol;

    @FXML private TableColumn<Vehicle, Integer> yearCol;

    @FXML private TableColumn<Vehicle, String> chargerTypeCol;

    @FXML private TableView<Vehicle> vehicleTable;

    /**
     * closes the scene when the close button is pushed
     */
    public void closeScene() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }

    /**
     * Sets the text field to the name of the current user in the profile box
     */
    public void setName() {
        name.setText(userDAO.getCurrentUser().getName());
    }

    /**
     *Retrieves the vehicles from the database and puts their information into the table
     */
    public void setVehicles() {
        registrationCol.setCellValueFactory(new PropertyValueFactory<>("Registration"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("Make"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("Model"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        chargerTypeCol.setCellValueFactory(new PropertyValueFactory<>("ChargerType"));
        QueryResult data = vehicleDAO.getVehicles();
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(data.getVehicles());
        vehicleTable.setItems(vehicles);
    }

    /**
     * Initialises the profile popup with User's registered vehicles in a table view.
     */
    public void init() {
        userDAO = new UserDAO();
        vehicleDAO = new VehicleDAO();
        setName();
        setVehicles();
    }

}