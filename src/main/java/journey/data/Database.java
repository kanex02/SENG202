package journey.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Static utility class to make queries to the database.
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
}
