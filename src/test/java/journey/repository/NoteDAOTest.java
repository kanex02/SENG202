package journey.repository;

import journey.data.Note;
import journey.data.Station;
import journey.data.User;
import journey.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class NoteDAOTest {
    private StationDAO stationDAO;
    private NoteDAO noteDAO;
    private UserDAO userDAO;
    private Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        stationDAO = new StationDAO();
        noteDAO = new NoteDAO();
        userDAO = new UserDAO();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        s.execute("DELETE FROM Notes WHERE station_ID = -1");
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
    }

    @AfterEach
    void tearDown() throws SQLException {
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        s.execute("DELETE FROM Notes WHERE station_ID = -1");
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
        Utils.closeConn(conn);
    }

    @Test
    void getSetNote() {
        User user = userDAO.setCurrentUser("Tester");
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
        station.setLatitude(0d);
        station.setLongitude(0d);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(1);
        station.setFavourite(false);

        stationDAO.insertStation(station);
        Note note = new Note(station, "testing123", 0, false);
        noteDAO.setNote(note, user);
        Note result = noteDAO.getNoteFromStation(station, user);
        assertEquals("testing123", result.getNote());
    }

    @Test
    void getSetExistingNote() {
        User user = userDAO.setCurrentUser("Tester");
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
        station.setLatitude(0d);
        station.setLongitude(0d);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(3);
        station.setFavourite(true);
        stationDAO.insertStation(station);
        Note note = new Note(station, "firstNote", 0, false);
        noteDAO.setNote(note, user);
        Note newNote = new Note(station, "secondNote", 3, true);
        noteDAO.setNote(newNote, user);
        Note result = noteDAO.getNoteFromStation(station, user);
        assertEquals("secondNote", result.getNote());
    }

    @Test
    void getNoNote() {
        User user = userDAO.setCurrentUser("Tester");
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
        station.setLatitude(0d);
        station.setLongitude(0d);
        station.setCurrentType("AC");
        station.setDateFirstOperational("date");
        station.setNumberOfConnectors(2);
        station.setConnectors(new String[]{""});
        station.setHasChargingCost(true);
        station.setRating(1);
        station.setFavourite(false);
        stationDAO.insertStation(station);
        Note result = noteDAO.getNoteFromStation(station, user);
        assertNull(result.getNote());
    }
}