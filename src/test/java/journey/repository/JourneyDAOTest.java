package journey.repository;

import journey.data.Journey;
import journey.data.User;
import journey.data.Utils;
import journey.data.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class JourneyDAOTest {
    JourneyDAO journeyDAO;
    VehicleDAO vehicleDAO;
    UserDAO userDAO;
    User user;
    Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        journeyDAO = new JourneyDAO();
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        user = userDAO.setCurrentUser("Tester");
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
    }

    @AfterEach
    void tearDown() throws SQLException {
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
        Utils.closeConn(conn);
    }

    @Test
    void getJourneyNumbers() throws Exception {
        Vehicle vehicle = new Vehicle(2020, "make", "model", "AC", "FakeRegUnique");
        vehicleDAO.setVehicle(vehicle, user);
        int id1 = journeyDAO.getNumberOfJourneys();
        ArrayList<Integer> stations = new ArrayList<>();
        stations.add(1);
        journeyDAO.addJourney(new Journey("0#0", "0#0", vehicle.getRegistration(),
                user.getId(), "now", stations ));
        assertEquals(id1+1, journeyDAO.getNumberOfJourneys());
        assertEquals(1, journeyDAO.getJourneys(user).length);
    }
}