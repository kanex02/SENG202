package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import journey.data.Journey;
import journey.data.Station;
import journey.data.User;
import journey.repository.StationDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Arrays;

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
    private Station[] currentStations;


    @FXML private Label currentVehicle;
    @FXML private BorderPane mapPane;
    @FXML private TabPane mainTabs;
    @FXML private AnchorPane tablePane;
    @FXML private AnchorPane prevJourneysPane;
//    @FXML private Text stationDescription;
    @FXML private TabPane journeyTab;
    @FXML private AnchorPane searchWrapper;
    @FXML private AnchorPane notesWrapper;
    @FXML private AnchorPane recordJourneyWrapper;
    @FXML private AnchorPane plannedJourneysWrapper;
    @FXML private AnchorPane completeJourneyWrapper;
    @FXML private TitledPane planJourneyPane;
    @FXML private Accordion accordionPane;

    @FXML private AnchorPane registerVehicleWrapper;
    @FXML private AnchorPane selectedStationWrapper;
    @FXML private Label noteStationAddr;


    private NotesController notesController;
    private SearchController searchController;
    private TableController tableController;
    private CreateJourneyController recordJourneyController;
    private MapController mapViewController;
    private CompletedJourneysController completedJourneysController;
    private SelectedStationController selectedStationController;
    private PlannedJourneyController plannedJourneyController;
    private Stage profileStage = null;
    private Stage helpStage = null;



    public void setStartAddr(String addr) {
        recordJourneyController.changeJourneyStart(addr);
    }

    public void setEndAddr(String addr) {
        recordJourneyController.changeJourneyEnd(addr);
    }

    /**
     * Sets the accordion pane to be expanded on the plan journeys tab
     */
    public void setViewPlanJourneys() {
        accordionPane.setExpandedPane(planJourneyPane);
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



    /**
     * Inserts previous journeys table into an anchor pane.
     */
    private void viewPrevJourneysTable() {
        try {
            FXMLLoader plannedJourneysLoader = new FXMLLoader(getClass().getResource("/fxml/plannedJourneys.fxml"));
            Parent plannedJourneysViewParent = plannedJourneysLoader.load();

            PlannedJourneyController plannedJourneyController = plannedJourneysLoader.getController();
            this.plannedJourneyController = plannedJourneyController;
            plannedJourneyController.init(stage, this);
            plannedJourneysWrapper.getChildren().add(plannedJourneysViewParent);
            AnchorPane.setTopAnchor(plannedJourneysViewParent, 0d);
            AnchorPane.setBottomAnchor(plannedJourneysViewParent, 0d);
            AnchorPane.setLeftAnchor(plannedJourneysViewParent, 0d);
            AnchorPane.setRightAnchor(plannedJourneysViewParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }


    private void viewCompletedJourneysTable() {
        try {
            FXMLLoader completedJourneysViewLoader = new FXMLLoader(getClass().getResource("/fxml/completedJourneys.fxml"));
            Parent completedJourneysViewParent = completedJourneysViewLoader.load();

            CompletedJourneysController completedJourneyViewController = completedJourneysViewLoader.getController();
            completedJourneyViewController.init(stage, this);
            completedJourneysController = completedJourneyViewController;
            completeJourneyWrapper.getChildren().add(completedJourneysViewParent);
            AnchorPane.setTopAnchor(completedJourneysViewParent, 0d);
            AnchorPane.setBottomAnchor(completedJourneysViewParent, 0d);
            AnchorPane.setLeftAnchor(completedJourneysViewParent, 0d);
            AnchorPane.setRightAnchor(completedJourneysViewParent, 0d);
//            prevJourneysPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            log.error(e);
        }
    }

    private void viewSelectedStation() {
        try {
            FXMLLoader selectedStationLoader = new FXMLLoader(getClass().getResource("/fxml/selectedStation.fxml"));
            Parent selectedStationParent = selectedStationLoader.load();

            SelectedStationController selectedStationController = selectedStationLoader.getController();
            selectedStationController.init(this);
            this.selectedStationController = selectedStationController;
            selectedStationWrapper.getChildren().add(selectedStationParent);
            AnchorPane.setTopAnchor(selectedStationParent, 0d);
            AnchorPane.setBottomAnchor(selectedStationParent, 0d);
            AnchorPane.setLeftAnchor(selectedStationParent, 0d);
            AnchorPane.setRightAnchor(selectedStationParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }


    public void updatePlannedJourneys() {
        plannedJourneyController.setJourneys(stage);
    }

    public void updateCompletedJourneys() {
        completedJourneysController.setJourneys(stage);
    }

    public void updateNote() {
        notesController.updateNote();
    }

    public int getSelectedStation() {
        return selectedStation;
    }

    public void setSelectedStation(int selectedStation) {
        recordJourneyController.updateSelectedStation(selectedStation);
        selectedStationController.updateSelectedStation(selectedStation);
        this.selectedStation = selectedStation;
    }

    public Station[] getStations() {
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
        try {
            FXMLLoader baseLoader = new FXMLLoader(getClass().getResource("/fxml/myProfile.fxml"));
            Parent root = baseLoader.load();
            Stage mainStage = new Stage();

            MyProfileController baseController = baseLoader.getController();
            baseController.init(this, stage, selectedVehicle);

            mainStage.setTitle("Journey");
            Scene scene = new Scene(root, 600, 400);
            mainStage.setScene(scene);

            // set the min height and width so the window opens at the correct size
            mainStage.setMinHeight(650);
            mainStage.setMinWidth(900);
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            mainStage.setX(bounds.getMinX());
            mainStage.setY(bounds.getMinY());
            mainStage.setWidth(bounds.getWidth());
            mainStage.setHeight(bounds.getHeight());
            mainStage.setMaximized(true);
            mainStage.show();
            this.stage.close();
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void setProfile(Stage profileStage) {
        this.profileStage = profileStage;
    }
    @FXML private void helpButton(Event event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"));
            root = loader.load();
            if (helpStage == null) {
                helpStage = new Stage();
            }
            HelpController controller = loader.getController();
            controller.init(helpStage);
            helpStage.setTitle("Help");

            Scene scene = new Scene(root, 1200, 760);
            helpStage.setScene(scene);
            helpStage.setMaxHeight(760);
            helpStage.setMaxWidth(1200);
            helpStage.setMaximized(false);
            helpStage.show();
            // set the min height and width so the window opens at the correct size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            helpStage.setX((bounds.getWidth() - helpStage.getWidth())*1.0f/2);
            helpStage.setY((bounds.getHeight() - helpStage.getHeight())*1.0f/3);

        } catch (IOException e) {
            log.error(e);
        }
        event.consume();
    }

    /**
     * Inserts the search fxml component into an anchor pane in the main controller.
     */
    private void viewSearch() {
        try {
            FXMLLoader searchLoader = new FXMLLoader(getClass().getResource("/fxml/search.fxml"));
            Parent searchParent = searchLoader.load();

            searchController = searchLoader.getController();
            searchController.init(this);
            searchWrapper.getChildren().add(searchParent);
            AnchorPane.setTopAnchor(searchParent, 0d);
            AnchorPane.setBottomAnchor(searchParent, 0d);
            AnchorPane.setLeftAnchor(searchParent, 0d);
            AnchorPane.setRightAnchor(searchParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Sets the current stations, and updates the map and table.

     * @param currentStations new set of stations
     */
    public void setCurrentStations(Station[] currentStations) {
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
     * Inserts the recordJourney fxml component into an anchor pane in the main controller.
     */
    private void viewRecordJourney() {
        try {
            FXMLLoader recorderLoader = new FXMLLoader(getClass().getResource("/fxml/recordJourney.fxml"));
            Parent recorderParent = recorderLoader.load();

            recordJourneyController = recorderLoader.getController();
            recordJourneyController.init(this);
            recordJourneyWrapper.getChildren().add(recorderParent);
            AnchorPane.setTopAnchor(recorderParent, 0d);
            AnchorPane.setBottomAnchor(recorderParent, 0d);
            AnchorPane.setLeftAnchor(recorderParent, 0d);
            AnchorPane.setRightAnchor(recorderParent, 0d);

        } catch (IOException e) {
            log.error(e);
        }
    }

    public void addMiscMarkerToMap(double lat, double lng, String label) {
        mapViewController.addMiscMarker(lat, lng, label);
    }

    /**
     * Inserts the viewNotes fxml component into an anchor pane in the main controller.
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

    public void setSelectedVehicle(String selectedVehicle) {
        if (getSelectedVehicle() == null) {
            currentVehicle.setText("Current Vehicle: none selected");
        } else {
            currentVehicle.setText("Current Vehicle: " + getSelectedVehicle());
        }
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

    /**
     * Initialize the window.

     * @param stage Top level container for this window
     */
    public void init(Stage stage, User user, String vehicle) {
        stationDAO = new StationDAO();
        currentUser = user;
        this.selectedVehicle = vehicle;
        currentStations = stationDAO.getAll();
        // Fill the combo boxes
        this.stage = stage;

        Station[] stations = stationDAO.getAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations) {
            String newString = Arrays.toString(Arrays.copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }
        viewMap();
        viewTable();
        viewPrevJourneysTable();
        viewRecordJourney();
        viewSearch();
        viewNotes();
        setSelectedVehicle(selectedVehicle);
        viewCompletedJourneysTable();
        viewSelectedStation();

//        journeyTab.getSelectionModel().selectedItemProperty().addListener(
//                (observableValue, oldVal, newVal) -> mapViewController.clearRoute()
//        );
    }
}
