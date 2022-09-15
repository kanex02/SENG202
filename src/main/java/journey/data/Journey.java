package journey.data;

/**
 * A trip that the user took.
 */
public class Journey {
    private Station[] stations;
    private double distanceTravelled;
    private String start;

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

    public int getVehicleID() {
        return vehicleID;
    }

    public void setVehicleID(int vehicleID) {
        this.vehicleID = vehicleID;
    }

    public int getJourneyID() {
        return journeyID;
    }

    public void setJourneyID(int journeyID) {
        this.journeyID = journeyID;
    }

    private String end;
    private int vehicleID;
    private int journeyID;

    public Journey(String start, String end, int vehicleID, int journeyID) {
        this.start = start;
        this.end = end;
        this.vehicleID = vehicleID;
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
