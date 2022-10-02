package journey.repository;

import journey.controller.ReadCSV;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

import journey.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Static utility class to make queries to the database.
 * TODO: Exception handler for fatal exceptions
 */
public final class DatabaseManager {
    private final String databasePath;
    private static final Logger log = LogManager.getLogger();
    private static DatabaseManager instance = null;

    /**
     * Constructs a new database manager.
     */
    private DatabaseManager() {
        databasePath = "database.db";
    }

    /**
     * Constructs a new database manager from a specified url.

     * @param url the desired path to database.
     */
    public DatabaseManager(String url) {
        this.databasePath = url;
    }


    /**
     * Connects to the database.

     * @return a connection.
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
     * Singleton method to get current Instance if exists otherwise create it.

     * @return the single instance DatabaseSingleton
     */
    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
            boolean noDB;
            try {
                Connection conn = instance.connect();
                Statement statement = conn.createStatement();
                ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM main.sqlite_master "
                        + "WHERE name = 'Stations'");
                noDB = (rs.getInt(1) == 0);
                Utils.closeConn(conn);
                if (noDB) {
                    instance.setup();
                    ReadCSV.readStations();
                }
            } catch (Exception e) {
                log.error(e);
            }
        }

        return instance;
    }



    /**
     * Sets up the database if not yet set up.
     */
    public void setup() throws SQLException, IOException {

        // Create a new table.
        // Note: Order of stations in a journey is done by a 'order' column.
        Connection conn = null;
        try {
            String setupSQL = Files.readString(Path.of("src/main/resources/sql/init_db.sql"));
            String[] statements = setupSQL.split("--Break");
            conn = connect();
            assert (conn != null);
            Statement statement = conn.createStatement();
            for (String line : statements) {
                statement.addBatch(line);
            }
            statement.executeBatch();
            log.info("DatabaseManager setup.");
        } catch (Exception e) {
            log.error(e);
            throw e;
        } finally {
            Utils.closeConn(conn);
        }
    }
}