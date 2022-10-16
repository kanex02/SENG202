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
            try (PreparedStatement preparedStatement  = conn.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, user.getId());
                ResultSet rs = preparedStatement.executeQuery();
                while (rs.next()) {
                    res.add(new Journey(rs.getInt("ID"),
                            rs.getString("vehicle_ID"), rs.getInt("user_ID"),
                            rs.getString("date"), rs.getString("start"),
                            rs.getString("end")));
                }
            }
            for (Journey journey : res) {
                ArrayList<String> waypoints = new ArrayList<>();
                String stationsQuery = "SELECT * FROM JourneyWaypoints WHERE journey_ID = ? ORDER BY number";
                try (PreparedStatement preparedStatement = conn.prepareStatement(stationsQuery)) {
                    preparedStatement.setInt(1, journey.getJourneyID());
                    ResultSet resultSet = preparedStatement.executeQuery();
                    while (resultSet.next()) {
                        waypoints.add(resultSet.getString("waypoint"));
                    }
                    journey.setWaypoints(waypoints);
                }
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
            try (PreparedStatement preparedStatement  = conn.prepareStatement(insertQuery)) {
                preparedStatement.setString(2, journey.getStart());
                preparedStatement.setString(3, journey.getEnd());
                preparedStatement.setInt(4, journey.getUserID());
                preparedStatement.setString(5, journey.getVehicleRegistration());
                preparedStatement.setString(6, journey.getDate());
                preparedStatement.execute();
            }
            int id;
            try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT last_insert_rowid()")) {
                ResultSet rs = preparedStatement.executeQuery();
                id = rs.getInt(1);
            }

            try (Statement statement = conn.createStatement()) {
                for (int i = 0; i < journey.getWaypoints().size(); i++) {
                    String sql = "INSERT INTO JourneyWaypoints VALUES (" + id + ", '"
                            + journey.getWaypoints().get(i) + "', "
                            + i + ");";
                    statement.addBatch(sql);
                }
                statement.executeBatch();
            }
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
            try (PreparedStatement preparedStatement  = conn.prepareStatement(
                    "DELETE FROM JourneyWaypoints WHERE journey_ID = " + id)) {
                preparedStatement.execute();
            }
            try (PreparedStatement preparedStatement = conn.prepareStatement(
                    "DELETE FROM Journeys WHERE ID = " + id
            )) {
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
    }
}
