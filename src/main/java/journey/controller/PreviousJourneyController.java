package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import journey.data.*;
import journey.repository.JourneyDAO;


/**
 * A service to load data into the table viewer. TODO: Figure out how to get expanding rows.
 */
public class PreviousJourneyController {

    @FXML private TableColumn<Journey, String> startCol;
    @FXML private TableColumn<Journey, String> endCol;
    @FXML private TableColumn<Journey, String> vehicleCol;
    @FXML private TableColumn<Journey, String> dateCol;
    @FXML private TableView<Journey> journeyTable;
    @FXML private AnchorPane tableParent;

    private JourneyDAO journeyDAO;

    /**
     * Imports the data.

     * @param stage The stage to import into.
     */
    public void setJourneys(Stage stage) {
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicle_ID"));
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        QueryResult data = journeyDAO.getJourneys();
        ObservableList<Journey> journeys = FXCollections.observableArrayList(data.getJourney());
        journeyTable.setItems(journeys);
    }

    /**
     * Initialises the table.

     * @param stage The stage to init.
     */
    public void init(Stage stage) {

        journeyDAO = new JourneyDAO();

        setJourneys(stage);
        journeyTable.maxWidthProperty().bind(tableParent.widthProperty());
        journeyTable.maxHeightProperty().bind(tableParent.heightProperty());

    }
}
