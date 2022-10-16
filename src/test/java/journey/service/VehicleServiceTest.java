package journey.service;

import journey.data.Vehicle;
import journey.repository.VehicleDAO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class VehicleServiceTest {

    @Test
    void regValidOne() {
        String error = VehicleService.regValid("", null);
        assertEquals("Please enter a registration", error);
    }

    @Test
    void regValidTwo() {
        String error = VehicleService.regValid("!Test", null);
        assertEquals("Cannot contain special characters", error);
    }

    @Test
    void regValidThree() {
        String error = VehicleService.regValid("Testing", null);
        assertEquals("Cannot be more than 6 characters", error);
    }

    @Test
    void regValidFour() {
        Vehicle vehicle = new Vehicle(2022, "make","model", "ABC123", "Type 2 Socketed", "AC");
        String error = VehicleService.regValid("ABC123", vehicle);
        assertEquals("A vehicle with this registration already exists for this user!", error);
    }

    @Test
    void makeValidOne() {
        String error = VehicleService.makeValid("!make");
        assertEquals("Cannot contain digits or special characters", error);
    }

    @Test
    void makeValidTwo() {
        String error = VehicleService.makeValid("");
        assertEquals("Please enter a make", error);
    }

    @Test
    void makeValidThree() {
        String error = VehicleService.makeValid("Thequickbrownfoxjumpsoverthelazydog");
        assertEquals("Make cannot be more than 20 characters long", error);
    }

    @Test
    void modelValidOne() {
        String error = VehicleService.modelValid("!model");
        assertEquals("Cannot contain special characters", error);
    }

    @Test
    void modelValidTwo() {
        String error = VehicleService.modelValid("");
        assertEquals("Please enter a model", error);
    }

    @Test
    void modelValidThree() {
        String error = VehicleService.modelValid("Thequickbrownfoxjumpsoverthelazydog");
        assertEquals("Model cannot be more than 20 characters long", error);
    }

    @Test
    void yearValidOne() {
        String error = VehicleService.yearValid("");
        assertEquals("Please enter a year", error);
    }

    @Test
    void yearValidTwo() {
        String error = VehicleService.yearValid("2024");
        assertEquals("Year is out of range", error);
    }

    @Test
    void yearValidThree() {
        String error = VehicleService.yearValid("1995");
        assertEquals("Year is out of range", error);
    }

    @Test
    void yearValidFour() {
        String error = VehicleService.yearValid("year");
        assertEquals("Year must be an integer", error);
    }

    @Test
    void currentValidTest() {
        String error = VehicleService.currentValid("");
        assertEquals("Please select a current type", error);
    }

    @Test
    void connectorValidTest() {
        String error = VehicleService.connectorValid("");
        assertEquals("Please select a connector type", error);
    }

    @Test
    void isValidTest() {
        boolean valid = VehicleService.isValid("Error", "Error", "Error", "Error", "Error", "Error");
        assertFalse(valid);
    }

    @Test
    void isValidTestTwo() {
        boolean valid = VehicleService.isValid("", "", "", "", "", "");
        assertTrue(valid);
    }
}
