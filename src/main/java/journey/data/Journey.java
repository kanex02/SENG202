package journey.data;

import journey.Utils;

import java.util.ArrayList;

/**
 * A trip that the user took.
 */
public class Journey {
    private int userID;
    private ArrayList<String> waypoints;
    private String vehicleRegistration;
    private String start;
    private String end;
    private int journeyID;
    private String date;

    public Journey() { }



    /**
     * initialises private variables.

     * @param journeyID journeys ID
     * @param vehicleID Vehicles ID
     * @param userID Users ID
     * @param date date the journey was submitted
     */
    public Journey(int journeyID, String vehicleID, int userID, String date, String start, String end) {
        this.journeyID = journeyID;
        this.vehicleRegistration = vehicleID;
        this.userID = userID;
        this.date = date;
        this.start = start;
        this.end = end;
    }

    /**
     * initialises private variables.

     * @param vehicleID Vehicles ID
     * @param userID Users ID
     * @param date date the journey was submitted
     * @param waypoints stations visited on the trip
     */
    public Journey(String vehicleID, int userID, String date, ArrayList<String> waypoints) {
        this.vehicleRegistration = vehicleID;
        this.userID = userID;
        this.date = date;
        this.waypoints = waypoints;
        String[] start = waypoints.get(0).split("#");
        this.start = Utils.latLngToAddr(Double.parseDouble(start[0]),
                Double.parseDouble(start[1]));
        String[] end = waypoints.get(waypoints.size() - 1).split("#");
        this.end = Utils.latLngToAddr(Double.parseDouble(end[0]),
                Double.parseDouble(end[1]));
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getEnd() {
        return end;
    }

    public String getStart() {
        return start;
    }

    public int getJourneyID() {
        return journeyID;
    }

    public ArrayList<String> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<String> waypoints) {
        this.waypoints = waypoints;
    }

    public int getUserID() {
        return userID;
    }
}
