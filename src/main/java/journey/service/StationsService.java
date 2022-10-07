package journey.service;

import journey.Utils;
import journey.data.QueryStation;
import journey.data.Station;
import journey.repository.StationDAO;

import java.util.*;

public class StationsService {
    private final Station[] allStations;


    public StationsService() {
        StationDAO stationDAO = new StationDAO();
        allStations = stationDAO.getAll();
    }

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

    public Station[] filterBy(QueryStation queryStation) {
        ArrayList<Station> result = new ArrayList<>(Arrays.asList(allStations));
        //filter stations by fields of the queryStation
        String name = queryStation.getName();
        if (name != null && name.trim().length() > 0) {
            result.removeIf(station -> !station.getName().toLowerCase().contains(name.toLowerCase()));
        }
        String operator = queryStation.getOperator();
        if (operator != null && operator.trim().length() > 0) {
            result.removeIf(station -> !station.getOperator().toLowerCase().contains(operator.toLowerCase()));
        }
        int maxTime = queryStation.getMaxTime();
        if (maxTime > 0) {
            result.removeIf(station -> station.getMaxTime() < maxTime && !(station.getMaxTime() == 0));
        }
        String currentType = queryStation.getCurrentType();
        if (!Objects.equals(currentType, "") && currentType != null) {
            result.removeIf(station -> !Objects.equals(station.getCurrentType(), queryStation.getCurrentType())
                    && !Objects.equals(station.getCurrentType(), "Mixed"));
        }
        String[] connectors = queryStation.getConnectors();
        if (connectors != null && connectors.length > 0) {
            //remove if the set difference between the query station and result station is 0. (No overlap)
            Set<String> queryConnectors = new HashSet<>(Arrays.asList(queryStation.getConnectors()));
            ArrayList<Station> toRemove = new ArrayList<>();
            for (Station station : result) {
                Set<String> stationConnectors = new HashSet<>(Arrays.asList(station.getConnectors()));
                stationConnectors.retainAll(queryConnectors);
                if (stationConnectors.size() == 0) {
                    toRemove.add(station);
                }
            }
            for (Station station : toRemove) {
                result.remove(station);
            }

        }
        Boolean attractions = queryStation.getHasTouristAttraction();
        if (attractions != null) {
            if (attractions) {
                result.removeIf(station -> !station.getHasTouristAttraction());
            } else {
                result.removeIf(Station::getHasTouristAttraction);
            }
        }
        result.removeIf(station -> queryStation.getRange() > 0
                && queryStation.distanceTo(station) > queryStation.getRange());


        return result.toArray(Station[]::new);
    }

}
