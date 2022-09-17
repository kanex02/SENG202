package journey.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class JourneyDAO {

    private final DatabaseManager databaseManager;
    private static User currentUser;
    private static final Logger log = LogManager.getLogger();
    private UserDAO userDAO;

    public JourneyDAO() {
        userDAO = new UserDAO();
        databaseManager = DatabaseManager.getInstance();
        currentUser = userDAO.getCurrentUser();
    }


    public QueryResult getJourneys() {
        Connection conn = null;
        ArrayList<Journey> res = new ArrayList<>();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM UserJourneys WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, currentUser.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Journey(rs.getString("start"), rs.getString("end"), rs.getString("vehicle_ID"),
                        rs.getInt("journey_ID")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Utils.closeConn(conn);
        QueryResult result = new QueryResult();
        result.setJourney(res.toArray(Journey[]::new));
        return result;
    }


    public void addJourney(Journey journey) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String insertQuery = "INSERT INTO userJourneys VALUES (?,?,?,?,?)";
            PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
            insertStatement.setInt(2, currentUser.getId()); // UserID set to 1 as no users exist yet.
            insertStatement.setString(3, journey.getVehicleID());
            insertStatement.setString(5, journey.getStart());
            insertStatement.setString(5, journey.getEnd());
            insertStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Utils.closeConn(conn);
    }


}
