package journey.repository;

import journey.Utils;
import journey.data.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Concrete implementation of Database Access Object that handles all user related actions to the database.
 */
public class UserDAO {
    private final DatabaseManager databaseManager;
    private static final Logger log = LogManager.getLogger();

    public UserDAO() {
        databaseManager = DatabaseManager.getInstance();
    }

    /**
     * Sets the current user given a username.

     * @param username username entered in login page.
     */
    public User setCurrentUser(String username) {
        // Update the currentUser variable and User database if necessary
        Connection conn = null;
        User user = null;
        try {
            conn = databaseManager.connect();
            String userQuery = "SELECT * FROM Users WHERE name = ?";

            try (PreparedStatement preparedStatement = conn.prepareStatement(userQuery)) {
                preparedStatement.setString(1, username);
                ResultSet findUserSet = preparedStatement.executeQuery();

                /*
                 * If result set is empty there isn't a user, so
                 * we insert a new user into the database.
                 */
                if (!findUserSet.isBeforeFirst()) {
                    String insertQuery = "INSERT INTO Users VALUES (?,?)";
                    try (PreparedStatement preparedStatement1 = conn.prepareStatement(insertQuery)) {
                        preparedStatement1.setString(2, username); // UserID set to 1 as no users exist yet.
                        preparedStatement1.execute();
                    }
                }
                user = updateUser(username);
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return user;
    }

    /**
     * Updates the current user to one specified.

     * @param name name of the user to update to.
     */
    public User updateUser(String name) {
        User user = new User(name);
        String userQuery = """
                SELECT * FROM Users WHERE name = ?
                """;
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            try (PreparedStatement preparedStatement = conn.prepareStatement(userQuery)) {
                preparedStatement.setString(1, name);
                ResultSet res = preparedStatement.executeQuery();
                if (res.next()) {
                    user.setId(res.getInt(1));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        } finally {
            Utils.closeConn(conn);
        }
        return user;
    }

    /**
     * Gets all the users in the database.

     * @return List of users.
     */
    public User[] getUsers() {
        Connection conn = null;
        ArrayList<User> res = new ArrayList<>();
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Users";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    res.add(new User(rs.getString("name")));
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return res.toArray(User[]::new);
    }

    /**
     * Check if the name already exists in the Users database.

     * @param name name to check.
     * @return if the name is in the database of Users.
     */
    public boolean nameInDB(String name) {
        Connection conn = null;
        boolean inDB = false;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT name FROM Users where name = ? ";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setString(1, name);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    inDB = true;
                }
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return inDB;
    }

    /**
     * Puts new Username into the database.

     * @param id username ID.
     * @param newName updated name.
     */
    public void updateUserName(int id, String newName) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "UPDATE Users SET name = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setString(1, newName);
                ps.setInt(2, id);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
    }

    /**
     * get User from database.

     * @param id User ID
     * @return User Object
     */
    public User getUser(int id) {
        Connection conn = null;
        try {
            conn = databaseManager.connect();
            String sqlQuery = "SELECT * FROM Users WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                ps.setInt(1, id);
                ResultSet resultSet = ps.executeQuery();

                User user = new User(resultSet.getString("name"));
                user.setId(id);
                return user;
            }
        } catch (SQLException e) {
            log.error(e);
        }
        Utils.closeConn(conn);
        return null;
    }
}

