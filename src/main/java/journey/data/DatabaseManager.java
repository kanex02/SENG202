package journey.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

import static journey.data.Utils.convertArrayToString;

/**
 * Static utility class to make queries to the database.
 * TODO: Exception handler for fatal exceptions
 */
public final class DatabaseManager {
    private final String databasePath;
    private User currentUser = null;
    private static final Logger log = LogManager.getLogger();
    private static DatabaseManager instance = null;

    /**
     * Constructs a new database manager.
     */
    private DatabaseManager() {
        this.databasePath = "src/main/resources/journey.db";
    }

    /**
     * Constructs a new database manager from a specified url.
     * @param url the desired path to database.
     */
    private DatabaseManager(String url) {
        this.databasePath = url;
    }


    /**
     * Connects to the database.

     * @return 0 if successful or 1 if an error occurred.
     */
    public Connection connect() {
        Connection conn = null;
        try {
            String url = "jdbc:sqlite:".concat(databasePath);
            conn = DriverManager.getConnection(url);
            log.info("Connected to database.");
        } catch (SQLException e) {
            log.fatal(e);
        }
        return conn;
    }

    /**
     * Singleton method to get current Instance if exists otherwise create it
     * @return the single instance DatabaseSingleton
     */
    public static DatabaseManager getInstance() {
        if(instance == null) {
            instance = new DatabaseManager();
        }

        return instance;
    }

    /**
     * Updates the current user to one specified.
     * @param name name of the user to update to.
     */
    public void updateUser(String name) {
        User user = new User(name);
        String userQuery = """
                SELECT * FROM Users WHERE name = ?
                """;
        Connection conn = null;
        try {
            conn = connect();
            PreparedStatement statement = conn.prepareStatement(userQuery);
            statement.setString(1, name);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                user.setId(res.getInt(1));
            }
            log.info("Updated user. ");
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        currentUser = user;
    }

    /**
     * Sets up the database if not yet set up.
     */
    public void setup() {

        // Create a new table. TODO: Change hasTouristAttraction into list of attractions
        // Note: Order of stations in a journey is done by a 'order' column.
        Connection conn = null;
        try {
            String setupSQL = Files.readString(Path.of("/sql/init_db"));
            conn = connect();
            PreparedStatement statement = conn.prepareStatement(setupSQL);
            statement.execute();
            log.info("DatabaseManager setup.");
        } catch (Exception e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }



    // TODO: Handle non-unique users.
    public void setCurrentUser(String username) {
        // Update the currentUser variable and User database if necessary
        Connection conn = null;
        try {
            conn = connect();
            String userQuery = "SELECT * FROM Users WHERE name = ?";

            PreparedStatement findNoteStatement = conn.prepareStatement(userQuery);
            findNoteStatement.setString(1, username);
            ResultSet findNoteSet = findNoteStatement.executeQuery();

            /*
             * If result set is empty there isn't a user so
             * we insert a new user into the database.
             */
            if (!findNoteSet.isBeforeFirst()) {
                String insertQuery = "INSERT INTO Users VALUES (?,?)";
                PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
                insertStatement.setString(2, username); // UserID set to 1 as no users exist yet.
                insertStatement.execute();

            }
            updateUser(username);
            System.out.println("User updated");

        } catch(SQLException e) {
            e.printStackTrace();
        } finally {
            Utils.closeConn(conn);
        }
    }

    public Station queryStation(int id) {
        Connection conn = null;
        try {
            String sqlQuery = "SELECT * FROM Stations WHERE ID = ?";
            conn = connect();
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, id);
            ResultSet resultSet = ps.executeQuery();
            // Create a new station object. TODO: Clean up code (do in a more readable way, Process connectorsList properly
            Station station = new Station(resultSet.getInt("ID"),
                resultSet.getString("name"), resultSet.getString("operator"),
                resultSet.getString("owner"), resultSet.getString("address"),
                resultSet.getBoolean("is24Hours"), resultSet.getInt("carParkCount"),
                resultSet.getBoolean("hasCarParkCost"), resultSet.getInt("maxTimeLimit"),
                resultSet.getBoolean("hasTouristAttraction"), resultSet.getFloat("latitude"),
                resultSet.getFloat("longitude"), resultSet.getString("currentType"), resultSet.getString("dateFirstOperational"),
                resultSet.getInt("numberOfConnectors"), (resultSet.getString("connectorsList")).split(":"),
                resultSet.getBoolean("hasChargingCost"));
            return station;
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
            return null;
        }
    }
    /**
     * Inserts all features of the station into the database
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
        //TODO: add helper function to format string array to string
        //Creates new station in database. TODO: handle connectorsList properly
        Connection conn = null;
        try {
            conn = connect();
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

    public QueryResult catchEmAll() {
        Connection conn = null;
        ArrayList<Station> res = new ArrayList<>();
        try {
            conn = connect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Stations");
            Utils.insertRsIntoArray(rs, res);
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        QueryResult result = new QueryResult();
        result.setStations(res.toArray(Station[]::new));
        return result;
    }

    public QueryResult query(QueryStation searchStation) {


        //query with WHERE that is always true so that further statements can be chained on
        StringBuilder queryString = new StringBuilder("SELECT * FROM Stations WHERE id LIKE'%' ");

        //monster that builds up the query
        String address = searchStation.getAddress();
        if (address != null && address.trim().length() > 0) {
            queryString.append("AND address LIKE '%").append(address).append("%' ");
        }

        String name = searchStation.getName();
        if (name != null && name.trim().length() > 0) {
            queryString.append("AND name LIKE '%").append(name).append("%' ");
        }

        String operator = searchStation.getOperator();
        if (operator != null && operator.trim().length() > 0) {
            queryString.append("AND operator LIKE '%").append(operator).append("%' ");
        }

        int maxTime = searchStation.getMaxTime();
        if (maxTime > 0) {
            queryString.append("AND (maxTimeLimit >= ").append(maxTime)
                    .append(" OR maxTimeLimit = 0) ");
        }

        String currentType = searchStation.getCurrentType();
        if (!Objects.equals(currentType, "")) {
            queryString.append("AND (currentType = '").append(currentType)
                    .append("' OR currentType = 'Mixed') ");
        }

        Boolean attractions = searchStation.getHasTouristAttraction();
        if (attractions != null) {
            if (attractions) {
                queryString.append("AND hasTouristAttraction ");
            } else {
                queryString.append("AND NOT hasTouristAttraction ");
            }
        }

        ArrayList<Station> res = new ArrayList<>();
        Connection conn = null;
        try {
            conn = connect();
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(queryString.toString());
            Utils.insertRsIntoArray(rs, res);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        res.removeIf(station -> searchStation.getRange() != 0 && searchStation.distanceTo(station) > searchStation.getRange());
        Utils.closeConn(conn);
        QueryResult result = new QueryResult();
        result.setStations(res.toArray(Station[]::new));
        return result;
    }

    /**
     * Sets a given note into the Notes database.
     * If no note for the given station and user exists
     * it creates a new entry. Otherwise, updates the current note.

     * @param note The note to send to the database
     */

    public void setNote(Note note) {
        Connection conn = null;
        // Currently user is just set to ID of 1
        String noteString = note.getNote();
        Station currStation = note.getStation();
        int stationID = currStation.getOBJECTID();
        final int userID = 1; // TODO: Get the current user from database instead of hardcoding.

        try {
            conn = connect();
            // Query database to see if a note exists
            String findNoteQuery = "SELECT * FROM Notes WHERE station_id = ? AND user_id = ?";
            PreparedStatement findNoteStatement = conn.prepareStatement(findNoteQuery);
            findNoteStatement.setInt(1, stationID);
            findNoteStatement.setInt(2, userID);
            ResultSet findNoteSet = findNoteStatement.executeQuery();

            /*
            * If result set is empty there isn't a note for the station yet
            * In this case we just insert a new note into the station
            */
            if (!findNoteSet.isBeforeFirst()) {
                String insertQuery = "INSERT INTO notes VALUES (?,?,?,?)";
                PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
                insertStatement.setInt(2, userID); // UserID set to 1 as no users exist yet.
                insertStatement.setInt(3, stationID);
                insertStatement.setString(4, noteString);
                insertStatement.execute();
            } else {
                // A note exists, therefore we update it
                String updateQuery = "UPDATE Notes SET note = ? WHERE station_ID = ? AND user_ID = ?";
                PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                updateStatement.setString(1, noteString); // Updating the note field with the new note string.
                updateStatement.setInt(2, stationID);
                updateStatement.setInt(3, userID); // Hardcoded userID, update with actual id of user.
                updateStatement.execute();
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    public Note getNoteFromStation(Station station) {
        Connection conn = null;

        int stationID = station.getOBJECTID();
        final int userID = 1; // TODO: change to the user ID from the database, should be passed as a param
        try {
            conn = connect();
            String sqlQuery = "SELECT * FROM Notes WHERE station_ID = ? AND user_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, stationID);
            ps.setInt(2, userID); // Hardcoded user ID
            ResultSet resultSet = ps.executeQuery();
            // If there is no item in result set we disconnect first and return an empty note
            if(!resultSet.isBeforeFirst()) {
                return new Note(null, null);
            }
            String stationNote = resultSet.getString(4); // Get the note from the result set
            Note newNote = new Note(station, stationNote);
            return newNote;

        }  catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
            return null;
        }
    }

    public void setVehicle(Vehicle v) {
        Connection conn = null;
        try {
            conn = connect();
            String sqlQuery = "SELECT * FROM Vehicles WHERE user_ID = ? AND Registration = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, currentUser.getId());
            ps.setString(2, v.getRegistration());
            ResultSet resultSet = ps.executeQuery();

            // If there is no item in result set we disconnect first and return an empty note
            if(!resultSet.isBeforeFirst()) {

                String insertQuery = "INSERT INTO Vehicles VALUES (?,?,?,?,?,?)";
                PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
                insertStatement.setString(1, v.getRegistration());
                insertStatement.setInt(2, currentUser.getId());
                insertStatement.setInt(3, v.getYear());
                insertStatement.setString(4, v.getMake());
                insertStatement.setString(5, v.getModel());
                insertStatement.setString(6, v.getChargerType());

                insertStatement.execute();

                // Insert into list of vehicles for current user
                currentUser.newVehicle(v);

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
        try {
            String sqlQuery = "SELECT * FROM Vehicles WHERE User_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, currentUser.getId());
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
            QueryResult result = new QueryResult();
            result.setVehicles(res.toArray(Vehicle[]::new));
            return result;
        }


    }

    public void main(String[] args) {
        setup();
        QueryResult queryResult = catchEmAll();
        Station[] stuff = queryResult.getStations();
        for (Station station : stuff) {
            System.out.println(station.getAddress());
        }

    }
}