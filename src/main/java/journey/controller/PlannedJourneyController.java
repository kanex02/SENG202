package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    @FXML private Button deleteJourney;
    @FXML private Button goBack;

    private MainController mainController;
    private JourneyDAO journeyDAO;
    private Journey selectedJourney;
    private boolean confirming = true;

    /**
     * Imports the data.
     */
    public void setJourneys() {
        Journey[] data = journeyDAO.getPlannedJourneys(mainController.getCurrentUser());
        ObservableList<Journey> journeys = FXCollections.observableArrayList(data);
        for (Journey journey : journeys) {
            journey.setStart(clipAtComma(journey.getStart()));
            journey.setEnd(clipAtComma((journey.getEnd())));
        }
        journeyTable.setItems(journeys);
    }

    private String clipAtComma(String str) {
        return str.split(",")[0];
    }

    /**
     * Deletes a journey.
     */
    @FXML private void deleteJourney() {
        if (selectedJourney != null) {
            if (confirming) {
                deleteJourney.setText("Confirm delete?");
                goBack.setVisible(true);
                confirming = false;
            } else {
                journeyDAO.deleteJourney(selectedJourney);
                selectedJourney = null;
                setJourneys();
                goBack.setVisible(false);
                deleteJourney.setText("Delete selected journey");
                confirming = true;
            }
        }
    }

    /**
     * Undo
     */
    @FXML private void goBack() {
        if (!confirming) {
            deleteJourney.setText("Delete");
            goBack.setVisible(false);
            confirming = true;
        }
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

        goBack.setVisible(false);

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
