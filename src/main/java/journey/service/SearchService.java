package journey.service;

import journey.business.NominatimGeolocationManager;
import journey.data.GeoLocationResult;
import journey.data.QueryStation;

public class SearchService {

    public static QueryStation createQueryStation(String name,
                                                  String operator,
                                                  String currentType,
                                                  String[] connectors,
                                                  String attractions,
                                                  String maxTime,
                                                  String address,
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
        if (address != null
                && !address.isBlank()
                && range.matches("[+-]?(\\d+|\\d+\\.\\d+|\\.\\d+|\\d+\\.)")) {
            NominatimGeolocationManager nomMan = new NominatimGeolocationManager();
            GeoLocationResult geoLoc = nomMan.queryAddress(address);
            searchStation.setLatitude(geoLoc.getLat());
            searchStation.setLongitude(geoLoc.getLng());
            searchStation.setRange(Double.parseDouble(range));
        }
        return searchStation;
    }
}
