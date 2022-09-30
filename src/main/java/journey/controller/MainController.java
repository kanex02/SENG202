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
import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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


    private QueryResult currentStations;
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
    @FXML private AnchorPane registerVehicleWrapper;


    private SearchController searchController;
    private TableController tableController;
    private CreateJourneyController recordJourneyController;
    private MapController mapViewController;

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

            tableController = tableViewLoader.getController();
            tableController.init(stage, this);
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

            PlannedJourneyController prevJourneyViewController = prevJourneysViewLoader.getController();
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

        if (selectedStation != -1) {
            Station currStation = stationDAO.queryStation(selectedStation);
            if (currStation != null) {
                Note note = noteDAO.getNoteFromStation(currStation, currentUser);
                setChargerNoteText(note.getNote());
            }
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

            searchController = searchLoader.getController();
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
        mapViewController.addStationsOnMap();
        tableController.getData();
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

    private void viewRegisterVehicles() {
        try {
            FXMLLoader registerVehicleLoader = new FXMLLoader(getClass().getResource("/fxml/registerVehicle.fxml"));
            Parent registerVehicleParent = registerVehicleLoader.load();

            RegisterVehicleController registerVehicleController = registerVehicleLoader.getController();
            registerVehicleController.init(stage, this);
            registerVehicleWrapper.getChildren().add(registerVehicleParent);
            AnchorPane.setTopAnchor(registerVehicleParent, 0d);
            AnchorPane.setBottomAnchor(registerVehicleParent, 0d);
            AnchorPane.setLeftAnchor(registerVehicleParent, 0d);
            AnchorPane.setRightAnchor(registerVehicleParent, 0d);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void clearSearch() {
        setCurrentStations(stationDAO.getAll());
        mapViewController.clearSearch();
    }

    public void changeSearchLatLong(double lat, double lng) {
        searchController.changeSearchLatLong(lat, lng);
    }

    public void changeJourneyStart(double lat, double lng) {
        recordJourneyController.changeJourneyStart(lat, lng);
    }

    public void changeJourneyEnd(double lat, double lng) {
        recordJourneyController.changeJourneyEnd(lat, lng);
    }

    public void refreshSearch() {
        searchController.search();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void updateVehicles() {
        recordJourneyController.populateVehicleDropdown();
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

        QueryResult stations = stationDAO.getAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations.getStations()) {
            String newString = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }
        viewRegisterVehicles();
        viewMap();
        viewTable();
        viewPrevJourneysTable();
        viewRecordJourney();
        viewSearch();

        journeyTab.getSelectionModel().selectedItemProperty().addListener(((observableValue, oldVal, newVal) -> mapViewController.clearRoute()));
    }
}
