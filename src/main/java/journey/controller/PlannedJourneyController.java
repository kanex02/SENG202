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
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicle_ID"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        Journey[] data = journeyDAO.getPlannedJourneys(mainController.getCurrentUser());
        ObservableList<Journey> journeys = FXCollections.observableArrayList(data);
        journeyTable.setItems(journeys);
    }

    /**
     * Mark a journey as completed (move planned journey table to completed journey table).
     */
    @FXML public void markCompletedButton() {
        int journeyID = selectedJourney.getJourneyID();
        journeyDAO.completeAJourney(journeyID, mainController.getCurrentUser().getId());
        mainController.updateCompletedJourneys();
    }

    @FXML public void deleteJourney() {
        System.out.println("delete journey");
    }

    /**
     * Initialises the table.

     * @param mainController Main Controller to be inserted into.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        journeyDAO = new JourneyDAO();

        setJourneys();
        journeyTable.maxWidthProperty().bind(tableParent.widthProperty());
        journeyTable.maxHeightProperty().bind(tableParent.heightProperty());

        journeyTable.getSelectionModel().selectedItemProperty().addListener((observableValue, oldJourney, newJourney) -> {
                mainController.mapJourney(newJourney);
                this.selectedJourney = newJourney;
            }
        );
    }
}
