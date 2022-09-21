package journey.cucumber;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import journey.controller.ProfileController;
import journey.data.QueryResult;
import journey.data.User;
import journey.data.Vehicle;
import journey.repository.UserDAO;
import journey.repository.VehicleDAO;
import org.testfx.framework.junit5.ApplicationTest;
import journey.gui.MainWindow;
import journey.controller.MainController;
import journey.controller.LoginController;
import journey.controller.ProfileController;
import org.testfx.matcher.control.TextInputControlMatchers;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.testfx.api.FxAssert.verifyThat;



public class LoginStepDefs extends TestFXBase {

    @Before
    @Override
    public void setUpClass() throws Exception {
        ApplicationTest.launch(MainWindow.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent page = loader.load();
        initState(loader, stage);
        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    public void initState(FXMLLoader loader, Stage stage) {
        LoginController loginController = loader.getController();
        loginController.init(stage);
    }

    @Given("I am on the login page")
    public void userOnLogin() {
        verifyThat("#nameTextBox", Node::isVisible);
    }

    @When("I enter a name")
    public void userEntersName() {
        clickOn("#nameTextBox");
        write("Name");
        clickOn("#registerButton");
    }

    @Then("I login and my account is created")
    public void userLoggedIn() {
        verifyThat("#myProfileButton", Node::isVisible);
        UserDAO userDAO = new UserDAO();
        User currentUser = userDAO.getCurrentUser();
        assertEquals(currentUser.getName(), "Name");
    }

    @Given("I am logged in on the main page")
    public void userOnMain() {
        clickOn("#nameTextBox");
        write("Name");
        clickOn("#registerButton");
    }

    @When("I click on the profile button")
    public void userOpensProfile() {
        clickOn("#myProfileButton");
    }

    @Then("My user profile is displayed")
    public void userOnProfile() {
        verifyThat("#name", Node::isVisible);
    }

    @When("I am logged in on the main page with a vehicle registered")
    public void userHasVehicle() {
        userEntersName();
        clickOn("#registrationTextBox");
        write("ABC123");
        clickOn("#makeTextBox");
        write("TestMake");
        clickOn("#modelTextBox");
        write("TestModel");
        clickOn("#yearTextBox");
        write("2022");
        clickOn("#chargerBox");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#registerVehicleButton");
    }

    @Then("My user profile is displayed with the vehicle")
    public void userVehicleProfile() {
        clickOn("#myProfileButton");
        verifyThat("#vehicleTable", Node::hasProperties);
    }

    @When("I enter an invalid year")
    public void invalidYear() {
        clickOn("#registrationTextBox");
        write("abc123");
        clickOn("#makeTextBox");
        write("test");
        clickOn("#modelTextBox");
        write("test");
        clickOn("#yearTextBox");
        write("not a year");
        clickOn("#chargerBox");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        clickOn("#registerVehicleButton");
    }

    @Then("The vehicle isn't saved and the year error message is displayed")
    public void yearError() {
        verifyThat("#warningLabel", Node::hasProperties);
        UserDAO userDAO = new UserDAO();
        User currentUser = userDAO.getCurrentUser();
        VehicleDAO vehicleDAO = new VehicleDAO();
        QueryResult vehicles = vehicleDAO.getVehicles(currentUser);
        assertNotEquals(String.valueOf(vehicles.getVehicles()[vehicles.getVehicles().length-1].getYear()), "not a year");
    }
}
