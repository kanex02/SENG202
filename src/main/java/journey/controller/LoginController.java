package journey.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import journey.data.User;
import journey.repository.UserDAO;
import journey.service.LoginService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Objects;


/**
 * FXML controller class for the login window.
 * This is a basic window that allows a user to register/login to existing account
 */
public class LoginController {
    private static final Logger log = LogManager.getLogger();
    private UserDAO userDAO;
    private Stage stage;
    private User user;
    @FXML private AnchorPane wholeScene;
    @FXML private TextField nameTextBox;
    @FXML private ChoiceBox<String> nameChoiceBox;
    @FXML private Label loginWarningLabel;
    @FXML private Label registerWarningLabel;
    @FXML private ImageView journeyIcon;

    /**
     * Register a user and add them to the user database
     * when register button is pressed.
     */
    @FXML private void setRunLater() {
        if ((nameChoiceBox.getValue() == null || nameChoiceBox.getValue().equals("")) && nameTextBox.getText().equals("")) {
            loginWarningLabel.setText("Please select or enter Username");
        } else if (nameChoiceBox.getValue() == null || nameChoiceBox.getValue().equals("")) {
            Platform.runLater(this::register);
        } else {
            Platform.runLater(this::login);
        }
    }

    /**
     * Adds all users to the user drop-down to be logged into.
     */
    private void populateUserDropDown() {
        User[] data = userDAO.getUsers();
        ObservableList<String> users = FXCollections.observableArrayList();
        for (User user : data) {
            String newString = user.getName();
            users.add(newString);
        }
        users.add("");
        nameChoiceBox.setItems(users);
    }

    /**
     * Switch from the login page to the main page,
     * if register button is clicked and nameTextBox field is valid.
     * Sets warning label if not valid.
     */
    @FXML public void register() {

        String name = nameTextBox.getText();
        registerWarningLabel.setText("");
        Boolean valid = LoginService.checkUser(name);
        if (!valid) {
            registerWarningLabel.setText("Your name cannot contain any digits or special characters!");
        } else if (name.equals("")) {
            registerWarningLabel.setText("Please enter a name or select from dropdown");
        } else if (userDAO.nameInDB(name)) {
            registerWarningLabel.setText("A user with that name already exists!");
        } else if (name.length() > 15) {
            registerWarningLabel.setText("Your name cannot be longer than 15 characters!");
        } else {
            user = userDAO.setCurrentUser(name);
            stage.setTitle("Loading...");
            Platform.runLater(this::switchToMain);
        }
    }

    /**
     * Logs in user and switch to main page if a user is selected.
     * Sets warning label if user not selected and login button pressed.
     */
    @FXML public void login() {
        if (!(nameChoiceBox.getValue() == null || nameChoiceBox.getValue().equals(""))) {
            String name = nameChoiceBox.getValue();
            user = userDAO.setCurrentUser(name);
            stage.setTitle("Loading...");
            Platform.runLater(this::switchToMain);
        } else {
            loginWarningLabel.setText("Please select from dropdown or register new user");
        }
    }


    /**
     * Switches the current screen to the main screen.
     */
    private void switchToMain() {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = baseLoader.load();
            Stage mainStage = new Stage();

            MainController baseController = baseLoader.getController();
            baseController.init(mainStage, user, null);

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
        } catch (IOException e) {
            log.error(e);
        }

    }

    void initImages() {
        Image img = new Image(
                new BufferedInputStream(
                        Objects.requireNonNull(getClass().getResourceAsStream("/images/Journey_Logo.jpeg"))
                ));
        journeyIcon.setImage(img);
    }
    /**
     * initialises the login window.

     * @param stage stage to load
     */
    public void init(Stage stage) {
        this.stage = stage;
        userDAO = new UserDAO();
        //This must use Platform.runLater or else we get a core dump.
        wholeScene.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                setRunLater();
            }
        });
        loginWarningLabel.setText("");
        initImages();
        populateUserDropDown();
    }


}
