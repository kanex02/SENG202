package journey.data;

import java.util.ArrayList;

/**
 * User to use the system.
 */
public class User {
    private int id;
    private String name;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Station> stations;
    private ArrayList<Journey> journeys;
    private ArrayList<Note> notes;
    private ArrayList<String> chargerTypes;

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

    public ArrayList<Vehicle> getVehicles() {
        return vehicles;
    }

    public void setVehicles(ArrayList<Vehicle> vehicles) {
        this.vehicles = vehicles;
    }

    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public ArrayList<Journey> getJourneys() {
        return journeys;
    }

    public void setJourneys(ArrayList<Journey> journeys) {
        this.journeys = journeys;
    }

    public ArrayList<Note> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<String> getChargerTypes() {
        return chargerTypes;
    }
    public void newVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }
    public void newCharger(String charger) {
        this.chargerTypes.add(charger);
    }

    public void findChargerTypes() {
        for (Vehicle vehicle : vehicles) {
            String charger = vehicle.getFuelType();
            this.chargerTypes.add(charger);
        }
    }
}
