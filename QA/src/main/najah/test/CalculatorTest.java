package main.najah.test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.najah.code.Product;

@DisplayName("Product Tests")
public class TestProduct {

    private Product product;

    @BeforeAll
    static void beforeAll() {
        System.out.println("BeforeAll: setup complete");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("AfterAll: cleanup complete");
    }

    @BeforeEach
    void setUp() {
        product = new Product("Laptop", 1000.0);
        System.out.println("BeforeEach: test setup complete");
    }

    @AfterEach
    void tearDown() {
        System.out.println("AfterEach: test finished");
    }

    @Test
    @DisplayName("constructor should create product with valid data")
    void testConstructorValid() {
        assertAll(
            () -> assertEquals("Laptop", product.getName()),
            () -> assertEquals(1000.0, product.getFinalPrice(), 0.0001)
        );
    }

    @Test
    @DisplayName("constructor should reject negative price")
    void testConstructorNegativePrice() {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> new Product("Bad Product", -10.0)
        );
        assertEquals("Price must be non-negative", ex.getMessage());
    }

    @ParameterizedTest(name = "discount {0}% should be applied")
    @ValueSource(doubles = {0.0, 10.0, 25.0, 50.0})
    @DisplayName("applyDiscount should accept valid discounts")
    void testApplyDiscountValid(double discount) {
        product.applyDiscount(discount);
        double expected = 1000.0 * (1 - discount / 100.0);

        assertAll(
            () -> assertEquals(expected, product.getFinalPrice(), 0.0001),
            () -> assertEquals("Laptop", product.getName())
        );
    }

    @ParameterizedTest(name = "discount {0}% should be rejected")
    @ValueSource(doubles = {-1.0, 51.0, 100.0})
    @DisplayName("applyDiscount should reject invalid discounts")
    void testApplyDiscountInvalid(double discount) {
        IllegalArgumentException ex = assertThrows(
            IllegalArgumentException.class,
            () -> product.applyDiscount(discount)
        );
        assertEquals("Invalid discount", ex.getMessage());
    }

    @Test
    @DisplayName("getFinalPrice should return discounted price correctly")
    void testGetFinalPrice() {
        product.applyDiscount(20.0);

        assertAll(
            () -> assertEquals(800.0, product.getFinalPrice(), 0.0001),
            () -> assertNotEquals(1000.0, product.getFinalPrice(), 0.0001)
        );
    }

    @Test
    @DisplayName("product operations should finish quickly")
    void testTimeout() {
        assertTimeout(Duration.ofMillis(100), () -> {
            product.applyDiscount(15.0);
            product.getFinalPrice();
        });
    }
}