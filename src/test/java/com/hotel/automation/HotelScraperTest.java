package com.hotel.automation;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for HotelScraper class.
 * Follows Arrange-Act-Assert (AAA) pattern and uses JUnit 5 annotations.
 */
class HotelScraperTest {
    
    private HotelScraper scraper;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Starting HotelScraper tests...");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("HotelScraper tests completed.");
    }
    
    @BeforeEach
    void setUp() {
        // Arrange: Create a new scraper before each test
        scraper = new HotelScraper();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up WebDriver if it was initialized
        if (scraper != null) {
            try {
                scraper.closeDriver();
            } catch (Exception e) {
                // Ignore errors during cleanup
            }
        }
    }
    
    @Test
    @DisplayName("Test scraper initialization")
    void testScraperCreation() {
        // Arrange: Scraper created in setUp
        
        // Act: Verify scraper is not null
        
        // Assert: Scraper object should be created
        assertNotNull(scraper, "Scraper should be instantiated");
    }
    
    @Test
    @DisplayName("Test getting hotel list")
    void testGetHotels() {
        // Arrange: Static method call
        
        // Act: Get hotel list
        String[] hotels = HotelScraper.getHotels();
        
        // Assert: Verify hotel list is correct
        assertNotNull(hotels, "Hotel list should not be null");
        assertEquals(1, hotels.length, "Should have 1 hotel");
        assertTrue(containsValue(hotels, "Ritz-Carlton"), "Should contain Ritz-Carlton");
    }
    
    @Test
    @DisplayName("Test getting city list")
    void testGetCities() {
        // Arrange: Static method call
        
        // Act: Get city list
        String[] cities = HotelScraper.getCities();
        
        // Assert: Verify city list is correct
        assertNotNull(cities, "City list should not be null");
        assertEquals(5, cities.length, "Should have 5 cities");
        assertTrue(containsValue(cities, "Las Vegas"), "Should contain Las Vegas");
        assertTrue(containsValue(cities, "New York City"), "Should contain New York City");
        assertTrue(containsValue(cities, "Miami"), "Should contain Miami");
        assertTrue(containsValue(cities, "Paris"), "Should contain Paris");
        assertTrue(containsValue(cities, "Los Angeles"), "Should contain Los Angeles");
    }
    
    @Test
    @DisplayName("Test scraping single hotel-city combination")
    void testScrapeHotelCity() {
        // Arrange: Scraper already created
        String hotel = "Four Seasons";
        String city = "Las Vegas";
        
        // Act: Scrape data for one hotel-city combination
        // Note: Using mock data, no actual web scraping
        List<HotelPriceRecord> records = scraper.scrapeHotelCity(hotel, city);
        
        // Assert: Verify data was collected
        assertNotNull(records, "Records list should not be null");
        assertFalse(records.isEmpty(), "Should have at least some records");
        
        // Verify all records have correct hotel and city
        for (HotelPriceRecord record : records) {
            assertEquals(hotel, record.getHotelName(), "Hotel name should match");
            assertEquals(city, record.getCity(), "City should match");
            assertNotNull(record.getCheckinDate(), "Check-in date should not be null");
            assertTrue(record.getPrice() > 0, "Price should be positive");
        }
    }
    
    @Test
    @DisplayName("Test scraping generates data for all hotels and cities")
    void testScrapeAllHotels() {
        // Arrange: Scraper already created
        
        // Act: Scrape all hotels (using mock data)
        List<HotelPriceRecord> allRecords = scraper.scrapeAllHotels();
        
        // Assert: Verify data was collected for all combinations
        assertNotNull(allRecords, "Records list should not be null");
        assertFalse(allRecords.isEmpty(), "Should have records");
        
        // Should have records for 5 hotels × 5 cities = 25 combinations
        // Each combination has multiple dates
        assertTrue(allRecords.size() >= 25, "Should have records for multiple hotel-city combinations");
    }
    
    @Test
    @DisplayName("Test scraped data has valid date format")
    void testScrapedDataDateFormat() {
        // Arrange: Scraper already created
        
        // Act: Scrape data for one hotel
        List<HotelPriceRecord> records = scraper.scrapeHotelCity("Park Hyatt", "Miami");
        
        // Assert: Verify date format (YYYY-MM-DD)
        assertFalse(records.isEmpty(), "Should have records to test");
        for (HotelPriceRecord record : records) {
            String date = record.getCheckinDate();
            assertNotNull(date, "Date should not be null");
            assertTrue(date.matches("\\d{4}-\\d{2}-\\d{2}"), 
                      "Date should be in YYYY-MM-DD format: " + date);
        }
    }
    
    @Test
    @DisplayName("Test scraped data has realistic prices")
    void testScrapedDataRealisticPrices() {
        // Arrange: Scraper already created
        
        // Act: Scrape data
        List<HotelPriceRecord> records = scraper.scrapeHotelCity("Ritz-Carlton", "New York City");
        
        // Assert: Verify prices are realistic (for luxury hotels)
        assertFalse(records.isEmpty(), "Should have records to test");
        for (HotelPriceRecord record : records) {
            double price = record.getPrice();
            assertTrue(price > 0, "Price should be positive");
            assertTrue(price < 10000, "Price should be less than $10,000 for one night");
            // Luxury hotels in NYC should typically be over $200
            assertTrue(price > 100, "Luxury hotel price should be reasonable");
        }
    }
    
    @Test
    @DisplayName("Test scraper handles different hotel-city combinations")
    void testDifferentHotelCityCombinations() {
        // Arrange: Multiple combinations to test
        String[][] combinations = {
            {"Four Seasons", "Paris"},
            {"St. Regis", "Los Angeles"},
            {"Waldorf Astoria", "Miami"}
        };
        
        // Act & Assert: Test each combination
        for (String[] combo : combinations) {
            List<HotelPriceRecord> records = scraper.scrapeHotelCity(combo[0], combo[1]);
            
            assertNotNull(records, "Records should not be null for " + combo[0] + " in " + combo[1]);
            assertFalse(records.isEmpty(), "Should have records for " + combo[0] + " in " + combo[1]);
            
            // Verify first record has correct hotel and city
            HotelPriceRecord firstRecord = records.get(0);
            assertEquals(combo[0], firstRecord.getHotelName());
            assertEquals(combo[1], firstRecord.getCity());
        }
    }
    
    @Test
    @DisplayName("Test close driver doesn't throw exception when not initialized")
    void testCloseDriverNotInitialized() {
        // Arrange: Scraper created but driver not initialized
        
        // Act & Assert: Should not throw exception
        assertDoesNotThrow(() -> scraper.closeDriver(), 
                          "Closing uninitialized driver should not throw exception");
    }
    
    /**
     * Helper method to check if array contains a value.
     */
    private boolean containsValue(String[] array, String value) {
        for (String item : array) {
            if (item.equals(value)) {
                return true;
            }
        }
        return false;
    }
}
