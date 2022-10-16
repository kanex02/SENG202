package journey.service;

import journey.Utils;
import journey.data.QueryStation;
import journey.data.Station;
import journey.data.User;
import journey.repository.DatabaseManager;
import journey.repository.StationDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StationServiceTest {
    StationDAO stationDAO;
    User user;
    StationsService stationsService;
    DatabaseManager databaseManager;
    Connection conn;

    @BeforeEach
    void setUp() {
        stationDAO = new StationDAO();
        user = new User("USER");
        user.setId(-1);
        stationsService = new StationsService(user);
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
    }

    @AfterEach
    void teardown() throws SQLException {
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        Utils.closeConn(conn);
    }

    @Test //check that the fields that are cast to other types are done so properly
    void createQueryStationTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                null,
                null,
                "Yes",
                "100",
                "-42.66628070564927#171.96899414062503",
                "50",
                false);
        assertTrue(queryStation.getHasTouristAttraction() && queryStation.getMaxTime() == 100 && queryStation.getRange() == 50.0);
    }

    @Test
    void invalidNameTest() {
        String error = StationsService.errorCheck("test", "123", "100", "50");
        assertEquals(error, "Name cannot have special characters or integers\n");
    }

    @Test
    void invalidTimeLimitTest() {
        String error = StationsService.errorCheck("test", "test", "test", "50");
        assertEquals(error, "Time limit must be a number!\n");
    }

    @Test
    void invalidTimeLimitTwoTest() {
        String error = StationsService.errorCheck("test", "test", "-10", "50");
        assertEquals(error, "Time limit must be a positive integer!\n");
    }

    @Test
    void invalidRangeTest() {
        String error = StationsService.errorCheck("test", "test", "100", "test");
        assertEquals(error, "Range needs to be an integer!\n");
    }

    @Test
    void invalidRangeTwoTest() {
        String error = StationsService.errorCheck("test", "test", "100", "-10");
        assertEquals(error, "Range needs to be a positive integer");
    }

    @Test
    void invalidRangeThreeTest() {
        String error = StationsService.errorCheck("test", "test", "100", "1600");
        assertEquals(error, "Range must be within 1 - 1599!\n");
    }

    @Test
    void invalidAddressTest() {
        String error = StationsService.errorCheck("0.0#0.0", "test", "100", "50");
        assertEquals(error, "Address does not exist!\n");
    }

    @Test
    void fiterByNameTest() {
        QueryStation queryStation = StationsService.createQueryStation("YHA MT COOK",
                null,
                null,
                null,
                null,
                "",
                null,
                null,
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations[0].getName().contains("YHA MT COOK"));
    }

    @Test
    void fiterByOperatorTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                "MERIDIAN ENERGY LIMITED",
                null,
                null,
                null,
                "",
                null,
                null,
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations[0].getOperator().contains("MERIDIAN ENERGY LIMITED"));
    }

    @Test
    void fiterByCurrentTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                "AC",
                null,
                null,
                "",
                null,
                null,
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations[0].getCurrentType().contains("AC"));
    }

    @Test
    void fiterByConnectorsTest() {
        String[] conns = {"Type 2 Socketed"};
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                null,
                conns,
                null,
                "",
                null,
                null,
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        String[] foundConnectors = stations[0].getConnectors();
        boolean found = false;
        for (String connector : foundConnectors) {
            if (connector.contains("Type 2 Socketed")) {
                found = true;
            }
        }
        assertTrue(found);
    }

    @Test
    void fiterByAttractionsTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                null,
                null,
                "Yes",
                "",
                null,
                null,
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations[0].getHasTouristAttraction());
    }

    @Test
    void fiterByMaxTimeTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                null,
                null,
                null,
                "100",
                null,
                null,
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations[0].getMaxTime() <= 100);
    }

    @Test
    void fiterByAddressRangeTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                null,
                null,
                null,
                "",
                "-42.68243539838622#171.01867675781253",
                "10",
                false);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations[0].getName().contains("Hoki"));
    }

    @Test
    void fiterByFavouriteTest() {
        QueryStation queryStation = StationsService.createQueryStation(null,
                null,
                null,
                null,
                null,
                "",
                null,
                null,
                true);
        Station[] stations = stationsService.filterBy(queryStation);
        assertTrue(stations.length == 0);
    }

}
