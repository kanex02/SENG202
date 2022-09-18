package journey.controller;

import com.opencsv.bean.CsvToBeanBuilder;
import journey.repository.DatabaseManager;
import journey.data.Station;
import journey.repository.StationDAO;
import journey.data.Utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class ReadCSV {
    private static final Logger log = LogManager.getLogger();


    public static void readStations() throws FileNotFoundException {
        FileReader file = new FileReader("src/main/resources/EV_Roam_charging_stations.csv");

        List<Station> beans = new CsvToBeanBuilder<Station>(file)
                .withType(Station.class)
                .build()
                .parse();

        for (Station s : beans) {
            String connectors = s.getConnectorsList();
            connectors = connectors.substring(1, connectors.length() - 1);
            String[] connectorsList = connectors.split("},\\{");
        }

        String maxTimeLimit = s.getMaxTimeLimit();
        int time = 0;
        if (Utils.isInt(maxTimeLimit)) {
            time = Integer.parseInt(maxTimeLimit);
        }

        s.setMaxTime(time);
        s.setConnectors(connectorsList);

        StationDAO stationDAO = new StationDAO();

        stationDAO.createStation(s.getOBJECTID(), s.getName(), s.getOperator(), s.getOwner(), s.getAddress(),
            s.isIs24Hours(), s.getCarParkCount(), s.isHasCarParkCost(), s.getMaxTime(),
            s.getHasTouristAttraction(), s.getLatitude(), s.getLongitude(), s.getCurrentType(),
            s.getDateFirstOperational(), s.getNumberOfConnectors(), s.getConnectors(), s.isHasChargingCost());
        }
    }

    /**
     * Reads CSV and calls set up methods in database manager
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        try {
            databaseManager.setup();
        } catch (SQLException | IOException e) {
            log.error(e);
        }
        readStations();
    }
}
