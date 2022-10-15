package journey.data;

import com.opencsv.bean.CsvBindByName;

import static java.lang.Math.*;




/**
 * A class that models a station, for use in results and queries.
 * X and Y have been excluded, as latitude and longitude are more widely used.
 */
public class Station {
    @CsvBindByName
    private float X;
    @CsvBindByName
    private float Y;
    @CsvBindByName
    private int OBJECTID;
    @CsvBindByName
    private String name;
    @CsvBindByName
    private String operator;
    @CsvBindByName
    private String owner;
    @CsvBindByName
    private String address;
    @CsvBindByName
    private Boolean is24Hours;
    @CsvBindByName
    private int carParkCount;
    @CsvBindByName
    private Boolean hasCarParkCost;
    @CsvBindByName
    private String maxTimeLimit;
    @CsvBindByName
    private Boolean hasTouristAttraction;
    @CsvBindByName
    private double latitude;
    @CsvBindByName
    private double longitude;
    @CsvBindByName
    private String currentType;
    @CsvBindByName
    private String dateFirstOperational;
    @CsvBindByName
    private int numberOfConnectors;
    @CsvBindByName
    private String connectorsList;
    @CsvBindByName
    private Boolean hasChargingCost;
    private int maxTime;
    private String[] connectors;

    private int rating;
    private boolean favourite;

    public Station() {}

    /**
     * Initialises a new station.

     * @param id id according to the database.
     * @param name name.
     * @param operator operator.
     * @param owner owner.
     * @param address location of station.
     * @param is24Hours whether the station is open 24 hours.
     * @param carParkCount number of car parks.
     * @param hasCarParkCost whether parking costs money.
     * @param maxTimeLimit maximum time allowed to stay.
     * @param hasTouristAttraction whether there is an attraction nearby.
     * @param latitude latitude.
     * @param longitude longitude.
     * @param currentType type of current provided (AC/DC/Mixed).
     * @param dateFirstOperational date first online.
     * @param numberOfConnectors number of connectors.
     * @param connectorsList information about connectors.
     * @param hasChargingCost whether charging costs money.
     */
    public Station(int id, String name, String operator, String owner, String address,
                   Boolean is24Hours, int carParkCount, Boolean hasCarParkCost,
                   int maxTimeLimit, Boolean hasTouristAttraction, float latitude,
                   float longitude, String currentType, String dateFirstOperational,
                   int numberOfConnectors, String[] connectorsList, Boolean hasChargingCost) {
        this.OBJECTID = id;
        this.name = name;
        this.operator = operator;
        this.owner = owner;
        this.address = address;
        this.is24Hours = is24Hours;
        this.carParkCount = carParkCount;
        this.hasCarParkCost = hasCarParkCost;
        this.maxTime = maxTimeLimit;
        this.hasTouristAttraction = hasTouristAttraction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentType = currentType;
        this.dateFirstOperational = dateFirstOperational;
        this.numberOfConnectors = numberOfConnectors;
        this.connectors = connectorsList;
        this.hasChargingCost = hasChargingCost;
        rating = 0;
        favourite = false;
    }

    /**
     * Initialises new Station with rating and favourite

     * @param id id according to the database.
     * @param name name.
     * @param operator operator.
     * @param owner owner.
     * @param address location of station.
     * @param is24Hours whether the station is open 24 hours.
     * @param carParkCount number of car parks.
     * @param hasCarParkCost whether parking costs money.
     * @param maxTimeLimit maximum time allowed to stay.
     * @param hasTouristAttraction whether there is an attraction nearby.
     * @param latitude latitude.
     * @param longitude longitude.
     * @param currentType type of current provided (AC/DC/Mixed).
     * @param dateFirstOperational date first online.
     * @param numberOfConnectors number of connectors.
     * @param connectorsList information about connectors.
     * @param hasChargingCost whether charging costs money.
     * @param rating chargers rating.
     * @param favourite whether the station has been favourited or not.
     */
    public Station(int id, String name, String operator, String owner, String address,
                   Boolean is24Hours, int carParkCount, Boolean hasCarParkCost,
                   int maxTimeLimit, Boolean hasTouristAttraction, float latitude,
                   float longitude, String currentType, String dateFirstOperational,
                   int numberOfConnectors, String[] connectorsList,
                   Boolean hasChargingCost, int rating, boolean favourite) {
        this.OBJECTID = id;
        this.name = name;
        this.operator = operator;
        this.owner = owner;
        this.address = address;
        this.is24Hours = is24Hours;
        this.carParkCount = carParkCount;
        this.hasCarParkCost = hasCarParkCost;
        this.maxTime = maxTimeLimit;
        this.hasTouristAttraction = hasTouristAttraction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentType = currentType;
        this.dateFirstOperational = dateFirstOperational;
        this.numberOfConnectors = numberOfConnectors;
        this.connectors = connectorsList;
        this.hasChargingCost = hasChargingCost;
        this.rating = rating;
        this.favourite = favourite;
    }

    /**
     * Calculates the distance between this station and another one.

     * @param other The other station.
     * @return Distance between the stations, in km.
     */
    public double distanceTo(Station other) {
        double lon1 = toRadians(this.longitude);
        double lon2 = toRadians(other.longitude);
        double lat1 = toRadians(this.latitude);
        double lat2 = toRadians(other.latitude);
        double deltaLong = lon2 - lon1;
        double deltaLat = lat2 - lat1;
        double a = pow(sin(deltaLat / 2), 2)
                + cos(lat1) * cos(lat2)
                * pow(sin(deltaLong / 2), 2);
        double c = 2 * asin(Math.sqrt(a));
        double radius = 6357;
        return radius * c;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public boolean getFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {

        this.maxTime = maxTime;
    }

    public String[] getConnectors() {

        return connectors;
    }

    public void setConnectors(String[] connectors) {
        this.connectors = connectors;
    }

    public String getOwner() {
        return owner;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isIs24Hours() {
        return is24Hours;
    }

    public int getCarParkCount() {
        return carParkCount;
    }

    public String getMaxTimeLimit() {
        return maxTimeLimit;
    }

    public Boolean getHasTouristAttraction() {
        return hasTouristAttraction;
    }

    public void setHasTouristAttraction(Boolean hasTouristAttraction) {
        this.hasTouristAttraction = hasTouristAttraction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
    }

    public String getDateFirstOperational() {
        return dateFirstOperational;
    }

    public int getNumberOfConnectors() {
        return numberOfConnectors;
    }

    public String getConnectorsList() {
        return connectorsList;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return operator;
    }

    public int getOBJECTID() {
        return OBJECTID;
    }

    public String getShortDescription() {
        return String.format("%s, %s", name, address);
    }

    public Boolean getHasCarParkCost() {
        return hasCarParkCost;
    }

    public Boolean getHasChargingCost() {
        return hasChargingCost;
    }

    /**
     * Assembles a string of details about itself.

     * @return longDes a long description of itself
     */
    public String getLongDescription() {
        String longDes = String.format("Name: %s%nOperator: %s%nOwner: %s%nAddress: %s%nNumber Of Car Parks: %s%n",
                name, operator, owner, address, carParkCount);
        if (Boolean.TRUE.equals(hasCarParkCost)) {
            longDes += "Has free parking\n";
        } else {
            longDes += "Doesn't have free parking\n";
        }
        if (Boolean.TRUE.equals(is24Hours)) {
            longDes += "24 Hour parking available\n";
        }
        if (Boolean.TRUE.equals(maxTime == 0)) {
            longDes += "Unlimited time limit\n";
        } else {
            longDes += "Time limit: " + maxTime + "\n";
        }
        if (Boolean.TRUE.equals(hasTouristAttraction)) {
            longDes += "Has tourist attractions nearby\n";
        }
        longDes += String.format("Current Type: %s%nNumber of Connectors: %s%n", currentType, numberOfConnectors);
        if (Boolean.TRUE.equals(hasChargingCost)) {
            longDes += "Not free charging\n";
        }
        return longDes;
    }

    public String getReadableAddress() {
        return String.format("%s at %s", name, address);
    }



}
