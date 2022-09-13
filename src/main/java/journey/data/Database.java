package journey.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Static utility class to make queries to the database.
 * TODO: Exception handler for fatal exceptions
 */
public final class Database {
    private static final String databasePath = "src/main/resources/journey.db";
    private static Connection conn = null;
    private static User currentUser = null;


    /**
     * Connects to the database.

     * @return 0 if successful or 1 if an error occurred.
     */
    public static int connect() {
        try {
            String url = "jdbc:sqlite:".concat(databasePath);
            conn = DriverManager.getConnection(url);

            return 0;
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            return 1;
        }
    }

    /**
     * Disconnects from the database.

     * @return 0 if successful or 1 if an error occurred.
     */
    public static int disconnect() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;

                return 0;
            } else {
                System.out.println("No connection");

                return 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());

            return 1;
        }
    }

    /**
     * Updates the current user to one specified.
     * @param userId ID of the user to update to.
     */
    public static void updateUser(int userId) {
        User user = new User(userId);
        String userQuery = """
                SELECT * FROM Users WHERE ID = ?
                """;

        try {
            connect();
            PreparedStatement statement = conn.prepareStatement(userQuery);
            statement.setInt(1, userId);
            ResultSet res = statement.executeQuery();
            user.setName(res.getString(2));
            disconnect();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        currentUser = user;
    }

    /**
     * Sets up the database if not yet set up.
     */
    public static void setup() {

        // Create a new table. TODO: Change hasTouristAttraction into list of attractions
        // Note: Order of stations in a journey is done by a 'order' column.
        String stationsSql = """
                CREATE TABLE IF NOT EXISTS Stations (
                    ID INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    operator TEXT,
                    owner TEXT,
                    address TEXT,
                    is24Hours BOOLEAN,
                    carParkCount INTEGER,
                    hasCarparkCost BOOLEAN,
                    maxTimeLimit INTEGER,
                    hasTouristAttraction BOOLEAN,
                    latitude FLOAT NOT NULL,
                    longitude FLOAT NOT NULL,
                    currentType TEXT NOT NULL,
                    dateFirstOperational TEXT,
                    numberOfConnectors INTEGER,
                    connectorsList TEXT NOT NULL,
                    hasChargingCost BOOLEAN
                );
                """;
        String vehiclesSql = """
                CREATE TABLE IF NOT EXISTS Vehicles (
                    ID INTEGER PRIMARY KEY,
                    User_ID INTEGER NOT NULL REFERENCES Users(ID),
                    year INTEGER,
                    make TEXT,
                    model TEXT,
                    fuelType TEXT
                );
                """;
        String usersSql = """
                CREATE TABLE IF NOT EXISTS Users (
                    ID INTEGER IDENTITY(1, 1) PRIMARY KEY,
                    name TEXT
                );
                """;
        String journeysSql = """
                CREATE TABLE IF NOT EXISTS Journeys (
                    ID INTEGER PRIMARY KEY,
                    distance INTEGER
                );
                """;
        String notesSql = """
                CREATE TABLE IF NOT EXISTS Notes (
                    ID INTEGER IDENTITY(1,1) PRIMARY KEY,
                    user_ID INTEGER NOT NULL REFERENCES Users(ID),
                    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
                    note TEXT
                );
                """;
//        String userVehiclesSql = """
//                CREATE TABLE IF NOT EXISTS UserVehicles (
//                    user_ID INTEGER NOT NULL REFERENCES Users(ID),
//                    vehicle_ID INTEGER NOT NULL REFERENCES Vehicles(ID)
//                );
//                """;
        String favouriteStationsSql = """
                CREATE TABLE IF NOT EXISTS FavouriteStations (
                    user_ID INTEGER NOT NULL REFERENCES Users(ID),
                    station_ID INTEGER NOT NULL REFERENCES Stations(ID)
                );
                """;
        String userJourneysSql = """
                CREATE TABLE IF NOT EXISTS UserJourneys (
                    user_ID INTEGER NOT NULL REFERENCES Users(ID),
                    journey_ID INTEGER NOT NULL REFERENCES Journeys(ID),
                    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
                    journeyOrder INTEGER NOT NULL
                )
                """;

        try {
            Statement statement = conn.createStatement();
            statement.execute(stationsSql);
            statement.execute(vehiclesSql);
            statement.execute(usersSql);
            statement.execute(journeysSql);
            statement.execute(notesSql);
            //statement.execute(userVehiclesSql);
            statement.execute(favouriteStationsSql);
            statement.execute(userJourneysSql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        Database.currentUser = currentUser;
    }

    public static String convertArrayToString(String[] arr, String delimiter) {
        StringBuilder newString = new StringBuilder();
        for (Object ob : arr) {
            newString.append(ob.toString()).append(delimiter);
        }
        return newString.toString();
    }

    public static Station queryStation(int id) {
        connect();
        try {
            String sqlQuery = "SELECT * FROM Stations WHERE ID = ?";
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
            disconnect();
            return station;
        } catch (SQLException ex) {
            disconnect();
            throw new RuntimeException(ex);
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
     * @param hasTouristAttraction whether there are touris attractions nearby
     * @param latitude stations latitude
     * @param longitude stations longitude
     * @param currentType stations current type
     * @param dateFirstOperational date station was first operational
     * @param numberOfConnectors number of connectors available to charge with
     * @param connectorsList list of connectors
     * @param hasChargingCost cost of charging
     */
    public static void createStation(int id, String name, String operator, String owner, String address,
                                     Boolean is24Hours, int carParkCount, Boolean hasCarparkCost, int maxTimeLimit,
                                     Boolean hasTouristAttraction, float latitude, float longitude, String currentType,
                                     String dateFirstOperational, int numberOfConnectors, String[] connectorsList,
                                     Boolean hasChargingCost) {
        //TODO: add helper function to format string array to string
        //Creates new station in database. TODO: handle connectorsList properly
        connect();
        try {
            String sqlQuery = "INSERT INTO Stations VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            PreparedStatement ps  = conn.prepareStatement(sqlQuery);
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
            ps.setFloat(11, latitude);
            ps.setFloat(12, longitude);
            ps.setString(13, currentType);
            ps.setString(14, dateFirstOperational);
            ps.setInt(15, numberOfConnectors);
            ps.setString(16,convertArrayToString(connectorsList, "//"));
            ps.setBoolean(17, hasChargingCost);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        disconnect();
    }
    public static void deleteStation(int id) {}

    public static QueryResult catchEmAll() {
        connect();
        ArrayList<Station> res = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM Stations");
            while (rs.next()) {
                res.add(new Station(rs.getInt("ID"),
                        rs.getString("name"), rs.getString("operator"),
                        rs.getString("owner"), rs.getString("address"),
                        rs.getBoolean("is24Hours"), rs.getInt("carParkCount"),
                        rs.getBoolean("hasCarParkCost"), rs.getInt("maxTimeLimit"),
                        rs.getBoolean("hasTouristAttraction"), rs.getFloat("latitude"),
                        rs.getFloat("longitude"), rs.getString("currentType"), rs.getString("dateFirstOperational"),
                        rs.getInt("numberOfConnectors"), (rs.getString("connectorsList")).split(":"),
                        rs.getBoolean("hasChargingCost")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        disconnect();
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

    public static void setNote(Note note) {
        connect();
        // Currently user is just set to ID of 1
        String noteString = note.getNote();
        Station currStation = note.getStation();
        int stationID = currStation.getOBJECTID();
        final int userID = 1; // TODO: Get the current user from database instead of hardcoding.

        try {
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
            System.out.println(e.getMessage());
        }
        disconnect();
    }

    public static Note getNoteFromStation(Station station) {
        connect();

        int stationID = station.getOBJECTID();
        final int userID = 1; // TODO: change to the user ID from the database, should be passed as a param

        try {
            String sqlQuery = "SELECT * FROM Notes WHERE station_ID = ? AND user_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, stationID);
            ps.setInt(2, userID); // Hardcoded user ID

            ResultSet resultSet = ps.executeQuery();

            // If there is no item in result set we disconnect first and return an empty note
            if(!resultSet.isBeforeFirst()) {
                disconnect();
                return new Note(null, null);
            }

            String stationNote = resultSet.getString(4); // Get the note from the result set
            Note newNote = new Note(station, stationNote);

            disconnect();
            return newNote;

        }  catch (SQLException ex) {
            disconnect();
            throw new RuntimeException(ex);
        }
    }

    public static void main(String[] args) {
        QueryResult queryResult = catchEmAll();
        Station[] stuff = queryResult.getStations();
        for (Station station : stuff) {
            System.out.println(station.getAddress());
        }

    }
}