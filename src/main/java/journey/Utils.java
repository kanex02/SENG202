package journey;

import journey.business.NominatimGeolocationManager;
import journey.data.GeoCodeResult;
import journey.data.GeoLocationResult;
import journey.data.Station;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Provides useful functions for the database.
 */
public class Utils {
    private static final Logger log = LogManager.getLogger();
    private static final String CHARSPACEREGEX = "[a-z|A-Z| ]+";
    private static final String CHARNUMREGEX = "[a-z|A-Z|0-9| ]+";
    private static final String CHARONLYREGEX = "[a-z|A-Z]+";
    private Utils() {}
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
    public static String convertArrayListToString(List<String> arr, String delimiter) {
        StringBuilder str = new StringBuilder();
        for (String i : arr) {
            str.append(i).append(delimiter);
        }
        if (str.length() > 2) {
            return str.substring(0, str.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * Converts station result set into a station arraylist.

     * @param resultSet result set to be converted.
     * @param res resultant ArrayList being passed into.
     */

    public static void insertRsIntoArray(ResultSet resultSet, List<Station> res) {
        try {
            while (resultSet.next()) {
                Station station = new Station();
                station.setObjectid(resultSet.getInt("ID"));
                station.setName(resultSet.getString("name"));
                station.setOperator(resultSet.getString("operator"));
                station.setOwner(resultSet.getString("owner"));
                station.setAddress(resultSet.getString("address"));
                station.setIs24Hours(resultSet.getBoolean("is24Hours"));
                station.setCarParkCount(resultSet.getInt("carParkCount"));
                station.setHasCarParkCost(resultSet.getBoolean("hasCarParkCost"));
                station.setMaxTime(resultSet.getInt("maxTimeLimit"));
                station.setHasTouristAttraction(resultSet.getBoolean("hasTouristAttraction"));
                station.setLatitude(resultSet.getFloat("latitude"));
                station.setLongitude(resultSet.getFloat("longitude"));
                station.setCurrentType(resultSet.getString("currentType"));
                station.setDateFirstOperational(resultSet.getString("dateFirstOperational"));
                station.setNumberOfConnectors(resultSet.getInt("numberOfConnectors"));
                station.setConnectors((resultSet.getString("connectorsList")).split(":"));
                station.setHasTouristAttraction(resultSet.getBoolean("hasChargingCost"));
                station.setRating(resultSet.getInt("rating"));
                station.setFavourite(resultSet.getBoolean("favourited"));
                res.add(station);
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

    public static String getCharacterSpace() {
        return CHARSPACEREGEX;
    }

    public static String getCharacterDigit() {
        return CHARNUMREGEX;
    }

    public static String getCharacterOnly() {
        return CHARONLYREGEX;
    }


}
