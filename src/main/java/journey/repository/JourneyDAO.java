package journey.repository;

import journey.data.Journey;
import journey.data.User;
import journey.Utils;
import journey.data.Vehicle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

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
     * Gets number of journeys from the database

     * @return number of journeys in the database
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
     * Gets all the journeys inputted by the user

     * @param user current user
     * @return a list of journeys submitted by the user
     */
    public Journey[] getPlannedJourneys(User user) {
        Connection conn = null;
        ArrayList<Journey> res = new ArrayList<>();
        try {

            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Journeys WHERE User_ID = ? and completed = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, user.getId());
            ps.setBoolean(2, false);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Journey(rs.getInt("ID"), rs.getString("start"), rs.getString("end"),
                        rs.getString("vehicle_ID"), rs.getInt("user_ID"),
                        rs.getString("date")));
            }
            for (Journey journey : res) {
                ArrayList<Integer> stations = new ArrayList<>();
                String stationsQuery = "SELECT * FROM JourneyStations WHERE journey_ID = ? ORDER BY number";
                PreparedStatement preparedStatement = conn.prepareStatement(stationsQuery);
                preparedStatement.setInt(1, journey.getJourneyID());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    stations.add(resultSet.getInt("station_ID"));
                }
                journey.setStations(stations);
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return res.toArray(Journey[]::new);
    }


    public void completeAJourney(int journeyID, int currentUser) {
        Connection conn = null;
        Journey journey = queryJourney(journeyID, currentUser);
        journey.setDate(Utils.getDate());
        try {
            conn = databaseManager.connect();
            String insertQuery = "INSERT INTO Journeys VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
            insertStatement.setInt(3, journey.getUserID());
            insertStatement.setString(4, journey.getVehicle_ID());
            insertStatement.setString(5, journey.getStart());
            insertStatement.setString(6, journey.getEnd());
            insertStatement.setString(7, journey.getDate());
            insertStatement.setBoolean(8, true);
            insertStatement.execute();

            Statement statement = conn.createStatement();
            for (int i = 0; i < journey.getStations().size(); i++) {
                String sql = "INSERT INTO JourneyStations VALUES (" + getNumberOfJourneys() + "," +
                        journey.getStations().get(i) + "," +
                        i + ")";
                statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }


    public Journey[] getCompletedJourneys(User user) {
        Connection conn = null;
        ArrayList<Journey> res = new ArrayList<>();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Journeys WHERE User_ID = ? and completed = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, user.getId());
            ps.setBoolean(2, true);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(new Journey(rs.getInt("ID"), rs.getString("start"), rs.getString("end"),
                        rs.getString("vehicle_ID"), rs.getInt("user_ID"),
                        rs.getString("date")));
            }
            for (Journey journey : res) {
                ArrayList<Integer> stations = new ArrayList<>();
                String stationsQuery = "SELECT * FROM JourneyStations WHERE journey_ID = ? ORDER BY number";
                PreparedStatement preparedStatement = conn.prepareStatement(stationsQuery);
                preparedStatement.setInt(1, journey.getJourneyID());
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    stations.add(resultSet.getInt("station_ID"));
                }
                journey.setStations(stations);
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return res.toArray(Journey[]::new);
    }

    /**
     * adds a journey into the database

     * @param journey journey to be added into the database
     */
    public void addJourney(Journey journey) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String insertQuery = "INSERT INTO Journeys VALUES (?,?,?,?,?,?,?,?)";
            PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
            insertStatement.setInt(3, journey.getUserID());
            insertStatement.setString(4, journey.getVehicle_ID());
            insertStatement.setString(5, journey.getStart());
            insertStatement.setString(6, journey.getEnd());
            insertStatement.setString(7, journey.getDate());
            insertStatement.setBoolean(8, false);
            insertStatement.execute();

            Statement statement = conn.createStatement();
            for (int i = 0; i < journey.getStations().size(); i++) {
                String sql = "INSERT INTO JourneyStations VALUES (" + getNumberOfJourneys() + "," +
                        journey.getStations().get(i) + "," +
                        i + ")";
                statement.addBatch(sql);
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    public Journey queryJourney(int journeyID, int currentUser) {
        Connection conn = null;
        try {
            String sqlQuery = "SELECT * FROM Journeys WHERE ID = ? and user_ID = ?";
            conn = databaseManager.connect();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, journeyID);
            ps.setInt(2, currentUser);
            ResultSet resultSet = ps.executeQuery();
            // Create a new journey object.
            Journey journey = new Journey(resultSet.getInt("ID"), resultSet.getString("start"),
                    resultSet.getString("end"), resultSet.getString("vehicle_ID"),
                    resultSet.getInt("user_ID"), resultSet.getString("date"));
            ArrayList<Integer> stations = new ArrayList<>();
            String sqlQuery2 = "SELECT station_ID FROM JourneyStations WHERE journey_ID = ?";
            PreparedStatement ps2 = conn.prepareStatement(sqlQuery2);
            ps2.setInt(1, journey.getJourneyID());
            ResultSet resultSet2 = ps2.executeQuery();
            while (resultSet2.next()) {
                stations.add(resultSet2.getInt("station_id"));
            }
            journey.setStations(stations);
            return journey;

        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return null;
    }


}
