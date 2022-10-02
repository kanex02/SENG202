package journey.repository;

import journey.data.Note;
import journey.data.Station;
import journey.data.User;
import journey.data.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Concrete implementation of Database Access Object that handles all notes related actions to the database
 */
public class NoteDAO {
    private final DatabaseManager databaseManager;
    private static final Logger log = LogManager.getLogger();

    public NoteDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Sets a given note into the Notes database.
     * If no note for the given station and user exists
     * it creates a new entry. Otherwise, updates the current note.

     * @param note The note to send to the database
     */
    public void setNote(Note note, User user) {
        Connection conn = null;
        String noteString = note.getNote();
        Station currStation = note.getStation();
        int stationID = currStation.getOBJECTID();
        int userID = user.getId();
        try {
            conn = databaseManager.connect();
            // Query database to see if a note exists
            String findNoteQuery = "SELECT * FROM Notes WHERE station_id = ? AND user_id = ?";
            PreparedStatement findNoteStatement = conn.prepareStatement(findNoteQuery);
            findNoteStatement.setInt(1, stationID);
            findNoteStatement.setInt(2, userID);
            ResultSet findNoteSet = findNoteStatement.executeQuery();

            /*
             * If result set is empty there isn't a note for the station yet
             * In this case we just insert a new note into the station
             */
            if (!findNoteSet.isBeforeFirst()) {
                String insertQuery = "INSERT INTO notes VALUES (?,?,?,?)";
                PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
                insertStatement.setInt(2, userID); // UserID set to 1 as no users exist yet.
                insertStatement.setInt(3, stationID);
                insertStatement.setString(4, noteString);
                insertStatement.execute();
            } else {
                // A note exists, therefore we update it
                String updateQuery = "UPDATE Notes SET note = ? WHERE station_ID = ? AND user_ID = ?";
                PreparedStatement updateStatement = conn.prepareStatement(updateQuery);
                updateStatement.setString(1, noteString); // Updating the note field with the new note string.
                updateStatement.setInt(2, stationID);
                updateStatement.setInt(3, userID); // Hardcoded userID, update with actual id of user.
                updateStatement.execute();
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    /**
     * Gets not from give station
     * @param station station to get notes from
     * @return note from station
     */
    public Note getNoteFromStation(Station station, User user) {
        Connection conn = null;

        int stationID = station.getOBJECTID();
        int userID = user.getId();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Notes WHERE station_ID = ? AND user_ID = ?";
            PreparedStatement ps = conn.prepareStatement(sqlQuery);
            ps.setInt(1, stationID);
            ps.setInt(2, userID); // Hardcoded user ID
            ResultSet resultSet = ps.executeQuery();
            // If there is no item in result set we disconnect first and return an empty note
            if (!resultSet.isBeforeFirst()) {
                return new Note(null, null);
            }
            String stationNote = resultSet.getString(4); // Get the note from the result set
            return new Note(station, stationNote);
        }  catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return new Note();
    }
}
