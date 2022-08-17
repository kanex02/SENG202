package journey.data;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A class that models a station, for use in results and queries.
 * X and Y have been excluded, as latitude and longitude are more widely used.
 */
public class Station {
    private int id;
    private String name;
    private String operator;
    private String owner;
    private String address;
    private Boolean is24Hours;
    private int carParkCount;
    private Boolean hasCarparkCost;
    private int maxTimeLimit;
    private Boolean hasTouristAttraction;
    private float latitude;
    private float longitude;
    private String currentType;
    private String dateFirstOperational;
    private int numberOfConnectors;
    private String[] connectorsList;
    private Boolean hasChargingCost;

    /**
     * Initialises a new station.

     * @param id id according to the database
     * @param name name
     * @param operator operator
     * @param owner owner
     * @param address location of station
     * @param is24Hours whether the station is open 24 hours
     * @param carParkCount number of car parks
     * @param hasCarparkCost whether parking costs money
     * @param maxTimeLimit maximum time allowed to stay
     * @param hasTouristAttraction whether there is an attraction nearby
     * @param latitude latitude
     * @param longitude longitude
     * @param currentType type of current provided (AC/DC/Mixed)
     * @param dateFirstOperational date first online
     * @param numberOfConnectors number of connectors
     * @param connectorsList information about connectors
     * @param hasChargingCost whether charging costs money
     */
    public Station(int id, String name, String operator, String owner, String address,
                   Boolean is24Hours, int carParkCount, Boolean hasCarparkCost,
                   int maxTimeLimit, Boolean hasTouristAttraction, float latitude,
                   float longitude, String currentType, String dateFirstOperational,
                   int numberOfConnectors, String[] connectorsList, Boolean hasChargingCost) {
        this.id = id;
        this.name = name;
        this.operator = operator;
        this.owner = owner;
        this.address = address;
        this.is24Hours = is24Hours;
        this.carParkCount = carParkCount;
        this.hasCarparkCost = hasCarparkCost;
        this.maxTimeLimit = maxTimeLimit;
        this.hasTouristAttraction = hasTouristAttraction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.currentType = currentType;
        this.dateFirstOperational = dateFirstOperational;
        this.numberOfConnectors = numberOfConnectors;
        this.connectorsList = connectorsList;
        this.hasChargingCost = hasChargingCost;
    }

    /**
     * Calculates the distance between this station and another one.

     * @param other The other station.
     * @return Distance between the stations, in km.
     */
    public double distanceTo(Station other) {
        return 6378.8 * acos((sin(this.latitude) * sin(other.latitude))
                + cos(this.latitude) * cos(other.latitude) * cos(other.longitude - this.longitude));
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getIs24Hours() {
        return is24Hours;
    }

    public void setIs24Hours(Boolean is24Hours) {
        this.is24Hours = is24Hours;
    }

    public int getCarParkCount() {
        return carParkCount;
    }

    public void setCarParkCount(int carParkCount) {
        this.carParkCount = carParkCount;
    }

    public Boolean getHasCarparkCost() {
        return hasCarparkCost;
    }

    public void setHasCarparkCost(Boolean hasCarparkCost) {
        this.hasCarparkCost = hasCarparkCost;
    }

    public int getMaxTimeLimit() {
        return maxTimeLimit;
    }

    public void setMaxTimeLimit(int maxTimeLimit) {
        this.maxTimeLimit = maxTimeLimit;
    }

    public Boolean getHasTouristAttraction() {
        return hasTouristAttraction;
    }

    public void setHasTouristAttraction(Boolean hasTouristAttraction) {
        this.hasTouristAttraction = hasTouristAttraction;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
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

    public void setDateFirstOperational(String dateFirstOperational) {
        this.dateFirstOperational = dateFirstOperational;
    }

    public int getNumberOfConnectors() {
        return numberOfConnectors;
    }

    public void setNumberOfConnectors(int numberOfConnectors) {
        this.numberOfConnectors = numberOfConnectors;
    }

    public String[] getConnectorsList() {
        return connectorsList;
    }

    public void setConnectorsList(String[] connectorsList) {
        this.connectorsList = connectorsList;
    }

    public Boolean getHasChargingCost() {
        return hasChargingCost;
    }

    public void setHasChargingCost(Boolean hasChargingCost) {
        this.hasChargingCost = hasChargingCost;
    }
}
