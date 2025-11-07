package com.hotel.automation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

/**
 * Unit tests for DataAnalyzer class.
 * Follows Arrange-Act-Assert (AAA) pattern and uses JUnit 5 annotations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataAnalyzerTest {
    
    private DatabaseManager dbManager;
    private DataAnalyzer analyzer;
    
    @BeforeAll
    void setUpBeforeClass() {
        System.out.println("Starting DataAnalyzer tests...");
    }
    
    @AfterAll
    void tearDownAfterClass() {
        System.out.println("DataAnalyzer tests completed.");
    }
    
    @BeforeEach
    void setUp() {
        // Arrange: Set up database and analyzer before each test
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        dbManager.clearAllRecords();
        analyzer = new DataAnalyzer(dbManager);
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        if (dbManager != null) {
            dbManager.clearAllRecords();
        }
    }
    
    @Test
    @DisplayName("Test analyzer initialization")
    void testAnalyzerCreation() {
        // Arrange: Analyzer created in setUp
        
        // Act: Verify analyzer is not null
        
        // Assert: Analyzer should be instantiated
        assertNotNull(analyzer, "Analyzer should be instantiated");
    }
    
    @Test
    @DisplayName("Test analyzing lowest prices with data")
    void testAnalyzeLowestPrices() {
        // Arrange: Insert test data for multiple hotels and cities
        insertTestData();
        
        // Act: Analyze lowest prices
        Map<String, List<HotelPriceRecord>> results = analyzer.analyzeLowestPrices();
        
        // Assert: Verify results
        assertNotNull(results, "Results should not be null");
        assertFalse(results.isEmpty(), "Results should not be empty");
        assertTrue(results.size() >= 2, "Should have at least 2 hotel-city combinations");
    }
    
    @Test
    @DisplayName("Test analyzing lowest prices with empty database")
    void testAnalyzeLowestPricesEmptyDatabase() {
        // Arrange: Empty database (cleared in setUp)
        
        // Act: Analyze lowest prices
        Map<String, List<HotelPriceRecord>> results = analyzer.analyzeLowestPrices();
        
        // Assert: Verify empty results
        assertNotNull(results, "Results should not be null");
        assertTrue(results.isEmpty(), "Results should be empty for empty database");
    }
    
    @Test
    @DisplayName("Test getting lowest prices for specific hotel-city")
    void testGetLowestPricesForHotelCity() {
        // Arrange: Insert test data
        String hotel = "Test Hotel";
        String city = "Test City";
        dbManager.insertHotelPrice(hotel, city, "2025-05-01", 500.0);
        dbManager.insertHotelPrice(hotel, city, "2025-05-02", 300.0);
        dbManager.insertHotelPrice(hotel, city, "2025-05-03", 400.0);
        dbManager.insertHotelPrice(hotel, city, "2025-05-04", 200.0);
        
        // Act: Get lowest 3 prices
        List<HotelPriceRecord> lowestPrices = analyzer.getLowestPricesForHotelCity(hotel, city, 3);
        
        // Assert: Verify results are sorted by price
        assertEquals(3, lowestPrices.size(), "Should return 3 records");
        assertEquals(200.0, lowestPrices.get(0).getPrice(), 0.01, "First should be lowest");
        assertEquals(300.0, lowestPrices.get(1).getPrice(), 0.01, "Second should be second lowest");
        assertEquals(400.0, lowestPrices.get(2).getPrice(), 0.01, "Third should be third lowest");
    }
    
    @Test
    @DisplayName("Test calculating statistics with valid data")
    void testCalculateStatistics() {
        // Arrange: Create list of records
        List<HotelPriceRecord> records = new ArrayList<>();
        records.add(new HotelPriceRecord("Hotel", "City", "2025-05-01", 100.0));
        records.add(new HotelPriceRecord("Hotel", "City", "2025-05-02", 200.0));
        records.add(new HotelPriceRecord("Hotel", "City", "2025-05-03", 300.0));
        
        // Act: Calculate statistics
        DataAnalyzer.PriceStatistics stats = analyzer.calculateStatistics(records);
        
        // Assert: Verify statistics
        assertNotNull(stats, "Statistics should not be null");
        assertEquals(100.0, stats.getMinPrice(), 0.01, "Min price should be 100.0");
        assertEquals(300.0, stats.getMaxPrice(), 0.01, "Max price should be 300.0");
        assertEquals(200.0, stats.getAveragePrice(), 0.01, "Average price should be 200.0");
    }
    
    @Test
    @DisplayName("Test calculating statistics with empty list")
    void testCalculateStatisticsEmptyList() {
        // Arrange: Create empty list
        List<HotelPriceRecord> emptyList = new ArrayList<>();
        
        // Act: Calculate statistics
        DataAnalyzer.PriceStatistics stats = analyzer.calculateStatistics(emptyList);
        
        // Assert: Verify statistics for empty list
        assertNotNull(stats, "Statistics should not be null even for empty list");
        assertEquals(0.0, stats.getMinPrice(), 0.01, "Min should be 0");
        assertEquals(0.0, stats.getMaxPrice(), 0.01, "Max should be 0");
        assertEquals(0.0, stats.getAveragePrice(), 0.01, "Average should be 0");
    }
    
    @Test
    @DisplayName("Test calculating statistics with null list")
    void testCalculateStatisticsNullList() {
        // Arrange: Null list
        List<HotelPriceRecord> nullList = null;
        
        // Act: Calculate statistics
        DataAnalyzer.PriceStatistics stats = analyzer.calculateStatistics(nullList);
        
        // Assert: Verify statistics for null list
        assertNotNull(stats, "Statistics should not be null even for null input");
        assertEquals(0.0, stats.getMinPrice(), 0.01);
        assertEquals(0.0, stats.getMaxPrice(), 0.01);
        assertEquals(0.0, stats.getAveragePrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test getting unique hotels")
    void testGetUniqueHotels() {
        // Arrange: Insert data for multiple hotels
        dbManager.insertHotelPrice("Hotel A", "City X", "2025-05-01", 100.0);
        dbManager.insertHotelPrice("Hotel A", "City Y", "2025-05-01", 110.0);
        dbManager.insertHotelPrice("Hotel B", "City X", "2025-05-01", 120.0);
        dbManager.insertHotelPrice("Hotel B", "City Y", "2025-05-01", 130.0);
        dbManager.insertHotelPrice("Hotel C", "City X", "2025-05-01", 140.0);
        
        // Act: Get unique hotels
        List<String> uniqueHotels = analyzer.getUniqueHotels();
        
        // Assert: Verify unique hotels
        assertNotNull(uniqueHotels, "Hotel list should not be null");
        assertEquals(3, uniqueHotels.size(), "Should have 3 unique hotels");
        assertTrue(uniqueHotels.contains("Hotel A"), "Should contain Hotel A");
        assertTrue(uniqueHotels.contains("Hotel B"), "Should contain Hotel B");
        assertTrue(uniqueHotels.contains("Hotel C"), "Should contain Hotel C");
    }
    
    @Test
    @DisplayName("Test getting unique cities")
    void testGetUniqueCities() {
        // Arrange: Insert data for multiple cities
        dbManager.insertHotelPrice("Hotel A", "City X", "2025-05-01", 100.0);
        dbManager.insertHotelPrice("Hotel A", "City Y", "2025-05-01", 110.0);
        dbManager.insertHotelPrice("Hotel B", "City X", "2025-05-01", 120.0);
        dbManager.insertHotelPrice("Hotel B", "City Z", "2025-05-01", 130.0);
        
        // Act: Get unique cities
        List<String> uniqueCities = analyzer.getUniqueCities();
        
        // Assert: Verify unique cities
        assertNotNull(uniqueCities, "City list should not be null");
        assertEquals(3, uniqueCities.size(), "Should have 3 unique cities");
        assertTrue(uniqueCities.contains("City X"), "Should contain City X");
        assertTrue(uniqueCities.contains("City Y"), "Should contain City Y");
        assertTrue(uniqueCities.contains("City Z"), "Should contain City Z");
    }
    
    @Test
    @DisplayName("Test PriceStatistics toString method")
    void testPriceStatisticsToString() {
        // Arrange: Create statistics object
        DataAnalyzer.PriceStatistics stats = new DataAnalyzer.PriceStatistics(100.0, 500.0, 300.0);
        
        // Act: Get string representation
        String stringRep = stats.toString();
        
        // Assert: Verify string contains values
        assertNotNull(stringRep, "String representation should not be null");
        assertTrue(stringRep.contains("100"), "Should contain min value");
        assertTrue(stringRep.contains("500"), "Should contain max value");
        assertTrue(stringRep.contains("300"), "Should contain average value");
    }
    
    /**
     * Helper method to insert test data into the database.
     */
    private void insertTestData() {
        dbManager.insertHotelPrice("Four Seasons", "Las Vegas", "2025-05-01", 450.0);
        dbManager.insertHotelPrice("Four Seasons", "Las Vegas", "2025-05-02", 400.0);
        dbManager.insertHotelPrice("Four Seasons", "Las Vegas", "2025-05-03", 500.0);
        dbManager.insertHotelPrice("Ritz-Carlton", "Miami", "2025-05-01", 380.0);
        dbManager.insertHotelPrice("Ritz-Carlton", "Miami", "2025-05-02", 350.0);
        dbManager.insertHotelPrice("Ritz-Carlton", "Miami", "2025-05-03", 420.0);
    }
}
