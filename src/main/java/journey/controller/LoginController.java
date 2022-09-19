package journey.controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import journey.repository.UserDAO;
import journey.gui.MainWindow;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * FXML controller class for the login window
 * This is a basic window that allows a user to register/login to existing account
 */
public class LoginController {
    private static final Logger log = LogManager.getLogger();
    private UserDAO userDAO;
    private Stage stage;
    @FXML private TextField nameTextBox;
    @FXML private Label warningLabel;

    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

    /**
     * Register a user and add them to the user database
     * when register button is pressed.
     */

    @FXML private void registerUser() {
        String name = getNameTextBox();
        Matcher hasDigit = digit.matcher(name);
        Matcher hasSpecial = special.matcher(name);
        warningLabel.setText("");


        if (hasDigit.find() || hasSpecial.find()) {
            warningLabel.setText("Your name cannot contain any digits or special characters!");
        } else {
            userDAO.setCurrentUser(name);
            //something to switch stages
            switchToMain();
        }
    }

    @FXML private String getNameTextBox() {
        return nameTextBox.getText();
    }

    /**
     * Switches the current screen to the main screen
     */
    private void switchToMain() {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
            Parent root = baseLoader.load();
            Stage mainStage = new Stage();

            MainController baseController = baseLoader.getController();
            baseController.init(mainStage);

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
     * intialises the login window
     * @param stage stage to load
     */
    public void init(Stage stage) {
        this.stage = stage;
        userDAO = new UserDAO();
        //This must use Platform.runLater or else we get a core dump.
        nameTextBox.setOnKeyPressed( event -> {
            if( event.getCode() == KeyCode.ENTER ) {
                Platform.runLater(this::registerUser);
            }
        });
        warningLabel.setText("");
    }
}
