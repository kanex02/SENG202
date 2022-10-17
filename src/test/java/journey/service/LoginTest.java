package journey.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginTest {

    @Test
    void digitNameTest() {
        boolean valid = LoginService.checkUser("Test1");
        assertFalse(valid);
    }

    @Test
    void specialNameTest() {
        boolean valid = LoginService.checkUser("Test!");
        assertFalse(valid);
    }

    @Test
    void spaceNameTest() {
        boolean valid = LoginService.checkUser("Test User");
        assertTrue(valid);
    }
}
