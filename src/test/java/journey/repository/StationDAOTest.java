package journey.repository;

import journey.data.QueryStation;
import journey.data.Station;
import journey.Utils;
import journey.data.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        stationDAO.createStation(-1,
                "insertStationTest",
                "rotarepo",
                "Krane",
                "10 Downing Street",
                true,
                4,
                true,
                120,
                true,
                0f,
                0f,
                "AC",
                "date",
                2,
                new String[]{""},
                true
        );
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
        assertEquals(false, rs.getBoolean("hasChargingCost"));

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
        QueryStation queryStation = new QueryStation();
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
        stationDAO.createStation(-1,
            "queryTest1",
            "rotarepo",
            "Krane",
            "10 Downing Street",
            true,
            4,
            true,
            120,
            true,
            0f,
            0f,
            "AC",
            "date",
            2,
            new String[] {""},
            true
        );
        Station station = stationDAO.queryStation(-1);
        assertEquals("Krane", station.getOwner());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void testQueryStation2() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        stationDAO.createStation(-1,
            "queryTest2",
            "rotarepo",
            "Krane",
            "10 Downing Street",
            true,
            4,
            true,
            120,
            true,
            0f,
            0f,
            "DC",
            "date",
            2,
            new String[] {""},
            true
        );
        Station station = stationDAO.queryStation(-1);
        assertEquals(false, station.getFavourite());
        assertEquals(0, station.getRating());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void testQueryStation3() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        stationDAO.createStation(-1,
            "queryTest3",
            "rotarepo",
            "Krane",
            "10 Downing Street",
            false,
            2,
            false,
            120,
            true,
            0f,
            0f,
            "AC",
            "date",
            2,
            new String[] {""},
            true
        );
        Station station = stationDAO.queryStation(-1);

        assertEquals(120, station.getMaxTime());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }
}