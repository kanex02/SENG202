package journey.data;

/**
 * A trip that the user took.
 */
public class Journey {
    private Station[] stations;
    private double distanceTravelled;
    private String start;
    private String end;
    private String vehicle_ID;
    private int journeyID;
    private int userID;
    private String date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public Journey(String start, String end, String vehicleID, int userID, String date) {
        this.start = start;
        this.end = end;
        this.vehicle_ID = vehicleID;
        this.userID = userID;
        this.date = date;
    }
    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getVehicle_ID() {
        return vehicle_ID;
    }

    public void setVehicleID(String vehicleID) {
        this.vehicle_ID = vehicleID;
    }

    public int getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(int journeyID) {
        this.journeyID = journeyID;
    }

    public Station[] getStations() {
        return stations;
    }

    public void setStations(Station[] stations) {
        this.stations = stations;
    }

    public double getDistanceTravelled() {
        return distanceTravelled;
    }

    public void setDistanceTravelled(double distanceTravelled) {
        this.distanceTravelled = distanceTravelled;
    }
}
