package journey.service;

import journey.Utils;
import journey.data.QueryStation;
import journey.data.Station;
import journey.data.User;
import journey.repository.StationDAO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;


/**
 * Service class to extract testable methods for Station actions.
 */
public class StationsService {
    private final Station[] allStations;


    public StationsService(User user) {
        StationDAO stationDAO = new StationDAO();
        allStations = stationDAO.getAll(user);
    }

    public Station[] getAllStations() {
        return allStations;
    }

    /**
     * Creates a Query station to Query the database.

     * @param name name.
     * @param operator operator.
     * @param maxTime maximum time allowed to stay.
     * @param attractions whether there is an attraction nearby.
     * @param addressLatLng latitude and longitude of address.
     * @param currentType type of current provided (AC/DC/Mixed).
     * @param connectors information about connectors.
     * @param range range.
     * @param favourited whether the station has been favourited or not.
     * @return A station like object to be queried in the Database.
     */
    public static QueryStation createQueryStation(String name,
                                                  String operator,
                                                  String currentType,
                                                  String[] connectors,
                                                  String attractions,
                                                  String maxTime,
                                                  String addressLatLng,
                                                  String range,
                                                  boolean favourited) {
        QueryStation searchStation = new QueryStation();
        searchStation.setName(name);
        searchStation.setOperator(operator);
        searchStation.setCurrentType(currentType);
        searchStation.setConnectors(connectors);
        searchStation.setFavourite(favourited);
        if (attractions != null && !attractions.isBlank()) {
            boolean hasAttraction = (attractions.equals("Yes"));
            searchStation.setHasTouristAttraction(hasAttraction);
        }
        if (maxTime.matches("\\d+")) {
            searchStation.setMaxTime(Integer.parseInt(maxTime));
        }
        if (addressLatLng != null && !addressLatLng.isBlank() && !range.isBlank()) {
            String[] latLng = addressLatLng.split("#");
            searchStation.setLatitude(Double.parseDouble(latLng[0]));
            searchStation.setLongitude(Double.parseDouble(latLng[1]));
            searchStation.setRange(Double.parseDouble(range));
        }
        return searchStation;
    }

    /**
     * checks all inputs are given in a way that the database can understand.

     * @return A string of errors
     */
    public static String errorCheck(String addressLatLng, String name, String timeLimit, String range) {
        StringBuilder errors = new StringBuilder();

        //name check
        if (!name.matches("[a-zA-Z ]*")) {
            errors.append("Name cannot have special characters or integers\n");
        }

        //time limit check
        if (!timeLimit.equals("")) {
            if (!Utils.isInt(timeLimit)) {
                errors.append("Time limit must be a number!\n");
            } else if (Integer.parseInt(timeLimit) < 0) {
                errors.append("Time limit must be a positive integer!\n");
            }
        }

        //range check
        if (!range.equals("")) {
            if (!Utils.isInt(range)) {
                errors.append("Range needs to be an integer!\n");
            } else if (Integer.parseInt(range) <= 0) {
                errors.append("Range needs to be a positive integer");
            } else if (Integer.parseInt(range) >= 1600) {
                errors.append("Range must be within 1 - 1599!\n");
            }
        } else {
            errors.append("You have no range entered");
        }

        // range address check
        if (addressLatLng.equals("0.0#0.0") && !addressLatLng.isBlank()) {
            errors.append("Address does not exist!\n");
        }
        return errors.toString();
    }

    private void filterByConnector(QueryStation queryStation, ArrayList<Station> result) {
        String[] connectors = queryStation.getConnectors();

        if (connectors != null && connectors.length > 0) {
            //remove if the set difference between the query station and result station is 0. (No overlap)

            ArrayList<Station> toRemove = new ArrayList<>();
            for (Station station : result) {
                String[] stationConnectors = station.getConnectors();

                boolean hasConnector = hasConnector(queryStation, stationConnectors);

                if (!hasConnector) {
                    toRemove.add(station);
                }
            }
            for (Station station : toRemove) {
                result.remove(station);
            }
        }
    }

    /**
     * Returns whether a query station's connectors overlaps with an actual station's.

     * @param queryStation query station.
     * @param stationConnectors station to check.
     * @return has overlap.
     */
    private boolean hasConnector(QueryStation queryStation, String[] stationConnectors) {
        for (String connector : queryStation.getConnectors()) {
            for (String stationConnector : stationConnectors) {
                if (stationConnector.contains(connector)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Filter by a QueryStation.

     * @param queryStation station like object to be queried against
     * @return List of stations like the query station.
     */
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
            result.removeIf(station -> station.getMaxTime() < maxTime && station.getMaxTime() != 0);
        }
        String currentType = queryStation.getCurrentType();
        if (!Objects.equals(currentType, "") && currentType != null) {
            result.removeIf(station -> !Objects.equals(station.getCurrentType(), queryStation.getCurrentType())
                    && !Objects.equals(station.getCurrentType(), "Mixed"));
        }
        boolean favourite = queryStation.getFavourite();
        if (favourite) {
            result.removeIf(station -> !(station.getFavourite()));
        }
        filterByConnector(queryStation, result);
        Boolean attractions = queryStation.getHasTouristAttraction();
        if (attractions != null) {
            if (attractions) {
                result.removeIf(station -> !station.getHasTouristAttraction());
            } else {
                result.removeIf(Station::getHasTouristAttraction);
            }
        }
        if (queryStation.getRange() > 0) {
            result.removeIf(station -> queryStation.distanceTo(station) > queryStation.getRange());
        }

        return result.toArray(Station[]::new);
    }



}
