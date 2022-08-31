package journey.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.layout.AnchorPane;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.Database;
import journey.data.QueryResult;
import journey.data.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

/**
 * Controller for the main window
 * @author journey dev team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger();

    private static final ObservableList<String> filterListOptions =
        FXCollections.observableArrayList (
            "Distance",
            "Cost"
        );

    private static final ObservableList<String> chargerTypeOptions =
        FXCollections.observableArrayList (
            "Trickle",
            "AC",
            "DC"
        );

    private static final ObservableList<String> sortListOptions =
        FXCollections.observableArrayList (
            "Increasing",
            "Decreasing"
        );

    private String chargerTypeChoice;
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private TextField registrationTextBox;
    @FXML private ComboBox<String> filterList;
    @FXML private ComboBox<String> sortList;
    @FXML private AnchorPane scrollPane_inner;
    @FXML private TextField chargingStationTextField;
    @FXML private TextArea stationDetailTextArea;

    @FXML private ListView<Button> stationsList;

    // Function run when user dropdown button pressed
    @FXML private void userDropdown(Event event) {
        System.out.println("User dropdown button pressed!");
        event.consume();
    }

    // Function run when view previous journeys pressed
    @FXML private void viewPrevJourneys(Event event) {
        System.out.println("view prev journeys button pressed!");
        event.consume();
    }

    // Function called when start journey button pressed
    @FXML private void startJourneyButton(Event event) {
        System.out.println("Start journey button pressed!");
        event.consume();
    }

    // Function called when end journey button pressed
    @FXML private void endJourneyButton(Event event) {
        System.out.println("End journey button pressed!");
        event.consume();
    }

    // Function run when charger combo box choice is changed
    // Used to set the value that is stored
    @FXML private void chargerTypeChoice(Event event) {
        chargerTypeChoice = chargerBox.getValue();
        event.consume();
    }

    // Run when the user clicks the register vehicle button
    // Placeholder, just prints a string representation of the values entered
    @FXML private void registerVehicle(Event event) {
        String registration = getRegistrationTextBox();
        System.out.println("New vehicle with registration " + registration
        + " and charger type " + chargerTypeChoice);
        event.consume();
    }

    // Opens up the station view.
    @FXML private void viewStations(Event event) {
        Parent root;
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
            root = baseLoader.load();

            TableService controller = baseLoader.getController();

            Stage tableStage = new Stage(StageStyle.UNDECORATED);
            controller.getData(tableStage);

            tableStage.setTitle("Stations");
            Scene scene = new Scene(root);
            tableStage.setScene(scene);
            tableStage.show();
            tableStage.setMinHeight(500);
            tableStage.setMinWidth(800);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Populates the station list with expandable buttons.
    private void updateStations() {
        QueryResult stations = Database.catchEmAll();
        ObservableList<Button> locations = FXCollections.observableArrayList();
        for (Station station : stations.getStations()) {
            String newstring = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newstring = newstring.substring(1, newstring.length()-1);
            Button stationButton = new Button(newstring);
            stationButton.setStyle("-fx-background-color: rgba(0, 0, 0, 0);"
                    + "    -fx-border-radius: 0;");
            String expandedText = newstring
                    + "\n  lat: " + station.getLatitude()
                    + "\n  long: " + station.getLongitude();
            String finalNewstring = newstring;
            stationButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override public void handle(ActionEvent event) {
                    if (!stationButton.getText().contains("\n")) {
                        stationButton.setText(expandedText);
                    } else {
                        stationButton.setText(finalNewstring);
                    }
                }
            });
            locations.add(stationButton);
        }

        stationsList.setItems(locations);
    }

    private String getRegistrationTextBox() {
        return registrationTextBox.getText();
    }

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        // Fill the combo boxes
        filterList.setItems(filterListOptions);
        chargerBox.setItems(chargerTypeOptions);
        sortList.setItems(sortListOptions);

        updateStations();

    }


}
