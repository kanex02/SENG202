package journey.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Provides useful functions for the database.
 */
public class Utils {
    private static final Logger log = LogManager.getLogger();

    /**
     * Closes the connection.
     * @param conn connection to close.
     */
    public static void closeConn(Connection conn) {
        try {
            conn.close();
        } catch (Exception ignored) {}
    }

    /**
     * Converts a string array into a single string, separated by a delimiter.
     * @param arr array to convert.
     * @param delimiter string to separate values.
     * @return array in string form.
     */
    public static String convertArrayToString(String[] arr, String delimiter) {
        StringBuilder newString = new StringBuilder();
        for (Object ob : arr) {
            newString.append(ob.toString()).append(delimiter);
        }
        return newString.toString();
    }

    /**
     * Converts station result set into a station arraylist
     * @param rs result set to be converted
     * @param res resultant ArrayList being passed into
     */

    public static void insertRsIntoArray(ResultSet rs, ArrayList<Station> res) {
        try {
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
            log.error(e);
        }
    }

    public static String getDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String date = df.format(new Date());
        return date;
    }


    public static boolean isInt(String str) {

        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
