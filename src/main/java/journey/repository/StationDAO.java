package journey.repository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import journey.Utils;
import journey.data.Station;
import journey.data.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.ArrayList;

import static journey.Utils.convertArrayToString;


/**
 * Concrete implementation of Database Access Object that handles all station related actions to the database.
 */
public class StationDAO {
    private final DatabaseManager databaseManager;
    private static final Logger log = LogManager.getLogger();

    public StationDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Query SQL database for a station of given id.

     * @param id stations id to find in database
     * @return station object assembled from database
     */
    public Station queryStation(int id) {
        Connection conn = null;
        try {
            String sqlQuery = "SELECT * FROM Stations WHERE ID = ?";
            conn = databaseManager.connect();
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();
                // Create a new station object.
                return new Station(resultSet.getInt("ID"),
                        resultSet.getString("name"), resultSet.getString("operator"),
                        resultSet.getString("owner"), resultSet.getString("address"),
                        resultSet.getBoolean("is24Hours"), resultSet.getInt("carParkCount"),
                        resultSet.getBoolean("hasCarParkCost"), resultSet.getInt("maxTimeLimit"),
                        resultSet.getBoolean("hasTouristAttraction"), resultSet.getFloat("latitude"),
                        resultSet.getFloat("longitude"), resultSet.getString("currentType"),
                        resultSet.getString("dateFirstOperational"),
                        resultSet.getInt("numberOfConnectors"),
                        (resultSet.getString("connectorsList")).split(":"),
                        resultSet.getBoolean("hasChargingCost"));
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return null;
    }

    /**
     * Inserts all features of the station into the database.
     * Is seperated into params instead of a station object for easy transfer from ReadCSV -> StationDAO

     * @param station the station to insert
     */
    public void insertStation(Station station) {
        int id = station.getObjectid();
        String name = station.getName();
        String operator = station.getOperator();
        String owner = station.getOwner();
        String address = station.getAddress();
        boolean is24Hours = station.isIs24Hours();
        int carParkCount = station.getCarParkCount();
        boolean hasCarparkCost = station.hasCarParkCost();
        int maxTimeLimit = station.getMaxTime();
        boolean hasTouristAttraction = station.getHasTouristAttraction();
        double latitude = station.getLatitude();
        double longitude = station.getLongitude();
        String currentType = station.getCurrentType();
        String dateFirstOperational = station.getDateFirstOperational();
        int numberOfConnectors = station.getNumberOfConnectors();
        String[] connectorsList = station.getConnectors();
        Boolean hasChargingCost = station.hasChargingCost();

        //Creates new station in database.
        Connection conn = null;

        try {
            conn = databaseManager.connect();
            String sqlQuery = "INSERT INTO Stations VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            try (PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)) {
                preparedStatement.setInt(1, id);
                preparedStatement.setString(2, name);
                preparedStatement.setString(3, operator);
                preparedStatement.setString(4, owner);
                preparedStatement.setString(5, address);
                preparedStatement.setBoolean(6, is24Hours);
                preparedStatement.setInt(7, carParkCount);
                preparedStatement.setBoolean(8, hasCarparkCost);
                preparedStatement.setInt(9, maxTimeLimit);
                preparedStatement.setBoolean(10, hasTouristAttraction);
                preparedStatement.setDouble(11, latitude);
                preparedStatement.setDouble(12, longitude);
                preparedStatement.setString(13, currentType);
                preparedStatement.setString(14, dateFirstOperational);
                preparedStatement.setInt(15, numberOfConnectors);
                preparedStatement.setString(16, convertArrayToString(connectorsList, "//"));
                preparedStatement.setBoolean(17, hasChargingCost);
                preparedStatement.execute();
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    /**
     * Get all stations from database.

     * @return result ArrayList of all stations in the database
     */
    public Station[] getAll(User user) {
        Connection conn = null;
        ArrayList<Station> res = new ArrayList<>();

        try {
            conn = databaseManager.connect();
            String stationQuery = "SELECT * FROM Stations "
                    + "left outer join (select * from Notes where user_ID = ?) on Stations.id = station_ID";
            try (PreparedStatement preparedStatement = conn.prepareStatement(stationQuery)) {
                preparedStatement.setInt(1, user.getId());
                ResultSet rs = preparedStatement.executeQuery();
                Utils.insertRsIntoArray(rs, res);
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return res.toArray(Station[]::new);
    }

    /**
     * Gets all operators for searching.

     * @return List of operators.
     */
    public ObservableList<String> getAllOperators() {
        Connection conn = null;
        ObservableList<String> operators =
                FXCollections.observableArrayList("");
        try {
            conn = databaseManager.connect();
            try (Statement statement = conn.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT DISTINCT operator FROM Stations");
                while (rs.next()) {
                    operators.add(rs.getString("operator"));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return operators;
    }

}
