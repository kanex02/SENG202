package journey.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import journey.data.Note;
import journey.data.Station;
import journey.repository.NoteDAO;
import journey.repository.StationDAO;
import org.controlsfx.control.Rating;

/**
 * Controller for the notes panel to view notes.
 */
public class NotesController {
    private StationDAO stationDAO;
    private NoteDAO noteDAO;
    private MainController mainController;
    @FXML private TextArea stationDetailTextArea;
    @FXML private Label noteStationAddr;
    @FXML private Rating stationRating;
    @FXML private CheckBox favouriteCheckBox;

    /**
     * Submits notes and adds them the database for the current user.

     * @param event submit notes button clicked
     */
    @FXML
    private void submitNotes(Event event) {

        Station currStation = stationDAO.queryStation(mainController.getSelectedStation());
        String stationNote = stationDetailTextArea.getText();
        int rating = (int) stationRating.getRating();
        boolean favourite = favouriteCheckBox.isSelected();

        Note newNote = new Note(currStation, stationNote, rating, favourite);

        if (currStation != null) {
            // Set the note on the database
            noteDAO.setNote(newNote, mainController.getCurrentUser());
        }

        updateNoteText(newNote);
        event.consume();
    }

    /**
     * Runs all functionality to update the notes panel for a newly selected station
     */
    public void updateNote() {
        if (mainController.getSelectedStation() != -1) {

            Station currStation = stationDAO.queryStation(mainController.getSelectedStation());
            updateStationNoteAddr(currStation);

            Note note = noteDAO.getNoteFromStation(currStation, mainController.getCurrentUser());
            updateNoteText(note);
            updateFavourite(note);
            updateRatings(note);
        }
    }

    /**
     * Updates the favourite checkbox for the current station
     * @param note The note object to display
     */
    public void updateFavourite(Note note) {
        boolean favourite = note.getFavourite();
        favouriteCheckBox.setSelected(favourite);
    }

    /**
     * Updates the ratings display for the current station
     * @param note The not object to display
     */
    public void updateRatings(Note note) {
        int rating = note.getRating();
        stationRating.setRating(rating);
    }

    /**
     * Sets Note text for a given charger based on the current station selected.
     */
    public void updateNoteText(Note note) {
        String noteText = note.getNote();
        stationDetailTextArea.setText(noteText);
    }

    /**
     * Sets the address of station in the notes panel when clicked.
     */
    public void updateStationNoteAddr(Station currStation) {
        if (currStation != null) {
            String addr = currStation.getReadableAddress();
            noteStationAddr.setText(addr);
        }
    }


    /**
     * Initialises the notes panel.

     * @param mainController Main controller of the app
     */
    public void init(MainController mainController) {
        stationDAO = new StationDAO();
        noteDAO = new NoteDAO();
        this.mainController = mainController;
    }

}
