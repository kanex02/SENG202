package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.ImageCursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import journey.data.Journey;
import journey.data.Station;
import journey.data.User;
import journey.repository.StationDAO;
import journey.service.StationsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import static java.util.Arrays.asList;
import static java.util.Arrays.copyOfRange;

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
    @FXML private TabPane journeyTab;
    @FXML private ScrollPane searchWrapper;
    @FXML private ScrollPane notesWrapper;
    @FXML private ScrollPane recordJourneyWrapper;
    @FXML private ScrollPane plannedJourneysWrapper;
    @FXML private ScrollPane completeJourneyWrapper;
    @FXML private TitledPane planJourneyPane;
    @FXML private TitledPane plannedJourneyPane;
    @FXML private TitledPane selectedStationTitledPane;
    @FXML private TitledPane completedJourneysPane;
    @FXML private TitledPane searchPane;
    @FXML private TitledPane notesPane;

    @FXML private Accordion accordionPane;
    @FXML private BorderPane mainPane;

    @FXML private Label noteStationAddr;
    @FXML private Pane notificationPane;

    @FXML private javafx.scene.image.ImageView helpIcon;
    @FXML private javafx.scene.image.ImageView homeIcon;
    @FXML private javafx.scene.image.ImageView profileIcon;
    @FXML private javafx.scene.image.ImageView journeyIcon;
    @FXML private javafx.scene.image.ImageView searchIcon;
    @FXML private javafx.scene.image.ImageView notesIcon;
    @FXML private javafx.scene.image.ImageView planIcon;
    @FXML private javafx.scene.image.ImageView plannedIcon;
    @FXML private javafx.scene.image.ImageView tickIcon;
    @FXML private javafx.scene.image.ImageView chargerIcon;

    private NotesController notesController;
    private SearchController searchController;
    private TableController tableController;
    private CreateJourneyController recordJourneyController;
    private MapController mapViewController;
    private CompletedJourneysController completedJourneysController;
    private SelectedStationController selectedStationController;
    private PlannedJourneyController plannedJourneyController;
    private StationsService stationsService;
    private Stage profileStage = null;
    private Stage helpStage = null;

    private ArrayList<javafx.scene.image.ImageView> icons = new ArrayList<>();

    private final ArrayList<String> paths = new ArrayList<>(asList("/pictures/question 1.png",
            "/pictures/home-svgrepo-com.png", "/pictures/user 1.png", "/pictures/Journey_Logo.jpeg",
            "/pictures/search-svgrepo-com.png", "/pictures/notes-svgrepo-com.png",
            "/pictures/destination.png", "/pictures/task.png","/pictures/check-mark.png",
            "/pictures/charging-station.png"));


    public void setStartAddr(String addr) {
        recordJourneyController.changeJourneyStart(addr);
    }

    public void setEndAddr(String addr) {
        recordJourneyController.changeJourneyEnd(addr);
    }

    /**
     * Sets the accordion pane to be expanded on the plan journeys tab.
     */
    public void setViewPlanJourneys() {
        accordionPane.setExpandedPane(planJourneyPane);
    }

    public StationsService getStationsService() {
        return stationsService;
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

    public void updateFavourite(Station station, boolean favourite) {
        int stationID = station.getOBJECTID();
        Station[] allStations = stationsService.getAllStations();
        for(Station s : allStations) {
            if(s.getOBJECTID() == stationID) {
                s.setFavourite(favourite);
            }
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
            ((ScrollPane) plannedJourneyPane.getContent()).setContent(plannedJourneysViewParent);

        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Inserts completed journeys table into an anchor pane.
     */
    private void viewCompletedJourneysTable() {
        try {
            FXMLLoader completedJourneysViewLoader = new FXMLLoader(getClass().getResource("/fxml/completedJourneys.fxml"));
            Parent completedJourneysViewParent = completedJourneysViewLoader.load();

            CompletedJourneysController completedJourneyViewController = completedJourneysViewLoader.getController();
            completedJourneyViewController.init(stage, this);
            completedJourneysController = completedJourneyViewController;
            ((ScrollPane) completedJourneysPane.getContent()).setContent(completedJourneysViewParent);
//            prevJourneysPane.prefWidthProperty().bind(mainTabs.widthProperty());

        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Inserts selected station/attraction view into an anchor pane.
     */
    private void viewSelectedStation() {
        try {
            FXMLLoader selectedStationLoader = new FXMLLoader(getClass().getResource("/fxml/selectedStation.fxml"));
            Parent selectedStationParent = selectedStationLoader.load();

            SelectedStationController selectedStationController = selectedStationLoader.getController();
            selectedStationController.init(this);
            this.selectedStationController = selectedStationController;
            // You may want to access this component by id instead of accessing and casting from its parent
            ((ScrollPane) selectedStationTitledPane.getContent()).setContent(selectedStationParent);

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

     */
    @FXML private void myProfileButton() {
        try {
            FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/myProfile.fxml"));
            Parent profileParent = profileLoader.load();
            MyProfileController myProfileController = profileLoader.getController();
            myProfileController.init(this, stage, selectedVehicle, mainPane);
            mainPane.setCenter(profileParent);
            mainPane.toFront();
            mainPane.setVisible(true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    public void setProfile(Stage profileStage) {
        this.profileStage = profileStage;
    }

    /**
     * Opens new stage if none exists of a help page upon help button press

     * @param event help button pressed
     */
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
            ((ScrollPane) searchPane.getContent()).setContent(searchParent);

        } catch (IOException e) {
            e.printStackTrace();
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
            ((ScrollPane) planJourneyPane.getContent()).setContent(recorderParent);

        } catch (IOException e) {
            log.error(e);
        }
    }

    public void addMiscMarkerToMap(double lat, double lng, String label) {
        mapViewController.addMiscMarker(lat, lng, label);
    }

    public void clearSearchMarkerFromMap() {
        mapViewController.clearSearch();
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
            ((ScrollPane) notesPane.getContent()).setContent(notesParent);
        } catch (IOException e) {
            log.error(e);
        }
    }

    private void initImages() {
        for (int index = 0; index < icons.size(); index++) {
            Image img = new Image(
                    new BufferedInputStream(
                            Objects.requireNonNull(getClass().getResourceAsStream(paths.get(index)))
                    ));
            (icons.get(index)).setImage(img);
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

    public void changeSearchLatLong(double lat, double lng) {
        searchController.changeSearchLatLong(lat, lng);
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

        stationsService = new StationsService();

        // Fill the combo boxes
        this.stage = stage;

        Station[] stations = stationDAO.getAll();
        ObservableList<String> stationList = FXCollections.observableArrayList();
        for (Station station : stations) {
            String newString = Arrays.toString(copyOfRange(station.getAddress().split(","), 0, 2));
            newString = newString.substring(1, newString.length() - 1);
            stationList.add(newString);
        }
        icons = new ArrayList<>(asList(helpIcon, homeIcon, profileIcon, journeyIcon,
                searchIcon, notesIcon, planIcon, plannedIcon, tickIcon, chargerIcon));
        initImages();
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
