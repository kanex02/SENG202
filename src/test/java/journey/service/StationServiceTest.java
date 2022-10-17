package journey.service;

import journey.ReadCSV;
import journey.Utils;
import journey.data.QueryStation;
import journey.data.Station;
import journey.data.User;
import journey.repository.DatabaseManager;
import journey.repository.StationDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationServiceTest {
    static StationDAO stationDAO;
    static User user;
    static StationsService stationsService;
    static DatabaseManager databaseManager;
    Connection conn;

    @BeforeAll
    static void init() {
        databaseManager = DatabaseManager.initialiseWithUrl("src/test/resources/test.db");
        stationDAO = new StationDAO();
        ReadCSV.readStations();
        user = new User("USER");
        user.setId(-1);
        stationsService = new StationsService(user);
    }

    @BeforeEach
    void setUp() {
        conn = databaseManager.connect();
    }

    @AfterEach
    void teardown() throws SQLException {
        databaseManager.setup();
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
        assertEquals("Name cannot have special characters or integers\n", error);
    }

    @Test
    void invalidTimeLimitTest() {
        String error = StationsService.errorCheck("test", "test", "test", "50");
        assertEquals("Time limit must be a number!\n", error);
    }

    @Test
    void invalidTimeLimitTwoTest() {
        String error = StationsService.errorCheck("test", "test", "-10", "50");
        assertEquals("Time limit must be a positive integer!\n", error);
    }

    @Test
    void invalidRangeTest() {
        String error = StationsService.errorCheck("test", "test", "100", "test");
        assertEquals("Range needs to be an integer!\n", error);
    }

    @Test
    void invalidRangeTwoTest() {
        String error = StationsService.errorCheck("test", "test", "100", "-10");
        assertEquals("Range needs to be a positive integer", error);
    }

    @Test
    void invalidRangeThreeTest() {
        String error = StationsService.errorCheck("test", "test", "100", "1600");
        assertEquals("Range must be within 1 - 1599!\n", error);
    }

    @Test
    void invalidAddressTest() {
        String error = StationsService.errorCheck("0.0#0.0", "test", "100", "50");
        assertEquals("Address does not exist!\n", error);
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
                break;
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
        assertEquals(0, stations.length);
    }

}
