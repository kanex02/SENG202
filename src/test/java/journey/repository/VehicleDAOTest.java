package journey.repository;

import java.util.ArrayList;
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
        Vehicle vehicle1 = new Vehicle(2019, "make", "model", "AC", "FakeRegUnique", "HDMI");
        User user1 = userDAO.setCurrentUser("Tester1");
        vehicleDAO.setVehicle(vehicle1, user1);

        Vehicle[] vehicles = vehicleDAO.getVehicles(user1);
        Vehicle v = vehicles[0];

        assertEquals(2019, v.getYear());

    }

    @Test
    void getVehiclesTest() {
        Vehicle vehicle = new Vehicle(2020, "make", "model", "DC", "FakeRegUnique1", "CHAdeMO");
        User currUser = userDAO.setCurrentUser("Tester");
        vehicleDAO.setVehicle(vehicle, currUser);

        Vehicle[] currUserVehicles = vehicleDAO.getVehicles(currUser);
        int year = currUserVehicles[0].getYear();
        String make = currUserVehicles[0].getMake();

        assertEquals(2020, year);
        assertEquals("make", make);
    }

    @Test
    void selectedVehicleNotNull() {

        Vehicle vehicle = new Vehicle(2020, "make", "model", "DC", "FakeRegUnique2", "CHAdeMO");
        User currUser = userDAO.setCurrentUser("Tester");

        vehicleDAO.setVehicle(vehicle, currUser);

        Vehicle selectedVehicle = vehicleDAO.getSelectedVehicle(currUser);
        assertNotNull(selectedVehicle);

    }

    @Test
    void selectedVehicleTest() {

        Vehicle vehicle = new Vehicle(2019, "make", "model", "AC", "FakeRegUnique3", "CHAdeMO");
        User currUser = userDAO.setCurrentUser("Tester");

        vehicleDAO.setVehicle(vehicle, currUser);

        Vehicle selectedVehicle = vehicleDAO.getSelectedVehicle(currUser);

        assertEquals(vehicle.getYear(), selectedVehicle.getYear());
    }

    @Test
    void getEmptySelectedStation() {

        Vehicle vehicle = new Vehicle(2020, "make", "model", "DC", "FakeRegUnique2", "CHAdeMO");
        User currUser = userDAO.setCurrentUser("Tester");

        vehicleDAO.setVehicle(vehicle, currUser);
        vehicleDAO.changeSelectedVehicle(currUser, vehicle.getRegistration());

        Vehicle selectedVehicle = vehicleDAO.getSelectedVehicle(currUser);

        assertNull(selectedVehicle);

    }

}