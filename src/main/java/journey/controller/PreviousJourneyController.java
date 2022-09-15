package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import journey.data.*;


/**
 * A service to load data into the table viewer. TODO: Figure out how to get expanding rows.
 */
public class PreviousJourneyController {

    @FXML private TableColumn<Journey, String> startCol;
    @FXML private TableColumn<Journey, String> endCol;
    @FXML private TableColumn<Journey, String> vehicleCol;
    @FXML private TableView<Journey> journeyTable;
    @FXML private AnchorPane tableParent;

    /**
     * Imports the data.

     * @param stage The stage to import into.
     */
    public void setJourneys(Stage stage) {
        startCol.setCellValueFactory(new PropertyValueFactory<>("start"));
        endCol.setCellValueFactory(new PropertyValueFactory<>("end"));
        vehicleCol.setCellValueFactory(new PropertyValueFactory<>("vehicle_ID"));
        QueryResult data = Database.getJourneys();
        ObservableList<Journey> journeys = FXCollections.observableArrayList(data.getJourney());
        journeyTable.setItems(journeys);
    }

    /**
     * Initialises the table.

     * @param stage The stage to init.
     */
    public void init(Stage stage) {
        setJourneys(stage);
        journeyTable.maxWidthProperty().bind(tableParent.widthProperty());
        journeyTable.maxHeightProperty().bind(tableParent.heightProperty());

    }
}
