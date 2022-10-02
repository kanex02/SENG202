package journey.repository;

import journey.data.QueryResult;
import journey.data.User;
import journey.Utils;
import journey.data.Vehicle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

class VehicleDAOTest {
    UserDAO userDAO;
    VehicleDAO vehicleDAO;
    Connection conn;

    @BeforeEach
    void setUp() throws SQLException {
        DatabaseManager databaseManager = DatabaseManager.getInstance();
        conn = databaseManager.connect();
        vehicleDAO = new VehicleDAO();
        userDAO = new UserDAO();
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
    }

    @AfterEach
    void tearDown() throws SQLException {
        Statement s = conn.createStatement();
        s.execute("DELETE FROM Stations WHERE ID = -1");
        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
        s.execute("DELETE FROM Users WHERE name = 'Tester'");
        s.execute("DELETE FROM Users WHERE name = 'Tester1'");
        Utils.closeConn(conn);
    }

    @Test
    void setVehicleTwice() {
        Vehicle vehicle = new Vehicle(2020, "make", "model", "AC", "FakeRegUnique", "HDMI");
        User user = userDAO.setCurrentUser("Tester");
        vehicleDAO.setVehicle(vehicle, user);
        User user1 = userDAO.setCurrentUser("Tester1");
        vehicleDAO.setVehicle(vehicle, user1);
    }
}