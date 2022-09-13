package journey.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import journey.data.User;
import journey.data.Database;

public class LoginController {
    @FXML private TextField nameTextBox;
    @FXML private void registerUser(ActionEvent actionEvent) {
        String name = getNameTextBox();
        User user = new User(name);
        Database.setCurrentUser(user);
        //something to switch stages
        actionEvent.consume();
    }
    @FXML private String getNameTextBox() {
        return nameTextBox.getText();
    }
}
