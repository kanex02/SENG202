package journey.data;

/**
 * An electric vehicle.
 */
public class Vehicle {
    int year;
    String make;
    String model;
    String chargerType;
    String connectorType;
    String registration;

    /**
     * Vehicle object initialiser.

     * @param year year made.
     * @param make make.
     * @param model model.
     * @param chargerType charger type.
     * @param registration registration.
     * @param connector connector.
     */
    public Vehicle(int year, String make, String model, String chargerType, String registration, String connector) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.chargerType = chargerType;
        this.registration = registration;
        this.connectorType = connector;
    }

    public int getYear() {
        return year;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getChargerType() {
        return chargerType;
    }

    public String getRegistration() {
        return registration;
    }

    public String getConnectorType() {
        return connectorType;
    }

    public String getStringRepresentation() {
        return registration + ": " + year + " " + make + " " + model;
    }
}
