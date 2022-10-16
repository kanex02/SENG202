package journey.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import journey.data.Note;
import journey.data.Station;
import journey.repository.NoteDAO;
import journey.repository.StationDAO;

/**
 * Controller for selected station FXML.
 */
public class SelectedStationController {


    @FXML private Label addressField;
    @FXML private Label nameField;
    @FXML private Label operatorField;
    @FXML private Label currentField;
    @FXML private Label carparkField;
    @FXML private Label timeLimitField;
    @FXML private Label costField;
    @FXML private Label parkingCostField;
    @FXML private Label ratingField;
    @FXML private Label favouritedField;
    @FXML private Label operationalWarning;
    @FXML private ListView<String> connectorsList;

    private final StationDAO stationDAO = new StationDAO();
    private final NoteDAO noteDAO = new NoteDAO();
    private MainController mainController;
    private Station selectedStation;

    /**
     * Checks if a charging station
     */
    private ObservableList<String> getConnsInnoperative() {
        String[] connectors = selectedStation.getConnectors();
        boolean inoperative = false;
        ObservableList<String> conns = FXCollections.observableArrayList();
        int counter = 1;
        for (String connector: connectors) {
            if (!inoperative) {
                inoperative = connector.contains("Unknown") || connector.contains("Inoperative");
            }
            if (counter % 2 == 0) {
                String conn;
                if (counter == 2) {
                    conn = (connectors[counter-2] + ":" + connectors[counter-1] + ":" + connectors[counter].charAt(0));
                } else {
                    conn = (connectors[counter-2].substring(3) + ":" + connectors[counter-1] + ":" + connectors[counter].charAt(0));
                }
                conns.add(conn);
            }
            counter++;

        }
        if (inoperative) {
            operationalWarning.setText("WARNING: This station may have inoperative chargers!");
        } else {
            operationalWarning.setText("");
        }
        return conns;
    }
    /**
     * Fills all fields with the selected station's information.
     * (With readability changes e.g true->yes).
     */
    private void fillFields() {
        addressField.setText(selectedStation.getAddress());
        nameField.setText(selectedStation.getName());
        operatorField.setText(selectedStation.getOperator());
        currentField.setText(selectedStation.getCurrentType());
        carparkField.setText(Integer.toString(selectedStation.getCarParkCount()));
        int time = selectedStation.getMaxTime();
        if (time == 0) {
            timeLimitField.setText("Unlimited");
        } else {
            timeLimitField.setText(Integer.toString(time));
        }
        boolean chargeCost = selectedStation.hasChargingCost();
        costField.setText(chargeCost ? "Yes" : "No");
        boolean parkCost = selectedStation.hasCarParkCost();
        parkingCostField.setText(parkCost ? "Yes" : "No");
        Note notes = noteDAO.getNoteFromStation(selectedStation, mainController.getCurrentUser());
        favouritedField.setText(notes.getFavourite() ? "Yes" : "No");
        if (notes.getRating() != 0) {
            ratingField.setText(Integer.toString(notes.getRating()));
        } else {
            ratingField.setText("Not yet rated");
        }

        ObservableList<String> conns = getConnsInnoperative();
        connectorsList.getItems().clear();
        connectorsList.getItems().addAll(conns);

    }

    /**
     * update selected station upon change.

     * @param selectedStation currently selected station.
     */
    public void updateSelectedStation(int selectedStation) {
        this.selectedStation = stationDAO.queryStation(selectedStation);
        fillFields();
    }

    /**
     * Initialises Selected station controller.

     * @param mainController the main controller.
     */
    public void init(MainController mainController) {
        this.mainController = mainController;
        if (selectedStation != null) {
            selectedStation = stationDAO.queryStation(mainController.getSelectedStation());
            fillFields();
        }
    }

}
