package journey.repository;

import journey.data.QueryResult;
import journey.data.Utils;
import journey.data.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VehicleDAO {
    private final DatabaseManager databaseManager;
    UserDAO userDAO;
    private static final Logger log = LogManager.getLogger();

    public VehicleDAO() {
        databaseManager = DatabaseManager.getInstance();
        userDAO = new UserDAO();
    }

    public void setVehicle(Vehicle v) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE user_ID = ? AND Registration = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, userDAO.getCurrentUser().getId());
            ps.setString(2, v.getRegistration());
            ResultSet resultSet = ps.executeQuery();

            // If there is no item in result set we disconnect first and return an empty note
            if(!resultSet.isBeforeFirst()) {

                String insertQuery = "INSERT INTO Vehicles VALUES (?,?,?,?,?,?)";
                PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
                insertStatement.setString(1, v.getRegistration());
                insertStatement.setInt(2, userDAO.getCurrentUser().getId());
                insertStatement.setInt(3, v.getYear());
                insertStatement.setString(4, v.getMake());
                insertStatement.setString(5, v.getModel());
                insertStatement.setString(6, v.getChargerType());

                insertStatement.execute();

                // Insert into list of vehicles for current user
                userDAO.getCurrentUser().newVehicle(v);

            } else { // TODO: Handle error if vehicle already exists
                System.out.println("bad error");
            }

        } catch(SQLException e) {

            e.printStackTrace();
        } finally {
            Utils.closeConn(conn);
        }
    }

    public QueryResult getVehicles() {
        Connection conn = null;
        ArrayList<Vehicle> res = new ArrayList<Vehicle>();
        QueryResult result = new QueryResult();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, userDAO.getCurrentUser().getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Vehicle(rs.getInt("Year"), rs.getString("Make"),
                        rs.getString("Model"), rs.getString("ChargerType"),
                        rs.getString("Registration")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            Utils.closeConn(conn);
            result.setVehicles(res.toArray(Vehicle[]::new));
        }
        return result;
    }
}
