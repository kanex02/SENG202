package journey;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import journey.business.NominatimGeolocationManager;
import journey.data.GeoCodeResult;
import journey.data.GeoLocationResult;
import journey.data.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Provides useful functions for the database.
 */
public class Utils {
    private static final Logger log = LogManager.getLogger();

    /**
     * Closes the connection, ignoring any SQLException that maybe thrown, either from the connection already being
     * closed or conn being null.

     * @param conn connection to close.
     */
    public static void closeConn(Connection conn) {
        try {
            conn.close();
        } catch (Exception ignored) {
            //ignored
        }
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
     * Converts an array list into a string.

     * @param arr ArrayList to convert
     * @param delimiter string to separate each item with
     * @return string representation of array
     */
    public static String convertArrayListToString(ArrayList<String> arr, String delimiter) {
        StringBuilder str = new StringBuilder();
        for (String i : arr) {
            str.append(i).append(delimiter);
        }
        //return str.substring(0, delimiter.length()-1);
        if (str.length() > 2) {
            return str.substring(0, str.length() - 2);
        } else {
            return "";
        }
    }

    /**
     * Converts station result set into a station arraylist.

     * @param rs result set to be converted.
     * @param res resultant ArrayList being passed into.
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

    /**
     * gets current date from system.

     * @return current date.
     */
    public static String getDate() {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        return df.format(new Date());
    }


    /**
     * Checks if input can be converted to integer.

     * @param str input to be checked.
     * @return boolean is/isn't integer.
     */
    public static boolean isInt(String str) {
        try {
            @SuppressWarnings("unused")
            int x = Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    /**
     * gets lat and lng from location.

     * @param loc location.
     * @return lat#lng string.
     */
    public static String locToLatLng(String loc) {
        NominatimGeolocationManager nomMan = new NominatimGeolocationManager();
        GeoLocationResult geoLoc = nomMan.queryAddress(loc);
        return geoLoc.getLat() + "#" + geoLoc.getLng();
    }

    /**
     * gets address from lat and lng.

     * @param lat latitude.
     * @param lng longitude.
     * @return address String.
     */
    public static String latLngToAddr(double lat, double lng) {
        NominatimGeolocationManager nomMan = new NominatimGeolocationManager();
        GeoCodeResult geoCode = nomMan.queryLatLng(lat, lng);
        return geoCode.getAddress();
    }
}
