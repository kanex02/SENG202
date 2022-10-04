package service;

/**
 * Service class to extract testable methods for logging in.
 */
public class LoginService {

    /**
     * Checks that a name is valid.

     * @param name name to check
     * @return whether the name contains only characters
     */
    public static Boolean checkUser(String name) {
        return name.matches("[a-z|A-Z]+");
    }
}
