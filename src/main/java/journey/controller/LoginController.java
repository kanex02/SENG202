package journey.controller;

import java.io.IOException;
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
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import journey.data.QueryResult;
import journey.data.User;
import journey.repository.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.LoginService;


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
    @FXML private Label warningLabel;

    /**
     * Register a user and add them to the user database
     * when register button is pressed.
     */
    @FXML private void setRunLater() {
        if (nameChoiceBox.getValue() == null && nameTextBox.getText().equals("")) {
            warningLabel.setText("Please select or enter Username");
        } else if (nameChoiceBox.getValue() == null) {
            Platform.runLater(this::registerUser);
        } else {
            Platform.runLater(this::setUser);
        }
    }

    @FXML private void registerUser() {
        String name = nameTextBox.getText();
        warningLabel.setText("");
        Boolean valid = LoginService.checkUser(name);

        if (!valid) {
            warningLabel.setText("Your name cannot contain any digits or special characters!");
        } else if (name.equals("")) {
            warningLabel.setText("Please enter a name or select from dropdown");
        } else {
            user = userDAO.setCurrentUser(name);
            // Switch stages to main window
            switchToMain();
        }
    }

    private void populateUserDropDown() {
        QueryResult data = userDAO.getUsers();
        ObservableList<String> users = FXCollections.observableArrayList();
        for (User user : data.getUsers()) {
            String newString = user.getName();
            users.add(newString);
        }
        nameChoiceBox.setItems(users);
    }

    private void setUser() {
        String name = (String) nameChoiceBox.getValue();
        user = userDAO.setCurrentUser(name);
        switchToMain();
    }

    private String getNameTextBox() {
        return nameTextBox.getText();
    }

    /**
     * Switches the current screen to the main screen.
     */
    private void switchToMain() {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/newMain.fxml"));
            Parent root = baseLoader.load();
            Stage mainStage = new Stage();

            MainController baseController = baseLoader.getController();
            baseController.init(mainStage, user);

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
        warningLabel.setText("");
        populateUserDropDown();
    }


}
