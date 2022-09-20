package journey.data;

import java.util.ArrayList;

/**
 * A trip that the user took.
 */
public class Journey {
    private ArrayList<Integer> stations;
    private final String start;
    private final String end;
    private final String vehicle_ID;
    private int journeyID;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Journey(int journeyID, String start, String end, String vehicleID, String date) {
        this.journeyID = journeyID;
        this.start = start;
        this.end = end;
        this.vehicle_ID = vehicleID;
        this.date = date;
    }

    public Journey(String start, String end, String vehicleID, String date, ArrayList<Integer> stations) {
        this.start = start;
        this.end = end;
        this.vehicle_ID = vehicleID;
        this.date = date;
        this.stations = stations;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public String getVehicle_ID() {
        return vehicle_ID;
    }

    public int getJourneyID() {
        return journeyID;
    }

    public ArrayList<Integer> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Integer> stations) {
        this.stations = stations;
    }

}
