package journey.repository;

import journey.data.Journey;
import journey.data.QueryResult;
import journey.data.User;
import journey.data.Utils;
import journey.repository.DatabaseManager;
import journey.repository.UserDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Statement;
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
                res.add(new Journey(rs.getString("start"), rs.getString("end"),
                        rs.getString("vehicle_ID"), rs.getInt("journey_ID"),
                        rs.getString("date")));
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
            String insertQuery = "INSERT INTO userJourneys VALUES (?,?,?,?,?,?)";
            PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
            insertStatement.setInt(2, currentUser.getId());
            insertStatement.setString(3, journey.getVehicle_ID());
            insertStatement.setString(4, journey.getStart());
            insertStatement.setString(5, journey.getEnd());
            insertStatement.setString(6, journey.getDate());
            insertStatement.execute();

            Statement statement = conn.createStatement();
            for (int i = 0; i < journey.getStations().size(); i++) {
                StringBuilder sql = new StringBuilder("INSERT INTO ");
                statement.addBatch("");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        Utils.closeConn(conn);
    }


}
