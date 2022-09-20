package journey.data;

/**
 * An electric vehicle.
 */
public class Vehicle {
    int year;
    String make;
    String model;
    String chargerType;

    String registration;


    public Vehicle(int year, String make, String model, String chargerType, String registration) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.chargerType = chargerType;
        this.registration = registration;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getChargerType() {
        return chargerType;
    }

    public void setFuelType(String fuelType) {
        this.chargerType = chargerType;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public String getStringRepresentation() {
        String returnString = registration + ": " + year + " " + make + " " + model;
        return returnString;
    }
}
