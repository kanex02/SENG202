package journey.controller;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.*;
import journey.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * FXML controller class for the main window.
 * This window is the basis for the application and has components of other controllers within itself.

 * @author journey dev team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger();

    private StationDAO stationDAO;
    private NoteDAO noteDAO;
    private VehicleDAO vehicleDAO;

    private Stage stage;

    private int selectedStation = -1;

    private User currentUser;

    private static final ObservableList<String> chargerTypeOptions =
        FXCollections.observableArrayList(
            "",
            "AC",
            "DC"
        );


    private QueryResult currentStations;
    private String chargerTypeChoice;
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private TextField registrationTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    @FXML private TextField yearTextBox;
    @FXML private Label warningLabel;
    @FXML private TextArea stationDetailTextArea;
    @FXML private BorderPane mapPane;
    @FXML private TabPane mainTabs;
    @FXML private AnchorPane tablePane;
    @FXML private AnchorPane prevJourneysPane;
    @FXML private Text stationDescription;
    @FXML private TabPane journeyTab;
    @FXML private Accordion searchAccordion;
    @FXML private GridPane leftPanel;
    @FXML private GridPane rightPanel;
    @FXML private TitledPane searchTitlePane;
    @FXML private AnchorPane searchWrapper;
    @FXML private AnchorPane recordJourneyWrapper;


    private RecordJourneyController recordJourneyController;
    private MapController mapViewController;
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

    /**
     * Loads the open layers map view into the tab pane.
     */
    @FXML void selectMapViewTab() {
        // viewMap();
    }

    @FXML void openPrevJourneysTable() {
        viewPrevJourneysTable();
    }


    /**
     * Function run when charger combo box choice is changed.
     * Used to set the value that is stored.

     * @param event change of Type Choice event
     */
    @FXML private void chargerTypeChoice(Event event) {
        chargerTypeChoice = chargerBox.getValue();
        event.consume();
    }

    /**
     * Run when the user presses the register vehicle button.
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type.

     * @param event register vehicle button pressed
     */
    @FXML private void registerVehicle(Event event) {
        //get information about the vehicles and reset to null values
        boolean valid = true;
        String registration = getRegistrationTextBox();
        String year = getYearTextBox();
        String make = getMakeTextBox();
        String model = getModelTextBox();
        chargerTypeChoice(event);

        if (Objects.equals(year, "") || Objects.equals(registration, "")
                || Objects.equals(make, "") || Objects.equals(model, "")
                || Objects.equals(chargerTypeChoice, "")) {
            warningLabel.setText("Fill all fields");
            valid = false;
        }

        int intYear = 0;
        if (Utils.isInt(year)) {
            intYear = Integer.parseInt(year);
        } else {
            warningLabel.setText("Year is invalid");
            valid = false;
        }

        Matcher makeHasDigit = digit.matcher(make);
        Matcher makeHasSpecial = special.matcher(make);
        Matcher modelHasDigit = digit.matcher(model);
        Matcher modelHasSpecial = special.matcher(model);

        if (makeHasSpecial.find() || makeHasDigit.find()) {
            warningLabel.setText("Make entry is invalid");
            valid = false;
        }

        if (modelHasSpecial.find() || modelHasDigit.find()) {
            warningLabel.setText("Model entry is invalid");
            valid = false;
        }

        if (valid) {
            registrationTextBox.setText("");
            yearTextBox.setText("");
            makeTextBox.setText("");
            modelTextBox.setText("");
            warningLabel.setText("");
            chargerBox.setValue("");
            Vehicle newVehicle = new Vehicle(intYear, make, model, chargerTypeChoice, registration);

            // Send vehicle to database
            try {
                vehicleDAO.setVehicle(newVehicle, currentUser);
                recordJourneyController.populateVehicleDropdown();
            } catch (Exception e) {
                log.error(e);
            }
            event.consume();
        }
    }

    /**
     * Loads the OpenLayers map view into the tab pane component of main view.
     */
    public void viewMap() {
        try {
            FXMLLoader mapViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            Parent mapViewParent = mapViewLoader.load();

            mapViewController = mapViewLoader.getController();
            mapViewController.init(stage, this);
            mapPane.setCenter(mapViewParent);
            mapPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Loads the table view into the tab pane component of main view.
     */
    public void viewTable() {
        try {
            FXMLLoader tableViewLoader = new FXMLLoader(getClass().getResource("/fxml/table.fxml"));
            Parent tableViewParent = tableViewLoader.load();

            TableController tableViewController = tableViewLoader.getController();
            tableViewController.init(stage, this);
            tablePane.getChildren().add(tableViewParent);
            AnchorPane.setTopAnchor(tableViewParent, 0d);
            AnchorPane.setBottomAnchor(tableViewParent, 0d);
            AnchorPane.setLeftAnchor(tableViewParent, 0d);
            AnchorPane.setRightAnchor(tableViewParent, 0d);
            tablePane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            log.error(e);
        }
    }

    private void viewPrevJourneysTable() {
        try {
            FXMLLoader prevJourneysViewLoader = new FXMLLoader(getClass().getResource("/fxml/previousJourneys.fxml"));
            Parent prevJourneysViewParent = prevJourneysViewLoader.load();

            PreviousJourneyController prevJourneyViewController = prevJourneysViewLoader.getController();
            prevJourneyViewController.init(stage, this);
            prevJourneysPane.getChildren().add(prevJourneysViewParent);
            AnchorPane.setTopAnchor(prevJourneysViewParent, 0d);
            AnchorPane.setBottomAnchor(prevJourneysViewParent, 0d);
            AnchorPane.setLeftAnchor(prevJourneysViewParent, 0d);
            AnchorPane.setRightAnchor(prevJourneysViewParent, 0d);
            prevJourneysPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            log.error(e);
        }
    }



    private String getRegistrationTextBox() {
        return registrationTextBox.getText();
    }

    private String getMakeTextBox() {
        return makeTextBox.getText();
    }

    private String getModelTextBox() {
        return modelTextBox.getText();
    }

    private String getYearTextBox() {
        return yearTextBox.getText();
    }

    private String getChargerNoteText() {
        return stationDetailTextArea.getText();
    }

    private void setChargerNoteText(String s) {
        stationDetailTextArea.setText(s);
    }

    private void setStationDescription(String s) {
        stationDescription.setText(s);
    }

    /**
     * Sets Note text for a given charger based on the current station selected.
     */
    public void setNoteText() {
        Station currStation = stationDAO.queryStation(selectedStation);
        if (currStation != null) {
            Note note = noteDAO.getNoteFromStation(currStation, currentUser); // Retrieve note from database
            setChargerNoteText(note.getNote());
        }
    }

    /**
     * Sets Long Display text for a given charger based on the current station selected.
     */
    public void setStationText() {
        Station currStation = stationDAO.queryStation(selectedStation);
        if (currStation != null) {
            setStationDescription(currStation.getLongDescription());
        }
    }

    /**
     * Submits notes and adds them the database for the current user.

     * @param event submit notes button clicked
     */
    @FXML private void submitNotes(Event event) {
        Station currStation = stationDAO.queryStation(selectedStation);
        String stationNote = getChargerNoteText();

        if (currStation != null) {
            Note newNote = new Note(currStation, stationNote);
            // Set the note on the database
            noteDAO.setNote(newNote, currentUser);
        }
        setNoteText();
        event.consume();
    }

    public int getSelectedStation() {
        return selectedStation;
    }

    public void setSelectedStation(int selectedStation) {
        recordJourneyController.updateSelectedStation(selectedStation);
        this.selectedStation = selectedStation;
    }

    public QueryResult getStations() {
        return currentStations;
    }

    public void mapJourney(Journey journey) {
        mapViewController.mapJourney(journey);
    }

    /**
     * Brings up the profile popup window when the 'my profile' button is pressed.

     * @param event Profile button clicked event
     */
    @FXML private void myProfileButton(Event event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            root = loader.load();

            ProfileController controller = loader.getController();

            Stage profileStage = new Stage(StageStyle.UNDECORATED);
            controller.init(profileStage, this);

            profileStage.setTitle("Profile");
            Scene scene = new Scene(root);
            profileStage.setScene(scene);
            profileStage.show();
            profileStage.setMinHeight(400);
            profileStage.setMinWidth(500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        event.consume();
    }

    /**
     * Opens the map, and disables almost everything else.
     */
    public void onlyMap() {
        searchAccordion.expandedPaneProperty().setValue(null);
        leftPanel.setDisable(true);
        rightPanel.setDisable(true);
        searchAccordion.setDisable(true);
    }

    /**
     * Re-enables everything.
     */
    public void reenable() {
        leftPanel.setDisable(false);
        rightPanel.setDisable(false);
        searchAccordion.setDisable(false);
    }

    private void viewSearch() {
        try {
            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/fxml/search.fxml"));
            Parent searchParent = searchLoader.load();

            SearchController searchController = searchLoader.getController();
            searchController.init(stage, this);
            searchWrapper.getChildren().add(searchParent);
            AnchorPane.setTopAnchor(searchParent, 0d);
            AnchorPane.setBottomAnchor(searchParent, 0d);
            AnchorPane.setLeftAnchor(searchParent, 0d);
            AnchorPane.setRightAnchor(searchParent, 0d);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setCurrentStations(QueryResult currentStations) {
        this.currentStations = currentStations;
    }

    public MapController getMapViewController() {
        return mapViewController;
    }

    public void openMap() {
        mainTabs.getSelectionModel().select(0);
    }

    public void openSearch() {
        searchAccordion.expandedPaneProperty().setValue(searchTitlePane);
    }

    private void viewRecordJourney() {
        try {
            FXMLLoader recorderLoader = new FXMLLoader(getClass().getResource("/fxml/recordJourney.fxml"));
            Parent recorderParent = recorderLoader.load();

            recordJourneyController = recorderLoader.getController();
            recordJourneyController.init(stage, this);
            recordJourneyWrapper.getChildren().add(recorderParent);
            AnchorPane.setTopAnchor(recorderParent, 0d);
            AnchorPane.setBottomAnchor(recorderParent, 0d);
            AnchorPane.setLeftAnchor(recorderParent, 0d);
            AnchorPane.setRightAnchor(recorderParent, 0d);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }


    /**
     * Initialize the window.

     * @param stage Top level container for this window
     */
    public void init(Stage stage, User user) {
        stationDAO = new StationDAO();
        noteDAO = new NoteDAO();
        vehicleDAO = new VehicleDAO();

        currentUser = user;

        currentStations = stationDAO.getAll();
        // Fill the combo boxes
        this.stage = stage;
        chargerBox.setItems(chargerTypeOptions);

        QueryResult stations = stationDAO.getAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations.getStations()) {
            String newString = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }

        viewMap();
        viewTable();
        viewPrevJourneysTable();
        viewRecordJourney();
        viewSearch();

        journeyTab.getSelectionModel().selectedItemProperty()
                .addListener(((observableValue, oldVal, newVal) -> mapViewController.clearRoute()));
    }



}
