package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import journey.data.Journey;
import journey.repository.JourneyDAO;

/**
 * A service to load data into the table viewer.
 */
public class PlannedJourneyController {

    @FXML private TableColumn<Journey, String> startCol;
    @FXML private TableColumn<Journey, String> endCol;
    @FXML private TableColumn<Journey, String> vehicleCol;
    @FXML private TableColumn<Journey, String> dateCol;
    @FXML private TableView<Journey> journeyTable;
    @FXML private AnchorPane tableParent;

    private MainController mainController;
    private JourneyDAO journeyDAO;
    private Journey selectedJourney;

    /**
     * Imports the data.
     */
    public void setJourneys() {
        Journey[] data = journeyDAO.getPlannedJourneys(mainController.getCurrentUser());
        ObservableList<Journey> journeys = FXCollections.observableArrayList(data);
        journeyTable.setItems(journeys);
    }

    /**
     * Deletes a journey.
     */
    @FXML public void deleteJourney() {
        journeyDAO.deleteJourney(selectedJourney);
        selectedJourney = null;
        setJourneys();
    }

    public void clearTableSelection() {
        journeyTable.getSelectionModel().clearSelection();
    }

    /**
     * Initialises the table.

     * @param mainController Main Controller to be inserted into.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        journeyDAO = new JourneyDAO();

        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicleRegistration"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));

        setJourneys();

        journeyTable.maxWidthProperty().bind(tableParent.widthProperty());
        journeyTable.maxHeightProperty().bind(tableParent.heightProperty());

        journeyTable.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, oldJourney, newJourney) -> {
                    if (newJourney == null) {
                        mainController.clearRoute();
                    } else {
                        mainController.mapJourney(newJourney);
                    }
                this.selectedJourney = newJourney;
            }
        );

    }
}
