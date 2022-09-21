package journey.repository;

import journey.data.Note;
import journey.data.Station;
import journey.data.User;
import journey.data.Utils;
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
        Station station = new Station(-1,
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
        stationDAO.insertStation(station);
        Note note = new Note(station, "testing123");
        noteDAO.setNote(note, user);
        Note result = noteDAO.getNoteFromStation(station, user);
        assertEquals("testing123", result.getNote());
    }

    @Test
    void getSetExistingNote() {
        User user = userDAO.setCurrentUser("Tester");
        Station station = new Station(-1,
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
        stationDAO.insertStation(station);
        Note note = new Note(station, "testing123");
        noteDAO.setNote(note, user);
        Note newNote = new Note(station, "testing234");
        noteDAO.setNote(newNote, user);
        Note result = noteDAO.getNoteFromStation(station, user);
        assertEquals("testing234", result.getNote());
    }

    @Test
    void getNoNote() {
        User user = userDAO.setCurrentUser("Tester");
        Station station = new Station(-1,
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
        stationDAO.insertStation(station);
        Note result = noteDAO.getNoteFromStation(station, user);
        assertNull(result.getNote());
    }
}