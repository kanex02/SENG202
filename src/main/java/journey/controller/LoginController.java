package journey.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import journey.repository.UserDAO;
import journey.gui.MainWindow;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * FXML controller class for the login window
 * This is a basic window that allows a user to register/login to existing account
 */
public class LoginController {
    private static final Logger log = LogManager.getLogger();
    private UserDAO userDAO;
    @FXML private TextField nameTextBox;

    /**
     * Register a user and add them to the user database
     * when register button is pressed.
     * @param actionEvent event the register button is pressed
     */
    @FXML private void registerUser(ActionEvent actionEvent) {
        String name = getNameTextBox();
        userDAO.setCurrentUser(name);
        //something to switch stages
        switchToMain();
        actionEvent.consume();
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
            Stage stage = new Stage();

            MainController baseController = baseLoader.getController();
            baseController.init(stage);

            stage.setTitle("Journey");
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);

            // set the min height and width so the window opens at the correct size
            stage.setMinHeight(650);
            stage.setMinWidth(900);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.show();
            MainWindow.getStage().close();
        } catch (IOException e) {
            log.error(e);
        }

    }
    /**
     * intialises the login window
     * @param stage stage to load
     */
    public void init(Stage stage) {
        userDAO = new UserDAO();
    }
}
