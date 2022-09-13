package journey.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.event.Event;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

import journey.data.Database;

/**
 * Controller for the main window
 * @author journey dev team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger();

    private static int selectedStation = -1;

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
    @FXML private TextArea chargingStationTextArea;
    @FXML private TextArea stationDetailTextArea;
    @FXML private ComboBox<String> stationDropDown;
    @FXML private ListView<String> visitedStationsList;
    @FXML private TextField startTextBox;
    @FXML private TextField endTextBox;


    @FXML private ListView<Button> stationsList;
    @FXML private TableColumn<Station, String> addressCol;
    @FXML private TableColumn<Station, Boolean> attractionCol;
    @FXML private TableColumn<Station, Integer> carparksCol;
    @FXML private TableColumn<Station, Integer> connectorsCol;
    @FXML private TableColumn<Station, String> currentTypeCol;
    @FXML private TableColumn<Station, Boolean> isFreePark;
    @FXML private TableColumn<Station, Boolean> isFreeCharge;
    @FXML private TableColumn<Station, Integer> latCol;
    @FXML private TableColumn<Station, Integer> longCol;
    @FXML private TableColumn<Station, String> nameCol;
    @FXML private TableColumn<Station, String> operatorCol;
    @FXML private TableColumn<Station, Integer> timeLimitCol;
    @FXML private TableView<Station> stationTable;
    @FXML
    private AnchorPane searchPane;
    @FXML
    private HBox searchRow;


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

    private User user;
    // Run when the user clicks the register vehicle button
    // Placeholder, just prints a string representation of the values entered
    @FXML private void registerVehicle(Event event) {
        String registration = getRegistrationTextBox();
        int id = 1;
        int year = 2014;
        String make = "Ford";
        String model = "Focus";
        Vehicle newVehicle = new Vehicle(year, make, model, chargerTypeChoice, registration);
        user.newVehicle(newVehicle);
        event.consume();
    }

    @FXML private void viewMap(Event event) {
        Parent root;
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            root = baseLoader.load();
            MapController controller = baseLoader.getController();

            Stage mapStage = new Stage(StageStyle.UNDECORATED);
            controller.init(mapStage);

            mapStage.setTitle("Map");
            Scene scene = new Scene(root);
            mapStage.setScene(scene);
            mapStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.consume();
    }


    private String getRegistrationTextBox() {
        return registrationTextBox.getText();
    }

    private String getChargerNoteText() {
        return stationDetailTextArea.getText();
    }

    private void setChargerNoteText(String s) {
        stationDetailTextArea.setText(s);
    }

    private void setNoteText() {
        Station currStation = Database.queryStation(selectedStation);
        if (currStation != null) {
            Note note = Database.getNoteFromStation(currStation); // Retrieve note from database
            setChargerNoteText(note.getNote());
        }
    }

    @FXML private void submitNotes(Event event) {

        Station currStation = Database.queryStation(selectedStation);
        String stationNote = getChargerNoteText();

        if (currStation != null) {
            Note newNote = new Note(currStation, stationNote);
            // Set the note on the database
            Database.setNote(newNote);
        }
        event.consume();
    }

    /**
     * Imports the data.

     * @param stage The stage to import into.
     */
    public void getData(Stage stage) {
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));
        attractionCol.setCellValueFactory(new PropertyValueFactory<>("hasTouristAttraction"));
        carparksCol.setCellValueFactory(new PropertyValueFactory<>("carParkCount"));
        connectorsCol.setCellValueFactory(new PropertyValueFactory<>("numberOfConnectors"));
        currentTypeCol.setCellValueFactory(new PropertyValueFactory<>("currentType"));
        isFreePark.setCellValueFactory(new PropertyValueFactory<>("hasCarParkCost"));
        isFreeCharge.setCellValueFactory(new PropertyValueFactory<>("hasChargingCost"));
        latCol.setCellValueFactory(new PropertyValueFactory<>("latitude"));
        longCol.setCellValueFactory(new PropertyValueFactory<>("longitude"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        operatorCol.setCellValueFactory(new PropertyValueFactory<>("operator"));
        timeLimitCol.setCellValueFactory(new PropertyValueFactory<>("maxTime"));
        QueryResult data = Database.catchEmAll();
        ObservableList<Station> stations = FXCollections.observableArrayList(data.getStations());
        stationTable.setItems(stations);
    }

    @FXML private void selectStation(Event event) {
        if(stationDropDown.getValue() != null) {
            ObservableList<String> visitedStations = visitedStationsList.getItems();
            visitedStations.add(stationDropDown.getValue());
            visitedStationsList.setItems(visitedStations);
        }
        event.consume();
    }

    @FXML private void addJourney(Event event) {
        if(startTextBox.getText() != "" && endTextBox.getText() != "") {
            System.out.println("Start: " + startTextBox.getText() + ", End: " + endTextBox.getText());
            for(String station : visitedStationsList.getItems()) {
                System.out.println(station);
            }
        }
    }

    /**
     * Initialize the window
     *
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        // Fill the combo boxes
        chargerBox.setItems(chargerTypeOptions);
        getData(stage);

        QueryResult stations = Database.catchEmAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations.getStations()) {
            String newString = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }

        stationDropDown.setItems(stationList);

        //Add selection listener
        stationTable.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldStation, newStation) -> {
            selectedStation = newStation.getOBJECTID();
            setNoteText();
        }));
    }


}
