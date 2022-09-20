package journey.data;

import java.util.ArrayList;

/**
 * User to use the system.
 */
public class User {
    private int id;
    private String name;
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private ArrayList<Station> stations;

    public User(String name) {
        this.name = name;
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

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public void newVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

}
