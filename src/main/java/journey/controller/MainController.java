package journey.controller;

import java.io.IOException;
import java.util.Arrays;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import journey.data.Journey;
import journey.data.QueryResult;
import journey.data.Station;
import journey.data.User;
import journey.repository.StationDAO;
import journey.Utils;
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
    private Stage stage;
    private int selectedStation = -1;
    private User currentUser;
    private String selectedVehicle = null;
    private QueryResult currentStations;


    @FXML private Label currentVehicle;
    @FXML private BorderPane mapPane;
    @FXML private TabPane mainTabs;
    @FXML private AnchorPane tablePane;
    @FXML private AnchorPane prevJourneysPane;
    @FXML private Text stationDescription;
    @FXML private TabPane journeyTab;
    @FXML private AnchorPane searchWrapper;
    @FXML private AnchorPane notesWrapper;
    @FXML private AnchorPane recordJourneyWrapper;
    @FXML private AnchorPane registerVehicleWrapper;


    private NotesController notesController;
    private SearchController searchController;
    private TableController tableController;
    private CreateJourneyController recordJourneyController;
    private MapController mapViewController;
    private PlannedJourneyController plannedJourneyController;
    private Stage profileStage = null;



    public void populateVehicleDropdown() {
        recordJourneyController.populateVehicleDropdown();
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
            mapViewController.init(this);
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
            tableController.init(this);
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

    @FXML private void editVehicleButton(Event event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/editVehicle.fxml"));
            root = loader.load();

            EditVehicleController editVehicleController = loader.getController();

            Stage editVehicleStage = new Stage(StageStyle.UNDECORATED);
            editVehicleController.init(editVehicleStage, this);

            editVehicleStage.setTitle("Edit Vehicle");
            Scene scene = new Scene(root);
            editVehicleStage.setScene(scene);
            editVehicleStage.show();
            editVehicleStage.setMinHeight(371);
            editVehicleStage.setMinWidth(600);
        } catch (IOException e) {
            log.error(e);
        }
        event.consume();
    }


    /**
     * Inserts previous journeys table into an anchor pane
     */
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

    private void setStationDescription(String s) {
        stationDescription.setText(s);
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

            if (profileStage == null) {
                profileStage = new Stage(StageStyle.UNDECORATED);
                profileStage.setTitle("Profile");
            }
            Scene scene = new Scene(root);
            controller.init(profileStage, this);
            profileStage.setScene(scene);
            profileStage.show();
            profileStage.setMinHeight(400);
            profileStage.setMinWidth(500);
        } catch (IOException e) {
            log.error(e);
        }
        event.consume();
    }

    public void setProfile(Stage profileStage) {
        this.profileStage = profileStage;
    }

    /**
     * Inserts the search fxml component into an anchor pane in the main controller
     */
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
            log.error(e);
        }
    }

    @FXML public void openSearchPanel() {
        viewSearch();
    }

    /**
     * Inserts the search fxml component into an anchor pane in the main controller
     */
    private void viewNotesPanel() {
        try {
            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/fxml/notes.fxml"));
            Parent notesParent = searchLoader.load();

            notesController = searchLoader.getController();
            notesController.init(this);
            notesWrapper.getChildren().add(notesParent);
            AnchorPane.setTopAnchor(notesParent, 0d);
            AnchorPane.setBottomAnchor(notesParent, 0d);
            AnchorPane.setLeftAnchor(notesParent, 0d);
            AnchorPane.setRightAnchor(notesParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }

    @FXML public void openNotesPane() {
        viewNotesPanel();
    }


    /**
     * Inserts the search fxml component into an anchor pane in the main controller
     */
    private void viewPlanJourney() {
        try {
            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/fxml/recordJourney.fxml"));
            Parent planJourneyParent = searchLoader.load();

            recordJourneyController = searchLoader.getController();
            recordJourneyController.init(stage, this);
            recordJourneyWrapper.getChildren().add(planJourneyParent);
            AnchorPane.setTopAnchor(planJourneyParent, 0d);
            AnchorPane.setBottomAnchor(planJourneyParent, 0d);
            AnchorPane.setLeftAnchor(planJourneyParent, 0d);
            AnchorPane.setRightAnchor(planJourneyParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }

    @FXML public void openPlanJourneyPane() {
        viewPlanJourney();
    }

    private void viewPlannedJourneys() {
        try {
            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/fxml/plannedJourneys.fxml"));
            Parent plannedJourneysParent = searchLoader.load();

            plannedJourneyController = searchLoader.getController();
            plannedJourneyController.init(stage, this);
            recordJourneyWrapper.getChildren().add(plannedJourneysParent);
            AnchorPane.setTopAnchor(plannedJourneysParent, 0d);
            AnchorPane.setBottomAnchor(plannedJourneysParent, 0d);
            AnchorPane.setLeftAnchor(plannedJourneysParent, 0d);
            AnchorPane.setRightAnchor(plannedJourneysParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }

    @FXML public void openPlannedJourneysPanel() {
        viewPlannedJourneys();
    }

    public void updateNoteText() {
        notesController.updateNoteText();
    }

    /**
     * Sets the current stations, and updates the map and table.

     * @param currentStations new set of stations
     */
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

    /**
     * Inserts the recordJourney fxml component into an anchor pane in the main controller
     */
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
            log.error(e);
        }
    }
    /**
     * Inserts the viewRegisteredVehicles fxml component into an anchor pane in the main controller
     */
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
            log.error(e);
        }
    }
    /**
     * Inserts the viewNotes fxml component into an anchor pane in the main controller
     */
    private void viewNotes() {
        try {
            FXMLLoader notesLoader = new FXMLLoader(getClass().getResource("/fxml/notes.fxml"));
            Parent notesParent = notesLoader.load();

            notesController = notesLoader.getController();
            notesController.init(this);
            notesWrapper.getChildren().add(notesParent);
            AnchorPane.setTopAnchor(notesParent, 0d);
            AnchorPane.setBottomAnchor(notesParent, 0d);
            AnchorPane.setLeftAnchor(notesParent, 0d);
            AnchorPane.setRightAnchor(notesParent, 0d);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void clearSearch() {
        setCurrentStations(stationDAO.getAll());
        mapViewController.clearSearch();
    }

    public void setVehicle() {
        currentVehicle.setText("Current Vehicle: " + getSelectedVehicle());
    }

    public void setSelectedVehicle(String selectedVehicle) {
        currentVehicle.setText("Current Vehicle: " + getSelectedVehicle());
        this.selectedVehicle = selectedVehicle;
    }

    public String getSelectedVehicle() {
        return selectedVehicle;
    }

    public void changeSearchLatLong(String addr) {
        searchController.changeSearchLatLong(addr);
    }

    public void changeJourneyStart(String addr) {
        recordJourneyController.changeJourneyStart(addr);
    }

    public void changeJourneyEnd(String addr) {
        recordJourneyController.changeJourneyEnd(addr);
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
        viewNotes();

        journeyTab.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldVal, newVal) -> mapViewController.clearRoute()
        );
    }
}
