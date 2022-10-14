package journey.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import journey.controller.LoginController;

import java.io.IOException;

/**
 * Class starts the javaFX application window.

 * @author Journey dev team
 */
public class MainWindow extends Application {
    private static final Logger log = LogManager.getLogger();

    /**
     * Opens the gui with the fxml content specified in resources/fxml/main.

     * @param primaryStage The current fxml stage, handled by javaFX Application class
     */
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent root = baseLoader.load();
            LoginController baseController = baseLoader.getController();
            baseController.init(primaryStage);
            primaryStage.setTitle("Journey");
            Scene scene = new Scene(root, 800, 500);
            primaryStage.setScene(scene);
            primaryStage.setMaxHeight(500);
            primaryStage.setMaxWidth(800);
            primaryStage.setMaximized(false);
            primaryStage.setResizable(false);
            primaryStage.show();
            // set the min height and width so the window opens at the correct size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            primaryStage.setX((bounds.getWidth() - primaryStage.getWidth()) * 1.0f / 2);
            primaryStage.setY((bounds.getHeight() - primaryStage.getHeight()) * 1.0f / 3);

        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Launches the FXML application, this must be called from another class (in this cass App.java) otherwise JavaFX
     * errors out and does not run

     * @param args command line arguments
     */
    public static void main(String [] args) {
        launch(args);
    }

}
