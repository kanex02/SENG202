package journey.repository;

import journey.data.QueryResult;
import journey.data.QueryStation;
import journey.data.Station;
import journey.Utils;
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

    @BeforeEach
    void setUp() {
        stationDAO = new StationDAO();
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
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
    void insertStation() throws SQLException {
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
    void testGetAll() throws SQLException {
        QueryResult qr = stationDAO.getAll();
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Stations");
        assertEquals(rs.getInt(1), qr.getStations().length);
    }

    @Test
    void testQueryEmpty() throws SQLException {
        QueryStation queryStation = new QueryStation();
        QueryResult stations = stationDAO.query(queryStation);
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        ResultSet rs = s.executeQuery("SELECT COUNT(*) FROM Stations");
        assertEquals(rs.getInt(1), stations.getStations().length);
    }

    @Test
    void testQuery1() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();

        s.execute("DELETE FROM Stations WHERE ID = -1");
        stationDAO.createStation(-1,
                "queryTest",
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
        QueryStation queryStation = new QueryStation();
        queryStation.setAddress("10 Downing Street");
        queryStation.setName("queryTest");
        queryStation.setOperator("rotarepo");
        queryStation.setMaxTime(120);
        queryStation.setCurrentType("AC");
        queryStation.setHasTouristAttraction(true);
        int id = stationDAO.query(queryStation).getStations()[0].getOBJECTID();
        assertEquals(-1, id);

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }

    @Test
    void testQuery2() throws SQLException {
        databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        stationDAO.createStation(-1,
                "queryTest",
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
        Station station = stationDAO.queryStation(-1);
        assertEquals("Krane", station.getOwner());

        s.execute("DELETE FROM Stations WHERE ID = -1");
    }
}