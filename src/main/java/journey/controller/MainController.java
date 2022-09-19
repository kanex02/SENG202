package journey.controller;

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
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.*;
import journey.repository.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * FXML controller class for the main window
 * This window is the basis for the application and has components of other controllers within itself
 * @author journey dev team
 */
public class MainController {

    private static final Logger log = LogManager.getLogger();

    private StationDAO stationDAO;
    private NoteDAO noteDAO;
    private VehicleDAO vehicleDAO;
    private UserDAO userDAO;
    private JourneyDAO journeyDAO;

    private Stage stage;

    private static int selectedStation = -1;

    private static final ObservableList<String> filterListOptions =
        FXCollections.observableArrayList (
            "Distance",
            "Cost"
        );

    private static final ObservableList<String> chargerTypeOptions =
        FXCollections.observableArrayList (
            "",
            "AC",
            "DC"
        );

    private static final ObservableList<String> chargerTypeSearchOptions =
            FXCollections.observableArrayList (
                    "",
                    "Mixed",
                    "AC",
                    "DC"
            );

    private static final ObservableList<String> yesNoMaybeSo =
            FXCollections.observableArrayList (
                    "",
                    "Yes",
                    "No"
            );

    private static final ObservableList<String> sortListOptions =
        FXCollections.observableArrayList (
            "Increasing",
            "Decreasing"
        );


    private static QueryResult currentStations;
    private String chargerTypeChoice;
    @FXML private ChoiceBox<String> chargerBox;
    @FXML private TextField registrationTextBox;
    @FXML private TextField makeTextBox;
    @FXML private TextField modelTextBox;
    @FXML private TextField yearTextBox;
    @FXML private ComboBox<String> filterList;
    @FXML private ComboBox<String> sortList;
    @FXML private Label warningLabel;
    @FXML private Label journeyWarningLabel;

    @FXML private AnchorPane scrollPane_inner;
    @FXML private TextArea chargingStationTextArea;
    @FXML private TextArea stationDetailTextArea;
    @FXML private ComboBox<String> stationDropDown;
    @FXML private ComboBox<String> selectVehicleComboBox;
    private String vehicleChoice;
    @FXML private ListView<String> visitedStationsList;
    @FXML private TextField startTextBox;
    @FXML private TextField endTextBox;
    @FXML private AnchorPane searchPane;
    @FXML private HBox searchRow;
    @FXML private BorderPane mapPane;
    @FXML private TabPane mainTabs;
    @FXML private AnchorPane tablePane;
    @FXML private AnchorPane prevJourneysPane;
    @FXML private TextField addressSearch;
    @FXML private TextField nameSearch;
    @FXML private TextField operatorSearch;
    @FXML private TextField timeSearch;
    @FXML private ChoiceBox<String> chargerBoxSearch;
    @FXML private ChoiceBox<String> attractionSearch;
    @FXML private TextField distanceSearch;
    @FXML private TextField searchLat;
    @FXML private TextField searchLong;
    @FXML private Text stationDescription;
    @FXML private TextField startLat;
    @FXML private TextField startLong;
    @FXML private TextField endLat;
    @FXML private TextField endLong;

    private MapController mapViewController;
    Pattern digit = Pattern.compile("[0-9]");
    Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");


    /**
     * Loads the open layers map view into the tab pane;
     */
    @FXML void selectMapViewTab() {
        // viewMap();
    }

    @FXML void openPrevJourneysTable() {
        viewPrevJourneysTable();
    }

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

    /**
     * Function run when charger combo box choice is changed
     * Used to set the value that is stored
     * @param event change of Type Choice event
     */
    @FXML private void chargerTypeChoice(Event event) {
        chargerTypeChoice = chargerBox.getValue();
        event.consume();
    }

    /**
     * Run when the user presses the register vehicle button
     * Initialises a new vehicle and assigns it to the current user based on the input
     * fields for make, model, year, registration and charger type
     *
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

        if (year == "" || registration == "" || make == "" || model == "" || chargerTypeChoice == "") {
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

            DatabaseManager databaseManager = DatabaseManager.getInstance();
            // Send vehicle to database
            try {
                vehicleDAO.setVehicle(newVehicle);
                populateVehicleDropdown();
            } catch (Exception e) {
                log.error(e);
            }
            event.consume();
        }
    }
    /**
     * Loads the OpenLayers map view into the tab pane component of main view
     */
    private void viewMap() {
        try {
            FXMLLoader mapViewLoader = new FXMLLoader(getClass().getResource("/fxml/map.fxml"));
            Parent mapViewParent = mapViewLoader.load();

            mapViewController = mapViewLoader.getController();
            mapViewController.init(stage, this);
            mapPane.setCenter(mapViewParent);
            mapPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the table view into the tab pane component of main view
     */
    private void viewTable() {
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
            e.printStackTrace();
        }
    }

    private void viewPrevJourneysTable() {
        try {
            FXMLLoader prevJourneysViewLoader = new FXMLLoader(getClass().getResource("/fxml/previousJourneys.fxml"));
            Parent prevJourneysViewParent = prevJourneysViewLoader.load();

            PreviousJourneyController prevJourneyViewController = prevJourneysViewLoader.getController();
            prevJourneyViewController.init(stage);
            prevJourneysPane.getChildren().add(prevJourneysViewParent);
            AnchorPane.setTopAnchor(prevJourneysViewParent, 0d);
            AnchorPane.setBottomAnchor(prevJourneysViewParent, 0d);
            AnchorPane.setLeftAnchor(prevJourneysViewParent, 0d);
            AnchorPane.setRightAnchor(prevJourneysViewParent, 0d);
            prevJourneysPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            e.printStackTrace();
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
     * Sets Note text for a given charger based on the current station selected
     */
    public void setNoteText() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Station currStation = stationDAO.queryStation(selectedStation);
        if (currStation != null) {
            Note note = noteDAO.getNoteFromStation(currStation); // Retrieve note from database
            setChargerNoteText(note.getNote());
        }
    }
    /**
     * Sets Long Display text for a given charger based on the current station selected
     */
    public void setStationText() {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        Station currStation = stationDAO.queryStation(selectedStation);
        if (currStation != null) {
            setStationDescription(currStation.getLongDescription());
        }
    }

    /**
     * Submits notes and adds them the database for the current user
     * @param event submit notes button clicked
     */
    @FXML private void submitNotes(Event event) {
        Station currStation = stationDAO.queryStation(selectedStation);
        String stationNote = getChargerNoteText();

        if (currStation != null) {
            Note newNote = new Note(currStation, stationNote);
            // Set the note on the database
            noteDAO.setNote(newNote);
        }
        setNoteText();
        event.consume();
    }

    @FXML private void selectStation(Event event) {
        if(stationDropDown.getValue() != null) {
            ObservableList<String> visitedStations = visitedStationsList.getItems();
            visitedStations.add(stationDropDown.getValue());
            visitedStationsList.setItems(visitedStations);
        }
        event.consume();
    }

    @FXML private void selectVehicleComboBox(Event event) {
        vehicleChoice = selectVehicleComboBox.getValue();
        event.consume();
    }

    @FXML private void addJourney(Event event) {
        boolean valid = true;
        String end = endTextBox.getText();
        String start = startTextBox.getText();
        int userID = userDAO.getCurrentUser().getId();
        selectVehicleComboBox(event);
        if (vehicleChoice == "" || start == "" || end == "") {
            journeyWarningLabel.setText("Fill all fields");
            valid = false;
        }

        Matcher startHasSpecial = special.matcher(start);
        Matcher endHasSpecial = special.matcher(end);

        if (startHasSpecial.find()) {
            journeyWarningLabel.setText("Start location invalid");
            valid = false;
        }

        if (endHasSpecial.find()) {
            journeyWarningLabel.setText("End location invalid");
            valid = false;
        }

        if (valid == true) {
            journeyWarningLabel.setText("");
            String[] vehicle = vehicleChoice.split(": ");
            String date = Utils.getDate();
            endTextBox.setText("");
            startTextBox.setText("");
            selectVehicleComboBox.setValue("");
            Journey journey = new Journey(start, end , vehicle[0], userID, date);
            for(String station : visitedStationsList.getItems()) {
                System.out.println(station);
            }

            journeyDAO.addJourney(journey);
            event.consume();
        }
    }

    /**
     * Searches for relevant stations based on users search inputs
     * @param event search button pressed
     */
    @FXML private void search(Event event) {
        QueryStation searchStation = new QueryStation();
        searchStation.setAddress(addressSearch.getText());
        searchStation.setName(nameSearch.getText());
        searchStation.setOperator(operatorSearch.getText());
        searchStation.setCurrentType(chargerBoxSearch.getValue());
        String attractionSearchRes = attractionSearch.getValue();
        if (attractionSearchRes != null) {
            boolean hasAttraction = (attractionSearchRes.equals("Yes"));
            searchStation.setHasTouristAttraction(hasAttraction);
        }
        if (timeSearch.getText().matches("\\d+")) {
            searchStation.setMaxTime(Integer.parseInt(timeSearch.getText()));
        }
        if (searchLat.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                & searchLong.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")
                & distanceSearch.getText().matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
            searchStation.setLatitude(Double.parseDouble(searchLat.getText()));
            searchStation.setLongitude(Double.parseDouble(searchLong.getText()));
            searchStation.setRange(Double.parseDouble(distanceSearch.getText()));
        }
        currentStations = stationDAO.query(searchStation);
        viewMap();
        viewTable();
    }

    public static int getSelectedStation() {
        return selectedStation;
    }

    public static void setSelectedStation(int selectedStation) {
        MainController.selectedStation = selectedStation;

    }

    public static QueryResult getStations() {
        return currentStations;
    }

    public void populateVehicleDropdown() {
        QueryResult data = vehicleDAO.getVehicles();
        ObservableList<String> vehicles = FXCollections.observableArrayList();
        for (Vehicle vehicle : data.getVehicles()) {
            String newString = vehicle.getStringRepresentation();
            vehicles.add(newString);
        }
        selectVehicleComboBox.setItems(vehicles);
    }

    /**
     * Brings up the profile popup window when the 'my profile' button is pressed
     * @param event Profile button clicked event
     */
    @FXML private void myProfileButton(Event event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/profile.fxml"));
            root = loader.load();

            ProfileController controller = loader.getController();

            Stage profileStage = new Stage(StageStyle.UNDECORATED);
            controller.init(profileStage);

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
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the journey start lat and long is updated.
     */
    @FXML private void clickStart() {
        mainTabs.getSelectionModel().select(0);
        mapViewController.setCallback((lat, lng) -> {
            startLat.setText(String.valueOf(lat));
            startLong.setText(String.valueOf(lng));
            return true;
        });
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the journey end lat and long is updated.
     */
    @FXML private void clickEnd() {
        mainTabs.getSelectionModel().select(0);
        mapViewController.setCallback((lat, lng) -> {
            endLat.setText(String.valueOf(lat));
            endLong.setText(String.valueOf(lng));
            return true;
        });
    }

    /**
     * Gets the coordinates of the next click on the map. A callback function is passed in,
     * so when the map is clicked the searching lat and long is updated.
     */
    @FXML private void clickSearch() {
        mainTabs.getSelectionModel().select(0);
        mapViewController.setCallback((lat, lng) -> {
            searchLat.setText(String.valueOf(lat));
            searchLong.setText(String.valueOf(lng));
            return true;
        });
    }

    /**
     * Initialize the window
     * @param stage Top level container for this window
     */
    public void init(Stage stage) {
        stationDAO = new StationDAO();
        noteDAO = new NoteDAO();
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        journeyDAO = new JourneyDAO();


        currentStations = stationDAO.getAll();
        // Fill the combo boxes
        this.stage = stage;
        chargerBox.setItems(chargerTypeOptions);

        chargerBoxSearch.setItems(chargerTypeSearchOptions);
        chargerBoxSearch.setValue("");

        attractionSearch.setItems(yesNoMaybeSo);

        QueryResult stations = stationDAO.getAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations.getStations()) {
            String newString = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }

        stationDropDown.setItems(stationList);
        populateVehicleDropdown();
        viewMap();
        viewTable();
        viewPrevJourneysTable();
    }



}
