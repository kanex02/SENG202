package journey.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import journey.data.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * FXML controller class for the myProfile to be inserted in front of mainPane.
 */
public class ProfileMainController {

    @FXML private BorderPane registerVehicleWrapper;
    @FXML private AnchorPane profileWrapper;

    private static final Logger log = LogManager.getLogger();

    @FXML private javafx.scene.image.ImageView journeyImage;
    @FXML private javafx.scene.image.ImageView profileImage;
    @FXML private javafx.scene.image.ImageView homeImage;
    @FXML private javafx.scene.image.ImageView logoutImage;
    private MainController mainController;
    private ProfileController profileController = null;
    private Stage stage;
    private BorderPane mainPane;
    private User currentUser;

    /**
     * Insert register vehicle FXML into the scene.
     */
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

    public void setVehicle() {
        profileController.setVehicle();
        profileController.setVehicles();
    }

    /**
     * Insert edit vehicle FXML into the scene.
     */
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

    /**
     * Redirects user back to log in screen when logout button pressed.
     */
    @FXML private void logoutButton() {
        try {
            FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = loginLoader.load();
            LoginController loginController = loginLoader.getController();
            Stage loginStage = new Stage();
            loginController.init(loginStage);
            loginStage.setTitle("Journey");
            Scene scene = new Scene(root, 800, 500);
            loginStage.setScene(scene);
            loginStage.setMaxHeight(500);
            loginStage.setMaxWidth(800);
            loginStage.setMaximized(false);
            loginStage.setResizable(false);
            loginStage.show();
            // set the min height and width so the window opens at the correct size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            loginStage.setX((bounds.getWidth() - loginStage.getWidth()) * 1.0f / 2);
            loginStage.setY((bounds.getHeight() - loginStage.getHeight()) * 1.0f / 3);
            this.stage.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Insert view profile FXML into the scene.
     */
    private void viewProfile() {
        try {
            FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            Parent profileParent = profileLoader.load();

            ProfileController profileControl = profileLoader.getController();
            profileControl.init(this);
            this.profileController = profileControl;
            profileWrapper.getChildren().add(profileParent);
            AnchorPane.setTopAnchor(profileParent, 0d);
            AnchorPane.setBottomAnchor(profileParent, 0d);
            AnchorPane.setLeftAnchor(profileParent, 0d);
            AnchorPane.setRightAnchor(profileParent, 0d);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * set all vehicles within the profile controller.
     */
    public void populateVehicleTable() {
        profileController.setVehicles();
    }

    public ProfileController getProfileController() {
        return profileController;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }


    /**
     * Bring the main pane back to the front of the scene.
     */
    @FXML private void homeButton() {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = baseLoader.load();
            MainController baseController = baseLoader.getController();
            baseController.init(stage, mainController.getCurrentUser());
            mainPane.setCenter(root);
            mainPane.toFront();
            mainPane.setVisible(true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Sets the images for the profile screen.
     */
    private void setImages() {
        Image img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/Journey_Logo.jpeg"))
                ));
        (journeyImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/home-svgrepo-com 1.png"))
                ));
        (homeImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/logout.png"))
                ));
        (logoutImage).setImage(img);
        img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/user.png"))
                ));
        (profileImage).setImage(img);
    }
    /**
     * Initialise the myProfile controller.

     * @param mainController main FXML's controller
     * @param stage current stage
     * @param mainPane current main pane
     */
    public void init(MainController mainController, Stage stage, BorderPane mainPane) {
        this.mainController = mainController;
        this.stage = stage;
        this.mainPane = mainPane;
        this.currentUser = mainController.getCurrentUser();
        setImages();
        viewProfile();
        viewRegisterVehicles();
    }

}
