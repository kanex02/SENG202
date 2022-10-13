package journey.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
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
    @FXML private Label connectorField;
    @FXML private Label carparkField;
    @FXML private Label timeLimitField;
    @FXML private Label costField;
    @FXML private Label parkingCostField;
    @FXML private Label ratingField;
    @FXML private Label favoritedField;
//    @FXML private ListView<String> attractionsList;

    private final StationDAO stationDAO = new StationDAO();
    private final NoteDAO noteDAO = new NoteDAO();
//    private AttractionsService attractionsService = new AttractionsService();
    private MainController mainController;
    private Station selectedStation;

    /**
     * Fills all fields with the selected station's information.
     * (With readability changes e.g true->yes).
     */
    public void fillFields() {
        addressField.setText(selectedStation.getAddress());
        nameField.setText(selectedStation.getName());
        operatorField.setText(selectedStation.getOperator());
        currentField.setText(selectedStation.getCurrentType());
        connectorField.setText(Integer.toString(selectedStation.getNumberOfConnectors()));
        carparkField.setText(Integer.toString(selectedStation.getCarParkCount()));
        int time = selectedStation.getMaxTime();
        if (time == 0) {
            timeLimitField.setText("Unlimited");
        } else {
            timeLimitField.setText(Integer.toString(time));
        }
        boolean chargeCost = selectedStation.getHasChargingCost();
        costField.setText(chargeCost ? "Yes":"No");
        boolean parkCost = selectedStation.getHasCarParkCost();
        parkingCostField.setText(parkCost ? "Yes":"No");
        Note notes = noteDAO.getNoteFromStation(selectedStation, mainController.getCurrentUser());
        favoritedField.setText(notes.getFavourite() ? "Yes":"No");
        if (notes.getRating() != 0) {
            ratingField.setText(Integer.toString(notes.getRating()));
        } else {
            ratingField.setText("Not yet rated");
        }
//        ArrayList<Feature> features = null;
//        try {
//            features = attractionsService.getAttractions(selectedStation.getLatitude(), selectedStation.getLongitude(), 2000);
//        } catch (IOException e) {
//            log.error(e);
//        }
//        ObservableList<String> attractions = FXCollections.observableArrayList();
//        for (Feature feature : features) {
//
//            attractions.add(feature.getFormattedName());
//        }
//        attractionsList.setItems(attractions);
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
