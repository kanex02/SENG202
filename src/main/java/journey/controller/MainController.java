package journey.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import journey.data.Journey;
import journey.data.Station;
import journey.data.User;
import journey.repository.StationDAO;
import journey.repository.VehicleDAO;
import journey.service.StationsService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static java.util.Arrays.asList;



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
    @FXML private ScrollPane recordJourneyWrapper;
    @FXML private TitledPane planJourneyPane;
    @FXML private TitledPane plannedJourneyPane;
    @FXML private TitledPane selectedStationTitledPane;
    @FXML private TitledPane searchPane;
    @FXML private TitledPane notesPane;
    @FXML private Accordion accordionPane;
    @FXML private BorderPane mainPane;
    @FXML private javafx.scene.image.ImageView helpIcon;
    @FXML private javafx.scene.image.ImageView homeIcon;
    @FXML private javafx.scene.image.ImageView profileIcon;
    @FXML private javafx.scene.image.ImageView journeyIcon;
    @FXML private javafx.scene.image.ImageView searchIcon;
    @FXML private javafx.scene.image.ImageView notesIcon;
    @FXML private javafx.scene.image.ImageView planIcon;
    @FXML private javafx.scene.image.ImageView plannedIcon;
    @FXML private javafx.scene.image.ImageView chargerIcon;

    private NotesController notesController;
    private SearchController searchController;
    private TableController tableController;
    private CreateJourneyController createJourneyController;
    private MapController mapViewController;
    private SelectedStationController selectedStationController;
    private PlannedJourneyController plannedJourneyController;
    private StationsService stationsService;
    private Stage helpStage = null;
    private Stage creditStage = null;

    private ArrayList<javafx.scene.image.ImageView> icons = new ArrayList<>();

    private final ArrayList<String> paths = new ArrayList<>(asList("/images/question 1.png",
            "/images/home-svgrepo-com.png", "/images/user 1.png", "/images/Journey_Logo.jpeg",
            "/images/search-svgrepo-com.png", "/images/notes-svgrepo-com.png",
            "/images/destination.png", "/images/task.png",
            "/images/charging-station.png"));


    public void addRangeIndicator(double lat, double lng) {
        searchController.addRangeIndicator(lat, lng);
    }

    public void setUser(User user) {
        currentUser = user;
    }

    public StationsService getStationsService() {
        return stationsService;
    }

    /**
     * Loads the OpenLayers map view into the tab pane component of main view.
     */
    private void viewMap() {
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
     * Finds the favourite station within the list of all stations
     * and sets it to favourite/not favourite.

     * @param station Station to favourite (or unfavourite).
     * @param favourite t/f if favourite or not.
     */
    public void updateFavourite(Station station, boolean favourite) {
        int stationID = station.getObjectid();
        Station[] allStations = stationsService.getAllStations();
        for (Station s : allStations) {
            if (s.getObjectid() == stationID) {
                s.setFavourite(favourite);
            }
        }
    }

    /**
     * Loads the table view into the tab pane component of main view.
     */
    private void viewTable() {
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


            PlannedJourneyController plannedJourneyControl = plannedJourneysLoader.getController();
            plannedJourneyControl.init(this);
            this.plannedJourneyController = plannedJourneyControl;
            ((ScrollPane) plannedJourneyPane.getContent()).setContent(plannedJourneysViewParent);
            ((AnchorPane) plannedJourneysViewParent).prefHeightProperty().bind(
                    ((ScrollPane) plannedJourneyPane.getContent()).heightProperty());

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

            SelectedStationController selectedStationControl = selectedStationLoader.getController();
            selectedStationControl.init(this);
            this.selectedStationController = selectedStationControl;
            // You may want to access this component by id instead of accessing and casting from its parent
            ((ScrollPane) selectedStationTitledPane.getContent()).setContent(selectedStationParent);

        } catch (IOException e) {
            log.error(e);
        }
    }

    public void updatePlannedJourneys() {
        plannedJourneyController.setJourneys();
    }


    public int getSelectedStation() {
        return selectedStation;
    }

    /**
     * Sets the selected station.

     * @param selectedStation said selected station.
     */
    public void setSelectedStation(int selectedStation) {
        this.selectedStation = selectedStation;
        selectedStationController.updateSelectedStation(selectedStation);
        notesController.updateSelectedStation(selectedStation);
    }

    public Station[] getStations() {
        return currentStations;
    }

    public void mapJourney(Journey journey) {
        mapViewController.mapJourney(journey);
    }

    public void mapJourneyFromLatLng(String[] waypoints) {
        mapViewController.mapJourneyFromLatLng(waypoints);
    }

    /**
     * Brings up the profile popup window when the 'my profile' button is pressed.

     */
    @FXML private void viewMyProfile() {
        try {
            FXMLLoader profileLoader = new FXMLLoader(getClass().getResource("/fxml/myProfile.fxml"));
            Parent profileParent = profileLoader.load();
            ProfileMainController profileMainController = profileLoader.getController();
            profileMainController.init(this, stage, mainPane);
            mainPane.setCenter(profileParent);
            mainPane.toFront();
            mainPane.setVisible(true);
        } catch (IOException e) {
            log.error(e);
        }
    }

    /**
     * Inserts a waypoint
     * @param lat latitude
     * @param lng longitude
     * @param position index of the journey
     */
    public void insertWaypoint(double lat, double lng, int position) {
        createJourneyController.insertWaypoint(lat, lng, position);
    }

    public void clearWaypoints() {
        mapViewController.clearWaypoints();
    }

    public void clearWaypoint(int i) {
        mapViewController.clearWaypoint(i);
    }

    /**
     * Opens new stage if none exists of a help page upon help button press.

     * @param event help button pressed.
     */
    @FXML private void viewHelpScreen(Event event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/help.fxml"));
            root = loader.load();
            if (helpStage == null) {
                helpStage = new Stage();
            }
            HelpController controller = loader.getController();
            controller.init(helpStage, this.stage);
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
            helpStage.setX((bounds.getWidth() - helpStage.getWidth()) * 1.0f / 2);
            helpStage.setY((bounds.getHeight() - helpStage.getHeight()) * 1.0f / 3);

        } catch (IOException e) {
            log.error(e);
        }
        event.consume();
    }

    /**
     * Function to open the credits page.
     */
    @FXML private void viewAttributions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/credits.fxml"));
            Parent root = loader.load();
            if (creditStage == null) {
                creditStage = new Stage();
            }
            CreditsController controller = loader.getController();
            creditStage.setTitle("Credits");
            controller.start(creditStage);
            Scene scene = new Scene(root, 1200, 760);
            creditStage.setScene(scene);
            creditStage.setMaxHeight(400);
            creditStage.setMaxWidth(600);
            creditStage.setMaximized(false);
            creditStage.show();
            // set the min height and width so the window opens at the correct size
            Screen screen = Screen.getPrimary();
            Rectangle2D bounds = screen.getVisualBounds();
            creditStage.setX((bounds.getWidth() - creditStage.getWidth()) * 1.0f / 2);
            creditStage.setY((bounds.getHeight() - creditStage.getHeight()) * 1.0f / 3);

        } catch (Exception e) {
            e.printStackTrace();
        }
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

    /**
     * Switches the main tab view so that the map is in focus.
     */
    public void openMap() {
        mainTabs.getSelectionModel().select(0);
    }

    /**
     * Used to initialize the event listener for switching tabs
     * This is mainly used for bringing the selected station marker between
     * table view and the map view so that it is consistent.
     */
    public void setTabListener() {
        mainTabs.getSelectionModel().selectedIndexProperty().addListener((observableValue, number, t1) -> {
            final int tablePaneNum = 1;
            int selectedPane = t1.intValue();

            if (selectedPane == tablePaneNum) {
                tableController.clearSelections();
            }
        });
    }


    /**
     * Inserts the recordJourney fxml component into an anchor pane in the main controller.
     */
    private void viewRecordJourney() {
        try {
            FXMLLoader recorderLoader = new FXMLLoader(getClass().getResource("/fxml/planJourney.fxml"));
            Parent recorderParent = recorderLoader.load();

            ((AnchorPane) recorderParent).prefHeightProperty().bind(recordJourneyWrapper.heightProperty());

            createJourneyController = recorderLoader.getController();
            createJourneyController.init(this);
            ((ScrollPane) planJourneyPane.getContent()).setContent(recorderParent);

        } catch (IOException e) {
            log.error(e);
        }
    }

    public void addMiscMarkerToMap(double lat, double lng, String label) {
        mapViewController.addMiscMarker(lat, lng, label);
    }

    public void editWaypoint(Double lat, Double lng, int position) {
        createJourneyController.editWaypoint(lat, lng, position);
    }

    /**
     * Adds a new waypoint. Opens the planJourneyPane first.
     * @param lat latitude
     * @param lng longitude
     */
    public void addNewWaypoint(double lat, double lng) {
        accordionPane.setExpandedPane(planJourneyPane);
        createJourneyController.addNewWaypoint(lat, lng);
    }

    /**
     * Calls the clear search function from map controller.
     */
    public void clearSearchMarkerFromMap() {
        mapViewController.clearSearch();
    }

    /**
     * Called to set the previous marker to be favourited.
     */
    public void setPrevMarkerFavourite() {
        mapViewController.setPrevMarkerFavourite();
    }

    /**
     * Called to set the ID of the selected station.
     * Used to pass selected station from table view.
     */
    public void setSelectedStationID(int id, boolean favourite) { mapViewController.setSelectedStationID(id, favourite); }

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

    /**
     * Initializes the images for the main view screen.
     */
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
        setCurrentStations(stationDAO.getAll(currentUser));
    }

    /**
     * Sets the selected vehicle.

     * @param selectedVehicle said selected vehicle.
     */
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

    public void refreshSearch() {
        searchController.search();
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void clearRoute() {
        mapViewController.clearRoute();
    }

    /**
     * Initialize the window.

     * @param stage Top level container for this window
     */
    public void init(Stage stage, User user) {
        stationDAO = new StationDAO();
        currentUser = user;
        VehicleDAO vehicleDAO = new VehicleDAO();
        if (vehicleDAO.getSelectedVehicle(user) == null) {
            this.selectedVehicle = null;
        } else {
            this.selectedVehicle = vehicleDAO.getSelectedVehicle(user).getRegistration();
        }

        stationsService = new StationsService(currentUser);

        // Clears the route if a previous journey is being displayed
        accordionPane.expandedPaneProperty().addListener(((observableValue, oldPane, newPane) -> {
            if (newPane == planJourneyPane) {
                plannedJourneyController.clearTableSelection();
                createJourneyController.updateJourney();
            }
        }));

        // Fill the combo boxes
        this.stage = stage;

        currentStations = stationDAO.getAll(currentUser);
        icons = new ArrayList<>(asList(helpIcon, homeIcon, profileIcon, journeyIcon,
                searchIcon, notesIcon, planIcon, plannedIcon, chargerIcon));
        initImages();
        setTabListener();
        viewMap();
        viewTable();
        viewPrevJourneysTable();
        viewRecordJourney();
        viewSearch();
        viewNotes();
        setSelectedVehicle(selectedVehicle);
        viewSelectedStation();
    }

}
