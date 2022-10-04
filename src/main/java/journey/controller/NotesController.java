package journey.controller;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import journey.data.Note;
import journey.data.Station;
import journey.repository.NoteDAO;
import journey.repository.StationDAO;

/**
 * Controller for the notes panel to view notes.
 */
public class NotesController {
    private StationDAO stationDAO;
    private NoteDAO noteDAO;
    private MainController mainController;
    @FXML private TextArea stationDetailTextArea;
    @FXML private Label noteStationAddr;

    /**
     * Submits notes and adds them the database for the current user.

     * @param event submit notes button clicked
     */
    @FXML
    private void submitNotes(Event event) {
        Station currStation = stationDAO.queryStation(mainController.getSelectedStation());
        String stationNote = stationDetailTextArea.getText();

        if (currStation != null) {
            Note newNote = new Note(currStation, stationNote);
            // Set the note on the database
            noteDAO.setNote(newNote, mainController.getCurrentUser());
        }
        updateNoteText();
        event.consume();
    }

    /**
     * Sets Note text for a given charger based on the current station selected.
     */
    public void updateNoteText() {

        if (mainController.getSelectedStation() != -1) {
            Station currStation = stationDAO.queryStation(mainController.getSelectedStation());
            if (currStation != null) {
                Note note = noteDAO.getNoteFromStation(currStation, mainController.getCurrentUser());
                stationDetailTextArea.setText(note.getNote());
            }
        }
    }

    /**
     * Sets the address of station in the notes panel when clicked.
     */
    public void updateStationNoteAddr() {
        if (mainController.getSelectedStation() != -1) {
            Station currStation = stationDAO.queryStation(mainController.getSelectedStation());
            if (currStation != null) {
                String addr = currStation.getReadableAddress();
                noteStationAddr.setText(addr);
            }
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
