package journey.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Screen;
import journey.data.DatabaseManager;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import journey.data.UserDAO;
import journey.gui.MainWindow;

import java.io.IOException;


public class LoginController {
    private UserDAO userDAO;
    @FXML private TextField nameTextBox;

    @FXML private void registerUser(ActionEvent actionEvent) {
        String name = getNameTextBox();
        userDAO.setCurrentUser(name);
        //something to switch stages

        System.out.println("Updated User");

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
            stage.setMinHeight(600);
            stage.setMinWidth(900);
            stage.setMaximized(true);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();

            stage.setX(bounds.getMinX());
            stage.setY(bounds.getMinY());
            stage.setWidth(bounds.getWidth());
            stage.setHeight(bounds.getHeight());
            stage.show();
            MainWindow.getStage().close();

        } catch(IOException e) {
            System.out.println("Exception in loading");
            e.printStackTrace();
        }
        actionEvent.consume();
    }
    @FXML private String getNameTextBox() {
        return nameTextBox.getText();
    }

    public void init(Stage stage) {
        userDAO = new UserDAO();
    }
}
