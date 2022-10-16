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
    @FXML private Label notesSuccess;
    @FXML private Label notesWarning;
    private Station currStation;
    private double stationRatingValue;

    /**
     * Submits notes and adds them the database for the current user.

     * @param event submit notes button clicked
     */
    @FXML private void submitNotes(Event event) {
        notesSuccess.setText("");
        notesWarning.setText("");
        String stationNote = stationDetailTextArea.getText();
        int rating = (int) stationRatingValue;
        boolean favourite = favouriteCheckBox.isSelected();

        Note newNote = new Note(currStation, stationNote, rating, favourite);

        if (currStation != null && (stationNote != null || rating != 0 || favourite)) {
            // Set the note on the database
            noteDAO.setNote(newNote, mainController.getCurrentUser());

            if (favourite) {
                mainController.setPrevMarkerFavourite();
            }

            notesSuccess.setText("Added station feedback");
        } else if (currStation == null) {
            notesWarning.setText("No station selected");
        } else {
            notesWarning.setText("No station feedback provided");
        }
        updateNoteText(newNote);
        /* Also need to update the stations if the favourite check box was clicked
           so that the marker icon will update.
        */
        mainController.updateFavourite(currStation, favourite);
        event.consume();
    }

    /**
     * Runs all functionality to update the notes panel for a newly selected station.
     */
    private void updateNote() {
        if (mainController.getSelectedStation() != -1) {
            Station currentStation = stationDAO.queryStation(mainController.getSelectedStation());
            updateStationNoteAddr(currentStation);
            Note note = noteDAO.getNoteFromStation(currentStation, mainController.getCurrentUser());
            updateNoteText(note);
            updateFavourite(note);
            updateRatings(note);
        }
    }

    /**
     * Updates the favourite checkbox for the current station.

     * @param note The note object to display
     */
    private void updateFavourite(Note note) {
        boolean favourite = note.getFavourite();
        favouriteCheckBox.setSelected(favourite);
    }

    /**
     * Updates the ratings display for the current station.

     * @param note The note object to display
     */
    private void updateRatings(Note note) {
        int rating = note.getRating();
        stationRating.setRating(rating);
    }

    /**
     * Sets Note text for a given charger based on the current station selected.
     */
    private void updateNoteText(Note note) {
        String noteText = note.getNote();
        stationDetailTextArea.setText(noteText);
    }

    /**
     * Sets the address of station in the notes panel when clicked.
     * Also resets notesSuccess/Warning.
     */
    private void updateStationNoteAddr(Station currStation) {
        if (currStation != null) {
            String addr = currStation.getReadableAddress();
            notesSuccess.setText("");
            notesWarning.setText("");
            noteStationAddr.setText(addr);
        }
    }

    /**
     * Updates the currently selected station.

     * @param selectedStation current station to be displayed within the notes pane.
     */
    public void updateSelectedStation(int selectedStation) {
        this.currStation = stationDAO.queryStation(selectedStation);
        updateStationNoteAddr(currStation);
        updateNote();
    }

    /**
     * updates station rating based on user input.
     */
    @FXML private void updateRating() {
        stationRatingValue = stationRating.getRating();
    }

    /**
     * Ensures rating does not "Lock in" on hover.
     */
    @FXML private void mouseEscaped() {
        stationRating.setRating(stationRatingValue);
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
