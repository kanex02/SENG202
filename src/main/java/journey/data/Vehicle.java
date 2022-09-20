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



    public String getStringRepresentation() {
        return registration + ": " + year + " " + make + " " + model;
    }
}
