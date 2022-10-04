package journey.controller;

import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.QueryResult;
import journey.data.Vehicle;
import journey.repository.VehicleDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * Controller for the profile popup
 */
public class ProfileController {
    private static final Logger log = LogManager.getLogger();
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
        vehicle.setText(profileController.getSelectedVehicle());
    }

    @FXML private void editCurrentVehicle() {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editVehicle.fxml"));
            root = loader.load();

            EditVehicleController editVehicleController = loader.getController();

            Stage editVehicleStage = new Stage(StageStyle.UNDECORATED);
            editVehicleController.init(this);

            editVehicleStage.setTitle("Edit Vehicle");
            Scene scene = new Scene(root);
            editVehicleStage.setScene(scene);
            editVehicleStage.show();
            editVehicleStage.setMinHeight(424);
            editVehicleStage.setMinWidth(371);
        } catch (IOException e) {
            log.error(e);
        }
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

        vehicleTable.getSelectionModel().selectedItemProperty().addListener(((ObservableValue<? extends Vehicle> observable, Vehicle oldVehicle, Vehicle newVehicle) -> {
            if (newVehicle != null) {
                profileController.setSelectedVehicle(newVehicle.getRegistration());
                setVehicle();
            }
        }));
    }

}
