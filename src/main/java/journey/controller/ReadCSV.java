package journey.controller;

import com.opencsv.bean.CsvToBeanBuilder;
import journey.data.DatabaseManager;
import journey.data.Station;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class ReadCSV {


    public static boolean isInt(String str) {

        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static void readStations() throws FileNotFoundException {
        FileReader file = new FileReader("src/main/resources/EV_Roam_charging_stations.csv");

        List<Station> beans = new CsvToBeanBuilder<Station>(file)
                .withType(Station.class)
                .build()
                .parse();

        for (Station s : beans) {
            String connectors = s.getConnectorsList();
            connectors = connectors.substring(1, connectors.length()-1);
            String[] connectorsList = connectors.split("},\\{");

            String maxTimeLimit = s.getMaxTimeLimit();
            int time = 0;
            if (isInt(maxTimeLimit)) {
                time = Integer.parseInt(maxTimeLimit);
            }

            s.setMaxTime(time);
            s.setConnectors(connectorsList);

            DatabaseManager databaseManager = DatabaseManager.getInstance();

            databaseManager.createStation(s.getOBJECTID(), s.getName(), s.getOperator(), s.getOwner(), s.getAddress(),
                    s.isIs24Hours(), s.getCarParkCount(), s.isHasCarParkCost(), s.getMaxTime(),
                    s.getHasTouristAttraction(), s.getLatitude(), s.getLongitude(), s.getCurrentType(),
                    s.getDateFirstOperational(), s.getNumberOfConnectors(), s.getConnectors(), s.isHasChargingCost());
        }

    }

    public static void main(String[] args) throws FileNotFoundException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        databaseManager.setup();
        readStations();
    }
}
