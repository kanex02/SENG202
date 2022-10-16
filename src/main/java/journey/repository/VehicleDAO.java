package journey.repository;

import journey.data.User;
import journey.Utils;
import journey.data.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Concrete implementation of Database Access Object that handles all vehicle related actions to the database.
 */
public class VehicleDAO {
    private final DatabaseManager databaseManager;
    private static final Logger log = LogManager.getLogger();

    public VehicleDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Adds vehicle to Vehicle.

     * @param v username entered in login page
     */
    public void setVehicle(Vehicle v, User user) {
        Connection conn = null;
        boolean selected = getSelectedVehicle(user) == null;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE user_ID = ? AND Registration = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setInt(1, user.getId());
                ps.setString(2, v.getRegistration());
                ResultSet resultSet = ps.executeQuery();

                // If there is no item in result set we disconnect first and return an empty note
                if (!resultSet.isBeforeFirst()) {
                    String insertQuery = "INSERT INTO Vehicles VALUES (?,?,?,?,?,?,?,?)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, v.getRegistration());
                        insertStatement.setInt(2, user.getId());
                        insertStatement.setInt(3, v.getYear());
                        insertStatement.setString(4, v.getMake());
                        insertStatement.setString(5, v.getModel());
                        insertStatement.setString(6, v.getChargerType());
                        insertStatement.setString(7, v.getConnectorType());
                        insertStatement.setBoolean(8, selected);
                        insertStatement.execute();
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    /**
     * get all vehicles of the current user.

     * @return result ArrayList of all vehicles of the current user.
     */
    public Vehicle[] getVehicles(User user) {
        Connection conn = null;
        ArrayList<Vehicle> res = new ArrayList<>();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE User_ID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setInt(1, user.getId());
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    res.add(new Vehicle(rs.getInt("Year"), rs.getString("Make"),
                            rs.getString("Model"), rs.getString("ChargerType"),
                            rs.getString("Registration"), rs.getString("ConnectorType")));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return res.toArray(Vehicle[]::new);
    }

    /**
     * Gets selected Vehicle from database.

     * @param user Current user
     * @return Last selected Vehicle.
     */
    public Vehicle getSelectedVehicle(User user) {
        Connection conn = null;
        Vehicle v = null;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE User_ID = ? and selected = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setInt(1, user.getId());
                ps.setBoolean(2, true);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    v = new Vehicle(rs.getInt("Year"), rs.getString("Make"),
                            rs.getString("Model"), rs.getString("ChargerType"),
                            rs.getString("Registration"), rs.getString("ConnectorType"));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return v;
    }

    /**
     * Changes the selected Vehicle.

     * @param user Current User.
     * @param newSelection New selected Vehicle.
     */
    public void changeSelectedVehicle(User user, String newSelection) {
        Connection conn = null;
        Vehicle oldSelection = getSelectedVehicle(user);
        try {
            conn = databaseManager.connect();
            String sqlQuery = "UPDATE Vehicles SET selected = ? WHERE User_ID = ? and registration = ?";
            try (PreparedStatement ps1 = conn.prepareStatement(sqlQuery)) {
                ps1.setBoolean(1, true);
                ps1.setInt(2, user.getId());
                ps1.setString(3, newSelection);
                ps1.executeUpdate();

                if (oldSelection != null) {
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlQuery)) {
                        ps2.setBoolean(1, false);
                        ps2.setInt(2, user.getId());
                        ps2.setString(3, oldSelection.getRegistration());
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
    }

    /**
     * query the 'vehicles' table to get the vehicle with matching registration.

     * @param registration registration of the vehicle.
     * @return the vehicle object if the registration is found, null otherwise.
     */
    public Vehicle queryVehicle(String registration, int currentUser) {
        if (registration == null || registration.isBlank()) {
            return null;
        }
        Vehicle vehicle = null;
        Connection conn = null;
        try {
            String sqlQuery = "SELECT * FROM Vehicles WHERE registration = ? and user_ID = ?";
            conn = databaseManager.connect();
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setString(1, registration);
                ps.setInt(2, currentUser);
                ResultSet resultSet = ps.executeQuery();
                // Create a new station object.
                while (resultSet.next()) {
                    vehicle = new Vehicle(resultSet.getInt("year"), resultSet.getString("make"),
                            resultSet.getString("model"), resultSet.getString("chargerType"),
                            resultSet.getString("registration"), resultSet.getString("connectorType"));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return vehicle;
    }

    /**
     * Removes a vehicle from the database.

     * @param reg registration of vehicle to be removed.
     * @param userID User ID of owner of the vehicle.
     */
    public void removeVehicle(String reg, int userID) {
        Connection conn = null;
        try {
            String sqlQuery = "DELETE FROM Vehicles WHERE registration = ? and user_ID = ?";
            conn = databaseManager.connect();
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setString(1, reg);
                ps.setInt(2, userID);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

}
