package com.example.hotelpricingproject.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HotelPrice Entity Tests")
class HotelPriceTest {

    private HotelPrice hotelPrice;

    @BeforeEach
    void setUp() {
        // Arrange - Create a fresh HotelPrice for each test
        hotelPrice = new HotelPrice();
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        hotelPrice = null;
    }

    @Test
    @DisplayName("Should create HotelPrice with all fields")
    void testHotelPriceCreation() {
        // Arrange
        String hotelName = "Ritz-Carlton";
        String city = "Las Vegas";
        LocalDate checkIn = LocalDate.of(2025, 11, 27);
        LocalDate checkOut = LocalDate.of(2025, 11, 28);
        BigDecimal price = new BigDecimal("250.00");
        String rating = "4.5";
        String address = "123 Main St, Las Vegas";

        // Act
        hotelPrice = new HotelPrice(hotelName, city, checkIn, checkOut, price, rating, address);

        // Assert
        assertEquals(hotelName, hotelPrice.getHotelName());
        assertEquals(city, hotelPrice.getCity());
        assertEquals(checkIn, hotelPrice.getCheckInDate());
        assertEquals(checkOut, hotelPrice.getCheckOutDate());
        assertEquals(price, hotelPrice.getPrice());
        assertEquals(rating, hotelPrice.getRating());
        assertEquals(address, hotelPrice.getAddress());
        assertNotNull(hotelPrice.getScrapedDate());
    }

    @Test
    @DisplayName("Should set and get all properties")
    void testSettersAndGetters() {
        // Arrange
        hotelPrice.setHotelName("Ritz-Carlton");
        hotelPrice.setCity("Miami");
        hotelPrice.setCheckInDate(LocalDate.of(2025, 12, 1));
        hotelPrice.setCheckOutDate(LocalDate.of(2025, 12, 2));
        hotelPrice.setPrice(new BigDecimal("300.00"));
        hotelPrice.setRating("4.7");
        hotelPrice.setAddress("456 Ocean Blvd, Miami");

        // Act & Assert
        assertEquals("Ritz-Carlton", hotelPrice.getHotelName());
        assertEquals("Miami", hotelPrice.getCity());
        assertEquals(LocalDate.of(2025, 12, 1), hotelPrice.getCheckInDate());
        assertEquals(LocalDate.of(2025, 12, 2), hotelPrice.getCheckOutDate());
        assertEquals(new BigDecimal("300.00"), hotelPrice.getPrice());
        assertEquals("4.7", hotelPrice.getRating());
        assertEquals("456 Ocean Blvd, Miami", hotelPrice.getAddress());
    }

    @Test
    @DisplayName("Should set scraped date automatically on creation")
    void testScrapedDateAutomaticSet() {
        // Arrange
        LocalDate todayBeforeCreation = LocalDate.now();

        // Act
        hotelPrice = new HotelPrice("Hotel", "City", LocalDate.now(), LocalDate.now().plusDays(1), 
                                    new BigDecimal("100.00"), "4.0", "Address");

        // Assert
        assertNotNull(hotelPrice.getScrapedDate());
        assertTrue(!hotelPrice.getScrapedDate().isBefore(todayBeforeCreation));
    }

    @Test
    @DisplayName("Should generate string representation")
    void testToString() {
        // Arrange
        hotelPrice = new HotelPrice("Ritz-Carlton", "Las Vegas", LocalDate.of(2025, 11, 27), 
                                   LocalDate.of(2025, 11, 28), new BigDecimal("250.00"), "4.5", 
                                   "123 Main St, Las Vegas");

        // Act
        String result = hotelPrice.toString();

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("Ritz-Carlton"));
        assertTrue(result.contains("Las Vegas"));
        assertTrue(result.contains("250.00"));
    }
}
