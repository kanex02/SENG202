package journey.gui;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import javafx.scene.control.ListView;

import java.io.IOException;
import journey.data.Database;
import journey.data.QueryResult;
import journey.data.Station;
import journey.controller.LoginController;

/**
 * Class starts the javaFX application window
 * @author Journey dev team
 */
public class MainWindow extends Application {


    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }

    /**
     * Opens the gui with the fxml content specified in resources/fxml/main
     * @param primaryStage The current fxml stage, handled by javaFX Application class
     * @throws IOException if there is an issue loading fxml file
     */
    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = baseLoader.load();

        LoginController baseController = baseLoader.getController();
        baseController.init(primaryStage);
        stage = primaryStage;
//        FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
//        Parent root = baseLoader.load();
//
//        MainController baseController = baseLoader.getController();
//        baseController.init(primaryStage);

        primaryStage.setTitle("Journey");
        Scene scene = new Scene(root, 600, 400);
        // scene.getStylesheets().add("src/main/resources/gui/style.css");
        primaryStage.setScene(scene);
        primaryStage.show();
        // set the min height and width so the window opens at the correct size
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(900);


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
