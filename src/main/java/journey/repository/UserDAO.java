package journey.repository;

import journey.data.User;
import journey.data.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 * Concrete implementation of Database Access Object that handles all user related actions to the database
 */
public class UserDAO {
    private final DatabaseManager databaseManager;
    private static User currentUser;
    private static final Logger log = LogManager.getLogger();

    public UserDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    // TODO: Handle non-unique users.
    /**
     * Sets the current user given a username
     * @param username username entered in login page
     */
    public void setCurrentUser(String username) {
        // Update the currentUser variable and User database if necessary
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String userQuery = "SELECT * FROM Users WHERE name = ?";

            PreparedStatement findNoteStatement = conn.prepareStatement(userQuery);
            findNoteStatement.setString(1, username);
            ResultSet findNoteSet = findNoteStatement.executeQuery();

            /*
             * If result set is empty there isn't a user, so
             * we insert a new user into the database.
             */
            if (!findNoteSet.isBeforeFirst()) {
                String insertQuery = "INSERT INTO Users VALUES (?,?)";
                PreparedStatement insertStatement  = conn.prepareStatement(insertQuery);
                insertStatement.setString(2, username); // UserID set to 1 as no users exist yet.
                insertStatement.execute();
            }
            updateUser(username);
            System.out.println("User updated");
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
    }

    /**
     * Updates the current user to one specified.
     * @param name name of the user to update to.
     */
    public void updateUser(String name) {
        User user = new User(name);
        String userQuery = """
                SELECT * FROM Users WHERE name = ?
                """;
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            PreparedStatement statement = conn.prepareStatement(userQuery);
            statement.setString(1, name);
            ResultSet res = statement.executeQuery();
            if (res.next()) {
                user.setId(res.getInt(1));
            }
            log.info("Updated user. ");
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        currentUser = user;
    }
    public User getCurrentUser() {
        return currentUser;
    }
}
