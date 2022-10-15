package journey.repository;

import journey.data.Journey;
import journey.data.User;
import journey.Utils;
import journey.data.Vehicle;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JourneyDAOTest {
    static DatabaseManager databaseManager;
    static JourneyDAO journeyDAO;
    static VehicleDAO vehicleDAO;
    static UserDAO userDAO;
    static User user;
    static Vehicle vehicle;
    static Journey journey;

    @BeforeAll
    static void initialise() {
        databaseManager = DatabaseManager.getInstance();
        journeyDAO = new JourneyDAO();
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        user = userDAO.setCurrentUser("Tester");
        vehicle = new Vehicle(2020, "make", "model", "AC", "FakeRegUnique", "HDMI");
        vehicleDAO.setVehicle(vehicle, user);
        ArrayList<String> stations = new ArrayList<>();
        stations.add("-43.73#170.10");
        stations.add("-43.76#170.13");
        journey = new Journey(vehicle.getRegistration(), user.getId(), "now", stations);
    }

    @BeforeEach
    void createJourney() {
        journeyDAO.addJourney(journey);
        journey = journeyDAO.getPlannedJourneys(user)[0];
    }

    @AfterEach
    void tearDown() {
        journeyDAO.deleteJourney(journey);
    }

    @AfterAll
    static void closeDown() throws SQLException {
        Connection conn = databaseManager.connect();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
        Utils.closeConn(conn);
    }

    @Test
    void waypointsEntered() throws SQLException {
        Connection conn = databaseManager.connect();
        int count = conn.prepareStatement("SELECT COUNT(*) FROM JourneyWaypoints WHERE journey_ID = "
                        + journey.getJourneyID())
                .executeQuery().getInt(1);
        Utils.closeConn(conn);
        assertEquals(2, count);
    }

    @Test
    void getPlannedJourneys() {
        assertEquals(1, journeyDAO.getPlannedJourneys(user).length);
    }
}