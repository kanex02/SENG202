package journey.repository;

import static journey.Utils.convertArrayToString;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import journey.Utils;
import journey.data.Station;
import journey.data.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


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
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
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

     * @param id station id
     * @param name station name
     * @param operator station operator
     * @param owner station owner
     * @param address station address
     * @param is24Hours whether station is open 24/7
     * @param carParkCount how many car parks station has
     * @param hasCarparkCost whether station carpark costs to park at
     * @param maxTimeLimit maximum time allowed at station
     * @param hasTouristAttraction whether there are tourist attractions nearby
     * @param latitude stations latitude
     * @param longitude stations longitude
     * @param currentType stations current type
     * @param dateFirstOperational date station was first operational
     * @param numberOfConnectors number of connectors available to charge with
     * @param connectorsList list of connectors
     * @param hasChargingCost cost of charging
     */
    public void createStation(int id, String name, String operator, String owner, String address,
                              Boolean is24Hours, int carParkCount, Boolean hasCarparkCost, int maxTimeLimit,
                              Boolean hasTouristAttraction, double latitude, double longitude, String currentType,
                              String dateFirstOperational, int numberOfConnectors, String[] connectorsList,
                              Boolean hasChargingCost) {
        //Creates new station in database.
        Connection conn = null;

        try {
            conn = databaseManager.connect();
            String sqlQuery = "INSERT INTO Stations VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.setString(3, operator);
            ps.setString(4, owner);
            ps.setString(5, address);
            ps.setBoolean(6, is24Hours);
            ps.setInt(7, carParkCount);
            ps.setBoolean(8, hasCarparkCost);
            ps.setInt(9, maxTimeLimit);
            ps.setBoolean(10, hasTouristAttraction);
            ps.setDouble(11, latitude);
            ps.setDouble(12, longitude);
            ps.setString(13, currentType);
            ps.setString(14, dateFirstOperational);
            ps.setInt(15, numberOfConnectors);
            ps.setString(16, convertArrayToString(connectorsList, "//"));
            ps.setBoolean(17, hasChargingCost);
            ps.execute();
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    /**
     * Inserts a station into the database.

     * @param station station to insert
     */
    public void insertStation(Station station) {
        createStation(station.getOBJECTID(),
                station.getName(),
                station.getOperator(),
                station.getOwner(),
                station.getAddress(),
                station.isIs24Hours(),
                station.getCarParkCount(),
                station.isHasCarParkCost(),
                station.getMaxTime(),
                station.getHasTouristAttraction(),
                station.getLatitude(),
                station.getLongitude(),
                station.getCurrentType(),
                station.getDateFirstOperational(),
                station.getNumberOfConnectors(),
                station.getConnectors(),
                station.isHasChargingCost());
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
            PreparedStatement ps = conn.prepareStatement(stationQuery);
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();
            Utils.insertRsIntoArray(rs, res);
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
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT DISTINCT operator FROM Stations");
            while (rs.next()) {
                operators.add(rs.getString("operator"));
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return operators;
    }

}
