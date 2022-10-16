package journey.data;

/**
 * A user comment about a station.
 */
public class Note {
    private Station station;
    private String theNote;
    private boolean favourite;
    private int rating;

    public Note() {}

    /**
     * Initialises stations private variables.

     * @param station a station that a note is being added to/removed from.
     * @param note the note for the station.
     * @param rating the rating of the station.
     * @param favourite whether the station has been favourited or not.
     */
    public Note(Station station, String note, int rating, boolean favourite) {
        this.station = station;
        this.theNote = note;
        this.rating = rating;
        this.favourite = favourite;
    }

    public int getRating() {
        return rating;
    }

    public boolean getFavourite() {
        return favourite;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getNote() {
        return theNote;
    }

    public void setNote(String note) {
        this.theNote = note;
    }
}
