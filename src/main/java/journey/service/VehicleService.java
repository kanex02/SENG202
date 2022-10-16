package journey.service;

import journey.Utils;
import journey.data.Vehicle;

import java.util.Objects;

public class VehicleService {

    private VehicleService() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Check a vehicle's registration input is valid.

     * @param registration registration to validate.
     * @param exists vehicle that currently has that registration.
     * @return error string.
     */
    public static String regValid(String registration, Vehicle exists) {
        String error = "";
        if (Objects.equals(registration, "")) {
            error = "Please enter a registration";
        } else if (!registration.matches(Utils.getCharacterDigit())) {
            error = "Cannot contain special characters";
        } else if (registration.length() > 6) {
            error = "Cannot be more than 6 characters";
        } else if (exists != null) {
            error = "A vehicle with this registration already exists for this user!";
        }
        return error;
    }

    /**
     * Check a vehicle's make input is valid.

     * @param make make to be validated.
     * @return error string.
     */
    public static String makeValid(String make) {
        String error = "";
        if (make.equals("")) {
            error = "Please enter a make";
        } else if (!make.matches(Utils.getCharacterOnly())) {
            error = "Cannot contain digits or special characters";
        } else if (make.length() > 20) {
            error = "Make cannot be more than 20 characters long";
        }
        return error;
    }

    /**
     * Check a Vehicle's model is valid.

     * @param model model to be validated.
     * @return error string.
     */
    public static String modelValid(String model) {
        String error = "";
        if (model.equals("")) {
            error = "Please enter a model";
        } else if (!model.matches(Utils.getCharacterDigit())) {
            error = "Cannot contain special characters";
        } else if (model.length() > 20) {
            error = "Model cannot be more than 20 characters long";
        }
        return error;
    }

    /**
     * Check a vehicle's year input is valid.

     * @param year to be checked.
     * @return error string.
     */
    public static String yearValid(String year) {
        String error = "";
        int intYear;
        if (year.equals("")) {
            error = "Please enter a year";
        } else {
            if (Utils.isInt(year)) {
                intYear = Integer.parseInt(year);
                String date = Utils.getDate();
                int currentYear = Integer.parseInt(date.split("/")[2]);
                if (intYear > currentYear || intYear < 1996) {
                    error = "Year is out of range";
                }
            } else {
                error = "Year must be an integer";
            }
        }
        return error;
    }

    /**
     * Check a vehicle's current input is valid.

     * @param current to be validated.
     * @return error string.
     */
    public static String currentValid(String current) {
        String error = "";
        if (current == null || current.equals("")) {
            error = "Please select a current type";
        }
        return error;
    }

    /**
     * Check a vehicle's connectors input is valid.

     * @param connector input to be validated.
     * @return error string.
     */
    public static String connectorValid(String connector) {
        String error = "";
        if (connector == null || connector.equals("")) {
            error = "Please select a connector type";
        }
        return error;
    }

    /**
     * Error checking for entering a vehicle.

     * @return whether result passed error checking or not (true/false).
     */
    public static boolean isValid(String regError, String makeError, String modelError, String yearError, String currentError, String connectorError) {
        return regError.equals("") && makeError.equals("") && modelError.equals("") && yearError.equals("") && currentError.equals("") && connectorError.equals("");
    }
}
