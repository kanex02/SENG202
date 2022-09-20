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
import journey.repository.DatabaseManager;
import journey.repository.UserDAO;
import journey.repository.VehicleDAO;

/**
 * Controller for the profile popup
 */
public class ProfileController {
    private MainController mainController;
    private VehicleDAO vehicleDAO;
    @FXML private Button closeButton;
    @FXML private Label name;
    private static Station selectedVehicle = null;
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
     * @param stage current stage
     */
    public void setName(Stage stage) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        name.setText(mainController.getCurrentUser().getName());
    }

    /**
     *Retrieves the vehicles from the database and puts their information into the table
     * @param stage current stage
     */
    public void setVehicles(Stage stage) {
        registrationCol.setCellValueFactory(new PropertyValueFactory<>("Registration"));
        makeCol.setCellValueFactory(new PropertyValueFactory<>("Make"));
        modelCol.setCellValueFactory(new PropertyValueFactory<>("Model"));
        yearCol.setCellValueFactory(new PropertyValueFactory<>("Year"));
        chargerTypeCol.setCellValueFactory(new PropertyValueFactory<>("ChargerType"));
        QueryResult data = vehicleDAO.getVehicles(mainController.getCurrentUser());
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(data.getVehicles());
        vehicleTable.setItems(vehicles);
    }

    /**
     * Initialises the profile popup with User's registered vehicles in a table view.
     * @param stage current stage
     */
    public void init(Stage stage, MainController mainController) {
        this.mainController = mainController;
        vehicleDAO = new VehicleDAO();
        setName(stage);
        setVehicles(stage);
    }

}
