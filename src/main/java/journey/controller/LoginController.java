package journey.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import journey.data.User;

public class LoginController {
    @FXML private TextField nameTextBox;
    @FXML private void registerUser(ActionEvent actionEvent) {
        String name = getRegistrationTextBox();
        User user = new User(1);
        user.setName(name);
        //something to switch stages
        actionEvent.consume();
    }
    @FXML private String getRegistrationTextBox() {
        return nameTextBox.getText();
    }
}
