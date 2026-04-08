package main.najah.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import main.najah.code.UserService;

@DisplayName("UserService Tests")
public class TestUserService {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @ParameterizedTest(name = "email \"{0}\" should be valid")
    @ValueSource(strings = {
        "user@test.com",
        "name.surname@site.org",
        "a@b.co"
    })
    @DisplayName("isValidEmail should return true for valid emails")
    void testValidEmails(String email) {
        assertTrue(userService.isValidEmail(email));
    }

    @ParameterizedTest(name = "email \"{0}\" should be invalid")
    @NullSource
    @ValueSource(strings = {
        "plainaddress",
        "user@domain",
        "domain.com",
        ""
    })
    @DisplayName("isValidEmail should return false for invalid emails")
    void testInvalidEmails(String email) {
        assertFalse(userService.isValidEmail(email));
    }

    @Test
    @DisplayName("authenticate should return true for correct admin credentials")
    void testAuthenticateValid() {
        assertAll(
            () -> assertTrue(userService.authenticate("admin", "1234")),
            () -> assertFalse(userService.authenticate("admin", "wrong"))
        );
    }

    @ParameterizedTest(name = "username={0}, password={1} should fail")
    @CsvSource({
        "user,1234",
        "admin,wrong",
        "ADMIN,1234",
        "'',''"
    })
    @DisplayName("authenticate should return false for invalid credentials")
    void testAuthenticateInvalid(String username, String password) {
        assertFalse(userService.authenticate(username, password));
    }

    @Test
    @DisplayName("user service operations should finish quickly")
    void testTimeout() {
        assertTimeout(Duration.ofMillis(100), () -> {
            userService.isValidEmail("user@test.com");
            userService.authenticate("admin", "1234");
        });
    }
}