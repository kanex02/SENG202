package journey.repository;

import java.io.File;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import journey.ReadCSV;
import journey.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;



/**
 * Static utility class to make queries to the database.
 */
public final class DatabaseManager {
    private final String databasePath;
    private static final Logger log = LogManager.getLogger();
    private static DatabaseManager instance;

    /**
     * Constructs a new database manager.
     */
    private DatabaseManager() {
        String path = DatabaseManager.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        path = URLDecoder.decode(path, StandardCharsets.UTF_8);
        File jarDir = new File(path);
        databasePath = jarDir.getParentFile() + "/database.db";
    }


    private DatabaseManager(String url) {
        this.databasePath = url;
        setup();
    }

    /**
     * Constructs a new database manager from a specified url.

     * @param url the desired path to database.
     */
    public static DatabaseManager initialiseWithUrl(String url) {
        if (instance == null) {
            instance = new DatabaseManager(url);
        }
        return instance;
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
        } catch (SQLException | NullPointerException e) {
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
            Boolean noDB = null;
            Connection conn = null;
            try {
                conn = instance.connect();
                if (conn != null) {
                    try (Statement statement = conn.createStatement()) {
                        ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM main.sqlite_master "
                                + "WHERE name = 'Stations'");
                        noDB = (rs.getInt(1) == 0);
                    }
                }
                Utils.closeConn(conn);
            } catch (Exception e) {
                log.error(e);
            } finally {
                Utils.closeConn(conn);
            }

            // Sets up the instance and imports data if the database is not already set up
            if (noDB != null && noDB) {
                instance.setup();
                ReadCSV.readStations();
            }
        }
        return instance;
    }



    /**
     * Sets up the database if not yet set up.
     */
    public void setup() {

        // Create a new table.
        // Note: Order of stations in a journey is done by a 'order' column.
        Connection conn = null;
        try {
            String setupSQL = Files.readString(Path.of("src/main/resources/sql/init_db.sql"));
            String[] statements = setupSQL.split("--Break");
            conn = connect();
            assert (conn != null);
            try (Statement statement = conn.createStatement()) {
                for (String line : statements) {
                    statement.addBatch(line);
                }
                statement.executeBatch();
                log.info("DatabaseManager setup.");
            }
        } catch (Exception e) {
            log.fatal(e);
        } finally {
            Utils.closeConn(conn);
        }
    }
}