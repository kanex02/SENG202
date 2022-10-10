package journey.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import journey.data.User;

import java.io.IOException;

public class MyProfileController {

    @FXML private BorderPane registerVehicleWrapper;
    @FXML private AnchorPane profileWrapper;
    @FXML private Button homeButton;

    private static final Logger log = LogManager.getLogger();

    private MainController mainController;
    private ProfileController profileController = null;
    private Stage stage;
    private User currentUser;
    private String selectedVehicle;

    public void viewRegisterVehicles() {
        try {
            FXMLLoader registerVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/registerVehicle.fxml"));
            Parent registerVehicleParent = registerVehicleLoader.load();
            RegisterVehicleController registerVehicleController = registerVehicleLoader.getController();
            registerVehicleController.init(this);
            registerVehicleWrapper.setCenter(registerVehicleParent);
            registerVehicleWrapper.toFront();
            registerVehicleWrapper.setVisible(true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void loadEditVehicle() {
        try {
            FXMLLoader editVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/vehicleEdit.fxml"));
            Parent editVehicleParent = editVehicleLoader.load();
            EditVehicleController editVehicleController = editVehicleLoader.getController();
            editVehicleController.init(this);
            registerVehicleWrapper.setCenter(editVehicleParent);
            registerVehicleWrapper.toFront();
            registerVehicleWrapper.setVisible(true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void viewProfile() {
        try {
            FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent profileParent = profileLoader.load();

            ProfileController profileController = profileLoader.getController();
            this.profileController = profileController;
            profileController.init(stage, this);
            profileWrapper.getChildren().add(profileParent);
            AnchorPane.setTopAnchor(profileParent, 0d);
            AnchorPane.setBottomAnchor(profileParent, 0d);
            AnchorPane.setLeftAnchor(profileParent, 0d);
            AnchorPane.setRightAnchor(profileParent, 0d);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void setSelectedVehicle(String selectedVehicle) {
        this.selectedVehicle = selectedVehicle;
    }

    public String getSelectedVehicle() {
        return selectedVehicle;
    }

    public void populateVehicleTable() {
        profileController.setVehicles();
    }

    public ProfileController getProfileController() {
        return profileController;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void closeScene() {
        Stage stage = (Stage) homeButton.getScene().getWindow();
        stage.close();
    }

    @FXML private void homeButton() {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/newMain.fxml"));
            Parent root = baseLoader.load();
            Stage mainStage = new Stage();

            MainController baseController = baseLoader.getController();
            baseController.init(mainStage, mainController.getCurrentUser(), selectedVehicle);

            mainStage.setTitle("Journey");
            Scene scene = new Scene(root, 600, 400);
            mainStage.setScene(scene);

            // set the min height and width so the window opens at the correct size
            mainStage.setMinHeight(650);
            mainStage.setMinWidth(900);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            mainStage.setX(bounds.getMinX());
            mainStage.setY(bounds.getMinY());
            mainStage.setWidth(bounds.getWidth());
            mainStage.setHeight(bounds.getHeight());
            mainStage.setMaximized(true);
            mainStage.show();
            this.stage.close();
            closeScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void init(MainController mainController, Stage stage, String selectedVehicle) {
        this.mainController = mainController;
        this.stage = stage;
        this.selectedVehicle = selectedVehicle;
        this.currentUser = mainController.getCurrentUser();
        viewProfile();
        viewRegisterVehicles();
    }

}
