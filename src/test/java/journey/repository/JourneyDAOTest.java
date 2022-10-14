//package journey.repository;
//
//import journey.data.Journey;
//import journey.data.User;
//import journey.Utils;
//import journey.data.Vehicle;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.sql.Connection;
//import java.sql.SQLException;
//import java.sql.Statement;
//import java.util.ArrayList;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//class JourneyDAOTest {
//    JourneyDAO journeyDAO;
//    VehicleDAO vehicleDAO;
//    UserDAO userDAO;
//    User user;
//    Connection conn;
//    int id1;
//
//    @BeforeEach
//    void setUp() throws SQLException {
//        DatabaseManager databaseManager = DatabaseManager.getInstance();
//        conn = databaseManager.connect();
//        journeyDAO = new JourneyDAO();
//        vehicleDAO = new VehicleDAO();
//        userDAO = new UserDAO();
//        user = userDAO.setCurrentUser("Tester");
//        Statement s = conn.createStatement();
//        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
//    }
//
//    @BeforeEach
//    void createJourney() throws SQLException {
//        Vehicle vehicle = new Vehicle(2020, "make", "model", "AC", "FakeRegUnique", "HDMI");
//        vehicleDAO.setVehicle(vehicle, user);
//        id1 = journeyDAO.getNumberOfJourneys();
//        ArrayList<String> stations = new ArrayList<>();
//        stations.add("-43.73745#170.100913");
//        journeyDAO.addJourney(new Journey(vehicle.getRegistration(),
//                user.getId(), "now", stations));
//    }
//
//    @AfterEach
//    void tearDown() throws SQLException {
//        Statement s = conn.createStatement();
//        s.execute("DELETE FROM Users WHERE name = 'Tester'");
//        s.execute("DELETE FROM Vehicles WHERE registration = 'FakeRegUnique'");
//        Utils.closeConn(conn);
//    }
//
//    @Test
//    void getNumberOfJourneys() {
//        assertEquals(id1+1, journeyDAO.getNumberOfJourneys());
//    }
//
//    @Test
//    void getPlannedJourneys() {
//        assertEquals(1, journeyDAO.getPlannedJourneys(user).length);
//    }
//
//
////    @Test
////    void completeAJourney() {
////        assertEquals(0, journeyDAO.getCompletedJourneys(user).length);
////        journeyDAO.completeAJourney(journeyDAO.getPlannedJourneys(user)[0].getJourneyID(), user.getId());
////        assertEquals(1, journeyDAO.getCompletedJourneys(user).length);
////    }
//
////    @Test
////    void getCompletedJourneys() {
////        journeyDAO.completeAJourney(journeyDAO.getPlannedJourneys(user)[0].getJourneyID(), user.getId());
////        assertEquals(1, journeyDAO.getCompletedJourneys(user).length);
////    }
//
//}