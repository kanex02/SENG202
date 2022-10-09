package journey.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import journey.data.Station;
import journey.repository.StationDAO;;

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
    @FXML private ListView<String> attractionsList;

    private MainController mainController;
    private StationDAO stationDAO = new StationDAO();
    private Station selectedStation;

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
        if (chargeCost) {
            costField.setText("Yes");
        } else {
            costField.setText("No");
        }
        boolean parkCost = selectedStation.getHasCarParkCost();
        if (parkCost) {
            parkingCostField.setText("Yes");
        } else {
            parkingCostField.setText("No");
        }
        //TODO: wait for tom for the userstations table in the database
    }

    public void updateSelectedStation(int selectedStation) {
        this.selectedStation = stationDAO.queryStation(selectedStation);
        fillFields();
    }

    public void init(MainController mainController) {
        this.mainController = mainController;
        if (selectedStation != null) {
            selectedStation = stationDAO.queryStation(mainController.getSelectedStation());
            fillFields();
        }
    }

}
