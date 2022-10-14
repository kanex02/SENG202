package journey.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import journey.Utils;
import journey.data.Journey;
import journey.data.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * Concrete implementation of Database Access Object that handles all journey related actions to the database.
 */
public class JourneyDAO {

    private final DatabaseManager databaseManager;
    private static final Logger log = LogManager.getLogger();

    public JourneyDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Gets number of journeys from the database.

     * @return number of journeys in the database.
     */
    public int getNumberOfJourneys() {
        Connection conn = null;
        int count = 0;
        try {
            conn = databaseManager.connect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM Journeys");
            count = rs.getInt(1);
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return count;
    }

    /**
     * Gets all the journeys inputted by the user.

     * @param user current user.
     * @return a list of journeys submitted by the user.
     */
    public Journey[] getPlannedJourneys(User user) {
        Connection conn = null;
        ArrayList<Journey> res = new ArrayList<>();
        try {

            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Journeys WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Journey(rs.getInt("ID"),
                        rs.getString("vehicle_ID"), rs.getInt("user_ID"),
                        rs.getString("date"), rs.getString("start"),
                        rs.getString("end")));
            }
            for (Journey journey : res) {
                ArrayList<String> waypoints = new ArrayList<>();
                String stationsQuery = "SELECT * FROM JourneyWaypoints WHERE journey_ID = ? ORDER BY number";
                PreparedStatement preparedStatement = conn.prepareStatement(stationsQuery);
                preparedStatement.setInt(1, journey.getJourneyID());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    waypoints.add(resultSet.getString("waypoint"));
                }
                journey.setWaypoints(waypoints);
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return res.toArray(Journey[]::new);
    }

    /**
     * Adds a journey into the database.

     * @param journey journey to be added into the database.
     */
    public void addJourney(Journey journey) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String insertQuery = "INSERT INTO Journeys VALUES (?,?,?,?,?,?)";
            PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
            insertStatement.setString(2, journey.getStart());
            insertStatement.setString(3, journey.getEnd());
            insertStatement.setInt(4, journey.getUserID());
            insertStatement.setString(5, journey.getVehicleRegistration());
            insertStatement.setString(6, journey.getDate());
            insertStatement.execute();

            Statement statement = conn.createStatement();
            for (int i = 0; i < journey.getWaypoints().size(); i++) {
                String sql = "INSERT INTO JourneyWaypoints VALUES (" + getNumberOfJourneys() + ", '"
                        + journey.getWaypoints().get(i) + "', "
                        + i + ");";
                statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
    }

    /**
     * Deletes all the waypoints from a journey, then the journey itself.

     * @param journey journey to delete
     */
    public void deleteJourney(Journey journey) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            int id = journey.getJourneyID();
            conn.prepareStatement("DELETE FROM JourneyWaypoints WHERE journey_ID = " + id).execute();
            conn.prepareStatement("DELETE FROM Journeys WHERE ID = " + id).execute();
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
    }
}
