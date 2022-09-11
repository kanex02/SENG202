package journey.data;

/**
 * An electric vehicle.
 */
public class Vehicle {
    int year;
    String make;
    String model;
    String fuelType;

    String registration;


    public Vehicle(int year, String make, String model, String fuelType, String registration) {
        this.year = year;
        this.make = make;
        this.model = model;
        this.fuelType = fuelType;
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

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }
}
