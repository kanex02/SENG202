package journey.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Static utility class to make queries to the database.
 * TODO: Exception handler for fatal exceptions
 */
public final class Database {
    private static final String databasePath = "src/main/resources/journey.db";
    private static Connection conn = null;


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
     * Sets up the database if not yet set up.
     */
    public static void init() {

        // Create a new table. TODO: Change hasTouristAttraction into list of attractions
        String sql = """
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
                )
                """;

        try {
            Statement statement = conn.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
