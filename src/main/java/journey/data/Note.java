package journey.data;

/**
 * A user comment about a station.
 */
public class Note {
    private Station station;
    private String note;

    public Note() {}

    public Note(Station station, String note) {
        this.station = station;
        this.note = note;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
