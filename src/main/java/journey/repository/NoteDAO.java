package journey.repository;

import journey.data.Note;
import journey.data.Station;
import journey.data.User;
import journey.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Concrete implementation of Database Access Object that handles all notes related actions to the database.
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

     * @param note The note to send to the database.
     */
    public void setNote(Note note, User user) {

        String noteString = note.getNote();
        Station currStation = note.getStation();
        int rating = note.getRating();
        boolean favourite = note.getFavourite();

        int stationID = currStation.getObjectid();
        int userID = user.getId();

        Connection conn = null;
        try {
            conn = databaseManager.connect();
            // Query database to see if a note exists
            String findNoteQuery = "SELECT * FROM Notes WHERE station_id = ? AND user_id = ?";
            try (PreparedStatement findNoteStatement = conn.prepareStatement(findNoteQuery)) {
                findNoteStatement.setInt(1, stationID);
                findNoteStatement.setInt(2, userID);
                ResultSet findNoteSet = findNoteStatement.executeQuery();

                /*
                 * If result set is empty there isn't a note for the station yet
                 * In this case we just insert a new note into the station
                 */
                if (!findNoteSet.isBeforeFirst()) {
                    String insertQuery = "INSERT INTO notes VALUES (?,?,?,?,?,?)";
                    try (PreparedStatement insertStatement = conn.prepareStatement(insertQuery)) {
                        insertStatement.setInt(2, userID);
                        insertStatement.setInt(3, stationID);
                        insertStatement.setString(4, noteString);
                        insertStatement.setInt(5, rating);
                        insertStatement.setBoolean(6, favourite);
                        insertStatement.execute();
                    }
                } else {
                    // A note exists, therefore we update it
                    String updateQuery = "UPDATE Notes SET note = ?, rating = ?, "
                            + "favourited = ? WHERE station_ID = ? AND user_ID = ?";
                    try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
                        updateStatement.setString(1, noteString); // Updating the note field with the new note string.
                        updateStatement.setInt(2, rating);
                        updateStatement.setBoolean(3, favourite);
                        updateStatement.setInt(4, stationID);
                        updateStatement.setInt(5, userID);
                        updateStatement.execute();
                    }
                }
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }



    /**
     * Gets a note from the given station and user.
     *
     * @param station station to get notes from.
     * @return note from station.
     */
    public Note getNoteFromStation(Station station, User user) {
        Connection conn = null;

        int stationID = station.getObjectid();
        int userID = user.getId();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Notes WHERE station_ID = ? AND user_ID = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setInt(1, stationID);
                ps.setInt(2, userID);
                ResultSet resultSet = ps.executeQuery();
                // If there is no item in result set we disconnect first and return an empty note
                if (!resultSet.isBeforeFirst()) {
                    return new Note(null, null, 0, false);
                }

                String stationNote = resultSet.getString(4);
                int stationRating = resultSet.getInt(5);
                boolean stationFavourited = resultSet.getBoolean(6);

                return new Note(station, stationNote, stationRating, stationFavourited);
            }
        }  catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return new Note();
    }
}
