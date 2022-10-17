package journey.cucumber.controllerStepDefs;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.concurrent.TimeoutException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;
import journey.Utils;
import journey.controller.MainController;
import journey.controller.ProfileController;
import journey.controller.ProfileMainController;
import journey.controller.RegisterVehicleController;
import journey.data.Vehicle;
import journey.repository.UserDAO;
import journey.data.User;
import journey.cucumber.testFX.controllertests.TestFXBase;
import journey.gui.MainWindow;
import journey.repository.DatabaseManager;
import org.junit.jupiter.api.Assertions;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;

public class ProfileStepDefs extends TestFXBase {

    User user;
    UserDAO userDAO;
    private String registration;
    private String make;
    private String model;
    private int year;
    private String chargerType;
    private String connectorType;
    private Vehicle vehicle;

    @Before
    @Override
    public void setUpClass() throws Exception {

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().connect();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE name = 'Tester' or name = 'usersName'")) {
                ps.execute();
            }
        } finally {
            Utils.closeConn(conn);
        }
        ApplicationTest.launch(MainWindow.class);

        UserDAO userDAO = new UserDAO();
        User user = userDAO.setCurrentUser("Tester");

    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/fxml/main.fxml"));
        FXMLLoader mainProfileLoader = new FXMLLoader(getClass().getResource("/fxml/myProfile.fxml"));
        FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
        FXMLLoader registerLoader = new FXMLLoader(getClass().getResource("/fxml/registerVehicle.fxml"));

        Parent page = mainLoader.load();
        ProfileController profileController = profileLoader.getController();
        ProfileMainController profileMainController = mainProfileLoader.getController();
        MainController mainController = mainLoader.getController();
        RegisterVehicleController registerVehicleController = registerLoader.getController();

        profileMainController.setCurrentUser(user);

        mainController.init(stage, user);

        profileController.init(profileMainController);
        registerVehicleController.init(profileMainController);

        Scene scene = new Scene(page);
        stage.setScene(scene);
        stage.show();
    }

    @After
    public void afterEachTest() throws TimeoutException {
        try {
            FxToolkit.cleanupStages();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        Connection conn = null;
        try {
            conn = DatabaseManager.getInstance().connect();
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM Users WHERE name = 'Tester'")) {
                ps.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            Utils.closeConn(conn);
        }

        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Given("I am logged in with username {string}")
    public void iAmLoggedIn(String username) {
        // We know that only the login screen has a login button, so if we can see we can't've logged in
        verifyThat("#registerButton", Node::isVisible);

        clickOn("#nameTextBox");
        write(username);
        clickOn("#registerButton");

        // Main pane is on the main window, so the user is successfully registered and logged in
        verifyThat("#mainPane", Node::isVisible);
    }

    @Given("I am on the main page")
    public void iAmOnTheMainPage() {
        // Main pane is on the main window, so the user is successfully registered and logged in
        verifyThat("#currentVehicle", Node::isVisible);
    }

    @When("I click on the my profile button")
    public void iClickOnTheMyProfileButton() {
        clickOn("#myProfileButton");
    }


    @When("I enter the details of the vehicle registration {string} make {string} model {string} " +
            "year {string} charger {string} connector {string}")
    public void iEnterDetailsOfAVehicle(String registration, String make, String model, String year, String charger, String connector) {
        this.registration = registration;
        this.make = make;
        this.model = model;
        this.year = Integer.parseInt(year);
        this.chargerType = charger;
        this.connectorType = connector;

        this.vehicle = new Vehicle(this.year, this.make, this.model, this.chargerType, this.registration, this.connectorType);

    }

    @Then("The vehicle is not registered")
    public void theVehicleIsNotRegistered() {
        verifyThat("#connectorWarningLabel", Node::isVisible);
    }

    @Then("The vehicle is registered")
    public void theVehicleIsRegistered() {
        Assertions.assertNotNull(vehicle);
    }

    @Then("I am shown my profile")
    public void iAmShownMyProfile() {
        verifyThat("#vehicleTable", Node::isVisible);
    }
}