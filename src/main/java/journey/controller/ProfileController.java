package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import journey.data.QueryResult;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;

/**
 * Controller for the profile popup
 */
public class ProfileController {
    private MyProfileController profileController;
    private VehicleDAO vehicleDAO;
    private Stage stage = null;
    @FXML private Label name;
    @FXML private Label vehicle;
    @FXML private TableColumn<Vehicle, String> registrationCol;

    @FXML private TableColumn<Vehicle, String> makeCol;

    @FXML private TableColumn<Vehicle, String> modelCol;

    @FXML private TableColumn<Vehicle, Integer> yearCol;

    @FXML private TableColumn<Vehicle, String> chargerTypeCol;
    @FXML private TableColumn<Vehicle, String> connectorTypeCol;

    @FXML private TableView<Vehicle> vehicleTable;


    public Stage getProfileStage() {
        return stage;
    }

    public ProfileController getProfileController() {
        return this;
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
        vehicle.setText(profileController.getSelectedVehicle());
    }

    @FXML public void editCurrentVehicle() {
        System.out.println("edit");
    }

    @FXML public void deleteCurrentVehicle() {
        System.out.println("delete");
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
        connectorTypeCol.setCellValueFactory(new PropertyValueFactory<>("ConnectorType"));
        QueryResult data = vehicleDAO.getVehicles(profileController.getCurrentUser());
        ObservableList<Vehicle> vehicles = FXCollections.observableArrayList(data.getVehicles());
        vehicleTable.setItems(vehicles);
    }

    /**
     * Initialises the profile popup with User's registered vehicles in a table view.
     * @param stage current stage
     */
    public void init(Stage stage, MyProfileController profileController) {
        this.profileController = profileController;
        vehicleDAO = new VehicleDAO();
        this.stage = stage;
        setName();
        setVehicles();
        setVehicle();

        vehicleTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldVehicle, newVehicle) -> {
            profileController.setSelectedVehicle(newVehicle.getRegistration());
            setVehicle();
        }));
    }

}
