package journey;

import com.opencsv.bean.CsvToBeanBuilder;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import journey.data.Station;
import journey.repository.DatabaseManager;
import journey.repository.StationDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import journey.repository.DatabaseManager;

/**
 * Class to read data from a CSV into the database.
 */
public class ReadCSV {
    private static final Logger log = LogManager.getLogger();

    /**
     * Imports data into the database.
     */
    public static void readStations() {
        FileReader file = null;
        try {
            file = new FileReader("src/main/resources/EV_Roam_charging_stations.csv");
        } catch (FileNotFoundException e) {
            log.error(e);
        }

        assert file != null;
        List<Station> beans = new CsvToBeanBuilder<Station>(file)
            .withType(Station.class)
            .build()
            .parse();

        for (Station s : beans) {
            String connectors = s.getConnectorsList();
            connectors = connectors.substring(1, connectors.length() - 1);
            String[] connectorsList = connectors.split("},\\{");


            String maxTimeLimit = s.getMaxTimeLimit();
            int time = 0;
            if (Utils.isInt(maxTimeLimit)) {
                time = Integer.parseInt(maxTimeLimit);
            }

            s.setMaxTime(time);
            s.setConnectors(connectorsList);

            s.setRating(0);
            s.setFavourite(false);

            StationDAO stationDAO = new StationDAO();

            stationDAO.createStation(s.getOBJECTID(),
                    s.getName(),
                    s.getOperator(),
                    s.getOwner(),
                    s.getAddress(),
                    s.isIs24Hours(),
                    s.getCarParkCount(),
                    s.isHasCarParkCost(),
                    s.getMaxTime(),
                    s.getHasTouristAttraction(),
                    s.getLatitude(),
                    s.getLongitude(),
                    s.getCurrentType(),
                    s.getDateFirstOperational(),
                    s.getNumberOfConnectors(),
                    s.getConnectors(),
                    s.isHasChargingCost(),
                    s.getRating(),
                    s.getFavourite());
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
//        try {
//            databaseManager.setup();
//            readStations();
//        } catch (Exception e) {
//            System.out.println("BAD");
//            log.error(e);
//        }
    }


}
