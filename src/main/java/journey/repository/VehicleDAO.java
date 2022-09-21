package journey.repository;

import journey.data.QueryResult;
import journey.data.User;
import journey.data.Utils;
import journey.data.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
/**
 * Concrete implementation of Database Access Object that handles all vehicle related actions to the database
 */
public class VehicleDAO {
    private final DatabaseManager databaseManager;
    UserDAO userDAO;
    private static final Logger log = LogManager.getLogger();

    public VehicleDAO() {
        databaseManager = DatabaseManager.getInstance();
        userDAO = new UserDAO();
    }

    /**
     * Adds vehicle to Vehicle

     * @param v username entered in login page
     * @throws Exception Duplicate vehicle entry

     */
    public void setVehicle(Vehicle v, User user) throws Exception {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE user_ID = ? AND Registration = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, user.getId());
            ps.setString(2, v.getRegistration());
            ResultSet resultSet = ps.executeQuery();

            // If there is no item in result set we disconnect first and return an empty note
            if(!resultSet.isBeforeFirst()) {
                String insertQuery = "INSERT INTO Vehicles VALUES (?,?,?,?,?,?)";
                PreparedStatement insertStatement = conn.prepareStatement(insertQuery);
                insertStatement.setString(1, v.getRegistration());
                insertStatement.setInt(2, user.getId());
                insertStatement.setInt(3, v.getYear());
                insertStatement.setString(4, v.getMake());
                insertStatement.setString(5, v.getModel());
                insertStatement.setString(6, v.getChargerType());
                insertStatement.execute();
                // Insert into list of vehicles for current user
                user.newVehicle(v);
            }
        } catch(SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    /**
     * get all vehicles of the current user
     * @return result ArrayList of all vehicles of the current user
     */
    public QueryResult getVehicles(User user) {
        Connection conn = null;
        ArrayList<Vehicle> res = new ArrayList<>();
        QueryResult result = new QueryResult();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Vehicle(rs.getInt("Year"), rs.getString("Make"),
                        rs.getString("Model"), rs.getString("ChargerType"),
                        rs.getString("Registration")));
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
            result.setVehicles(res.toArray(Vehicle[]::new));
        }
        return result;
    }
}
