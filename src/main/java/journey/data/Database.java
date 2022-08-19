package journey.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
                    x INTEGER,
                    y INTEGER,
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
                    year INTEGER,
                    make TEXT,
                    model TEXT,
                    fuelType TEXT
                );
                """;
        String usersSql = """
                CREATE TABLE IF NOT EXISTS Users (
                    ID INTEGER PRIMARY KEY,
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
                    ID INTEGER PRIMARY KEY,
                    user_ID INTEGER NOT NULL REFERENCES Users(ID),
                    station_ID INTEGER NOT NULL REFERENCES Stations(ID),
                    note TEXT
                );
                """;
        String userVehiclesSql = """
                CREATE TABLE IF NOT EXISTS UserVehicles (
                    user_ID INTEGER NOT NULL REFERENCES Users(ID),
                    vehicle_ID INTEGER NOT NULL REFERENCES Vehicles(ID)
                );
                """;
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
            statement.execute(userVehiclesSql);
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
                resultSet.getBoolean("hasTouristAttraction"), resultSet.getInt("latitude"),
                resultSet.getInt("longitude"), resultSet.getString("currentType"), resultSet.getString("dateFirstOperational"),
                resultSet.getInt("numberOfConnectors"), null,
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
     * @param id
     * @param name
     * @param operator
     * @param owner
     * @param address
     * @param is24Hours
     * @param carParkCount
     * @param hasCarparkCost
     * @param maxTimeLimit
     * @param hasTouristAttraction
     * @param latitude
     * @param longitude
     * @param currentType
     * @param dateFirstOperational
     * @param numberOfConnectors
     * @param connectorsList
     * @param hasChargingCost
     */
    public static void createStation(int id, String name, String operator, String owner, String address,
                                     Boolean is24Hours, int carParkCount, Boolean hasCarparkCost, int maxTimeLimit,
                                     Boolean hasTouristAttraction, float latitude, float longitude, String currentType,
                                     String dateFirstOperational, int numberOfConnectors, String[] connectorsList,
                                     Boolean hasChargingCost) {
        //Creates new station in database. TODO: handle connectorsList properly
        connect();
        try {
            String sqlQuery = "INSERT INTO Stations VALUES (?,null,null,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
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
            ps.setString(16,"I have 3 connectors");
            ps.setBoolean(17, hasChargingCost);
            ps.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        disconnect();
    }
    public static void deleteStation(int id) {}

    public static void main(String[] args) {
        Database db = new Database();
        Station station = db.queryStation(1);
        System.out.println(station.getAddress());
        System.out.println(station.getCarParkCount());
        System.out.println(station.getHasCarparkCost());
    }
}
}
