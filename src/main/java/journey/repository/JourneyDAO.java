package journey.repository;

import journey.data.Journey;
import journey.data.User;
import journey.data.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

public class JourneyDAO {

    private final DatabaseManager databaseManager;
    private static final Logger log = LogManager.getLogger();
    private final UserDAO userDAO;

    public JourneyDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    public int getNumberOfJourneys() {
        Connection conn = null;
        int count = 0;
        try {
            conn = databaseManager.connect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM Journeys");
            count = rs.getInt(1);
        } catch (Exception e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }

        return count;
    }


    public Journey[] getJourneys(User user) {
        Connection conn = null;
        ArrayList<Journey> res = new ArrayList<>();
        try {

            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Journeys WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, user.getId());
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


    public void addJourney(Journey journey) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String insertQuery = "INSERT INTO Journeys VALUES (?,?,?,?,?,?,?)";
            PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
            insertStatement.setInt(3, journey.getUserID());
            insertStatement.setString(4, journey.getVehicle_ID());
            insertStatement.setString(5, journey.getStart());
            insertStatement.setString(6, journey.getEnd());
            insertStatement.setString(7, journey.getDate());
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
            System.out.println(e.getMessage());
        } finally {
            Utils.closeConn(conn);
        }
    }


}
