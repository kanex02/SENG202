package journey.cucumber.controllerStepDefs;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import journey.Utils;
import journey.controller.LoginController;
import journey.controller.MainController;
import journey.controller.MapController;
import journey.cucumber.testFX.controllertests.TestFXBase;
import journey.gui.MainWindow;
import journey.repository.DatabaseManager;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Predicate;

import static org.testfx.api.FxAssert.verifyThat;

public class LoginStepDefs extends TestFXBase {

    @Before
    @Override
    public void setUpClass() throws Exception {
        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().connect();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE name = 'uniqueUsername'")) {
                ps.execute();
            }
        } finally {
            Utils.closeConn(conn);
        }
        ApplicationTest.launch(MainWindow.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent page = loginLoader.load();
        LoginController loginController = loginLoader.getController();
        loginController.init(stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    @Given("I am on the login screen")
    public void iAmOnTheLoginScreen() {
        // We know that only the login screen has a login button, so if we can see we can't've logged in
        verifyThat("#registerButton", Node::isVisible);
    }

    @When("I register with username {string}")
    public void iLoginWithUsernameAndPassword(String username) {
        clickOn("#nameTextBox");
        write(username);
        clickOn("#registerButton");
    }

    @Then("I am logged in successfully")
    public void iAmLoggedInSuccessfully() {
        // Main pane is on the main window, so the user is successfully registered and logged in
        verifyThat("#mainPane", Node::isVisible);
    }

    @Then("I am not logged in")
    public void iAmNotLoggedIn() {
        // The register button is on the login page, which would close on a successful login
        verifyThat("#registerButton", Node::isVisible);
    }
}
