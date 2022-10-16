package journey.repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import journey.data.Station;
import journey.Utils;
import journey.data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StationDAOTest {
    StationDAO stationDAO;
    DatabaseManager databaseManager;
    Connection conn;
    User user;

    @BeforeEach
    void setUp() {
        stationDAO = new StationDAO();
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        user = new User("USER");
        user.setId(-1);
    }

    @AfterEach
    void tearDown() throws SQLException {
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        Utils.closeConn(conn);
    }

    //Check that the database has data
    @Test
    void queryStation() {
        Station station = stationDAO.queryStation(1);
        assertNotNull(station);
    }

    @Test
    void createStation() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();

        //Using -1 for ID to avoid clashes
        s.execute("DELETE FROM Stations WHERE ID = -1");
        Station station = new Station();
        station.setObjectid(-1);
        station.setName("insertStationTest");
        station.setOperator("rotarepo");
        station.setOwner("Krane");
        station.setAddress("10 Downing Street");
        station.setIs24Hours(true);
        station.setCarParkCount(4);
        station.setHasCarParkCost(true);
        station.setMaxTime(120);
        station.setHasTouristAttraction(true);
        station.setLatitude(0f);
        station.setLongitude(0f);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(0);
        station.setFavourite(false);
        stationDAO.insertStation(station);
        ResultSet rs = s.executeQuery("SELECT * FROM Stations WHERE ID = -1");
        rs.next();
        assertEquals("Krane", rs.getString("owner"));
        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void insertStation() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();

        // Using -1 for ID to avoid clashes
        s.execute("DELETE FROM Stations WHERE ID = -1");

        Station station = new Station(-1,
            "insertStationTest",
            "rotarepo",
            "Krane",
            "11 Downing Street",
            true,
            3,
            false,
            80,
            false,
            0f,
            0f,
            "AC",
            "date",
            4,
            new String[]{""},
            false);

        stationDAO.insertStation(station);

        ResultSet rs = s.executeQuery("SELECT * FROM Stations WHERE ID = -1");
        rs.next();

        assertEquals("Krane", rs.getString("owner"));
        assertFalse(rs.getBoolean("hasChargingCost"));

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void testGetAll() throws SQLException {
        Station[] stations = stationDAO.getAll(user);
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Stations");
        assertEquals(rs.getInt(1), stations.length);
    }

    @Test
    void testQueryEmpty() throws SQLException {
        Station[] stations = stationDAO.getAll(user);
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Stations");
        assertEquals(rs.getInt(1), stations.length);
    }

    @Test
    void testQueryStation() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        Station station = new Station();
        station.setObjectid(-1);
        station.setName("insertStationTest");
        station.setOperator("rotarepo");
        station.setOwner("Krane");
        station.setAddress("10 Downing Street");
        station.setIs24Hours(true);
        station.setCarParkCount(4);
        station.setHasCarParkCost(true);
        station.setMaxTime(120);
        station.setHasTouristAttraction(true);
        station.setLatitude(0f);
        station.setLongitude(0f);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(0);
        station.setFavourite(false);
        stationDAO.insertStation(station);
        Station stationRes = stationDAO.queryStation(-1);
        assertEquals("Krane", stationRes.getOwner());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void testQueryStation2() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        Station station = new Station();
        station.setObjectid(-1);
        station.setName("insertStationTest");
        station.setOperator("rotarepo");
        station.setOwner("Krane");
        station.setAddress("10 Downing Street");
        station.setIs24Hours(true);
        station.setCarParkCount(4);
        station.setHasCarParkCost(true);
        station.setMaxTime(120);
        station.setHasTouristAttraction(true);
        station.setLatitude(0f);
        station.setLongitude(0f);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(0);
        station.setFavourite(false);
        stationDAO.insertStation(station);
        Station stationRes = stationDAO.queryStation(-1);
        assertFalse(stationRes.getFavourite());
        assertEquals(0, stationRes.getRating());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void testQueryStation3() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        Station station = new Station();
        station.setObjectid(-1);
        station.setName("insertStationTest");
        station.setOperator("rotarepo");
        station.setOwner("Krane");
        station.setAddress("10 Downing Street");
        station.setIs24Hours(true);
        station.setCarParkCount(4);
        station.setHasCarParkCost(true);
        station.setMaxTime(120);
        station.setHasTouristAttraction(true);
        station.setLatitude(0f);
        station.setLongitude(0f);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(0);
        station.setFavourite(false);
        stationDAO.insertStation(station);
        Station stationRes = stationDAO.queryStation(-1);

        assertEquals(120, stationRes.getMaxTime());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }
}