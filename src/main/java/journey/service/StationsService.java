package journey.service;

import journey.Utils;
import journey.business.NominatimGeolocationManager;
import journey.data.GeoLocationResult;
import journey.data.QueryStation;
import journey.data.Station;

public class StationsService {
    private Station[] stations;

    public static QueryStation createQueryStation(String name,
                                                  String operator,
                                                  String currentType,
                                                  String[] connectors,
                                                  String attractions,
                                                  String maxTime,
                                                  String addressLatLng,
                                                  String range) {
        QueryStation searchStation = new QueryStation();
        searchStation.setName(name);
        searchStation.setOperator(operator);
        searchStation.setCurrentType(currentType);
        searchStation.setConnectors(connectors);
        if (attractions != null && !attractions.isBlank()) {
            boolean hasAttraction = (attractions.equals("Yes"));
            searchStation.setHasTouristAttraction(hasAttraction);
        }
        if (maxTime.matches("\\d+")) {
            searchStation.setMaxTime(Integer.parseInt(maxTime));
        }
        if (addressLatLng != null
                && !addressLatLng.isBlank()
                && range.matches("(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
            String[] latLng = addressLatLng.split("#");
            searchStation.setLatitude(Double.parseDouble(latLng[0]));
            searchStation.setLongitude(Double.parseDouble(latLng[1]));
            searchStation.setRange(Double.parseDouble(range));
        }
        return searchStation;
    }

    /**
     * checks all inputs are given in a way that the database can understand

     * @return A string of errors
     */
    public static String errorCheck(String addressLatLng, String name, String operator, String timeLimit, String range) {
        StringBuilder errors = new StringBuilder();

        //name check
        if (!name.matches("[a-zA-Z ]*")) {
            errors.append("Name cannot have special characters or integers\n");
        }

        //operator check
        if (!operator.matches("[a-zA-Z ]*")) {
            errors.append("Operator cannot have special characters or integers\n");
        }
        //time limit check
        if (!Utils.isInt(timeLimit) && !timeLimit.equals("")) {
            errors.append("Time limit must be an integer!\n");
        }

        //range check
        if (!Utils.isInt(range) && !range.equals("")) {
            errors.append("Range needs to be an integer!\n");
        }

        // range address check
        if (addressLatLng.equals("0.0#0.0") && !addressLatLng.isBlank()) {
            errors.append("Address does not exist!\n");
        }
        return errors.toString();
    }
}
