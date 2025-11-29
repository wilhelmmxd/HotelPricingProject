package com.example.hotelpricingproject.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import com.example.hotelpricingproject.entity.HotelPrice;
import com.example.hotelpricingproject.repository.HotelPriceRepository;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver",
    "spring.datasource.username=sa",
    "spring.datasource.password="
})
@Transactional
@DisplayName("Hotel Analysis Service Tests")
class HotelAnalysisServiceTest {

    @Autowired
    private HotelAnalysisService hotelAnalysisService;

    @Autowired
    private HotelPriceRepository hotelPriceRepository;

    private HotelPrice testPrice1;
    private HotelPrice testPrice2;
    private HotelPrice testPrice3;

    @BeforeEach
    void setUp() {
        // Arrange - Create test data
        LocalDate checkIn = LocalDate.of(2025, 11, 27);
        LocalDate checkOut = LocalDate.of(2025, 11, 28);

        testPrice1 = new HotelPrice(
                "Ritz-Carlton",
                "Las Vegas",
                checkIn,
                checkOut,
                new BigDecimal("250.00"),
                "4.5",
                "123 Main St, Las Vegas"
        );

        testPrice2 = new HotelPrice(
                "Ritz-Carlton",
                "Las Vegas",
                checkIn.plusDays(1),
                checkOut.plusDays(1),
                new BigDecimal("200.00"),
                "4.5",
                "123 Main St, Las Vegas"
        );

        testPrice3 = new HotelPrice(
                "Ritz-Carlton",
                "New York City",
                checkIn,
                checkOut,
                new BigDecimal("350.00"),
                "4.6",
                "789 5th Ave, NYC"
        );

        hotelPriceRepository.save(testPrice1);
        hotelPriceRepository.save(testPrice2);
        hotelPriceRepository.save(testPrice3);
    }

    @AfterEach
    void tearDown() {
        // Clean up after tests
        hotelPriceRepository.deleteAll();
    }

    @Test
    @DisplayName("Should find lowest prices for a hotel in a city")
    void testFindLowestPrices() {
        // Arrange - Test data already set up in setUp()

        // Act
        List<HotelPrice> lowestPrices = hotelAnalysisService.findLowestPrices("Ritz-Carlton", "Las Vegas");

        // Assert
        assertNotNull(lowestPrices);
        assertEquals(2, lowestPrices.size());
        assertEquals(new BigDecimal("200.00"), lowestPrices.get(0).getPrice());
        assertEquals(new BigDecimal("250.00"), lowestPrices.get(1).getPrice());
    }

    @Test
    @DisplayName("Should get prices for hotel in city")
    void testGetPricesForHotelInCity() {
        // Arrange - Test data set up in setUp()

        // Act
        List<HotelPrice> prices = hotelAnalysisService.getPricesForHotelInCity("Ritz-Carlton", "Las Vegas");

        // Assert
        assertNotNull(prices);
        assertEquals(2, prices.size());
        assertTrue(prices.stream().allMatch(p -> p.getHotelName().equals("Ritz-Carlton")));
        assertTrue(prices.stream().allMatch(p -> p.getCity().equals("Las Vegas")));
    }

    @Test
    @DisplayName("Should return empty list when no prices found")
    void testFindLowestPricesNoData() {
        // Arrange - Query for non-existent data

        // Act
        List<HotelPrice> lowestPrices = hotelAnalysisService.findLowestPrices("Non-Existent Hotel", "Tokyo");

        // Assert
        assertNotNull(lowestPrices);
        assertTrue(lowestPrices.isEmpty());
    }

    @Test
    @DisplayName("Should generate analysis report for multiple cities")
    void testGenerateAnalysisReport() {
        // Arrange
        List<String> cities = List.of("Las Vegas", "New York City");

        // Act
        String report = hotelAnalysisService.generateAnalysisReport("Ritz-Carlton", cities);

        // Assert
        assertNotNull(report);
        assertTrue(report.contains("Ritz-Carlton"));
        assertTrue(report.contains("Las Vegas"));
        assertTrue(report.contains("New York City"));
    }

    @Test
    @DisplayName("Should find lowest prices within date range")
    void testFindLowestPricesByDateRange() {
        // Arrange
        LocalDate startDate = LocalDate.of(2025, 11, 25);
        LocalDate endDate = LocalDate.of(2025, 11, 30);

        // Act
        List<HotelPrice> prices = hotelAnalysisService.findLowestPrices(
                "Ritz-Carlton", "Las Vegas", startDate, endDate);

        // Assert
        assertNotNull(prices);
        assertTrue(prices.size() <= 2);
        prices.forEach(p -> assertTrue(
                !p.getCheckInDate().isBefore(startDate) && 
                !p.getCheckInDate().isAfter(endDate)
        ));
    }
}
