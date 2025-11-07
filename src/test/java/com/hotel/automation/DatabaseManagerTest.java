package com.hotel.automation;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
 * Unit tests for DatabaseManager class.
 * Follows Arrange-Act-Assert (AAA) pattern and uses JUnit 5 annotations.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DatabaseManagerTest {
    
    private DatabaseManager dbManager;
    private static final String TEST_HOTEL = "Test Hotel";
    private static final String TEST_CITY = "Test City";
    private static final String TEST_DATE = "2025-05-15";
    private static final double TEST_PRICE = 299.99;
    
    @BeforeAll
    void setUpBeforeClass() {
        System.out.println("Starting DatabaseManager tests...");
    }
    
    @AfterAll
    void tearDownAfterClass() {
        System.out.println("DatabaseManager tests completed.");
    }
    
    @BeforeEach
    void setUp() {
        // Arrange: Create a fresh database manager before each test
        dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        dbManager.clearAllRecords();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up after each test
        if (dbManager != null) {
            dbManager.clearAllRecords();
        }
    }
    
    @Test
    @DisplayName("Test database initialization creates table successfully")
    void testInitializeDatabase() throws Exception {
        // Arrange: Database manager already created in setUp
        
        // Act: Initialize database (already done in setUp, but testing again)
        dbManager.initializeDatabase();
        
        // Assert: Verify table exists by checking connection
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='hotel_prices'")) {
            assertTrue(rs.next(), "hotel_prices table should exist");
            assertEquals("hotel_prices", rs.getString("name"));
        }
    }
    
    @Test
    @DisplayName("Test inserting a single hotel price record")
    void testInsertHotelPrice() {
        // Arrange: Test data already defined as constants
        
        // Act: Insert a hotel price record
        boolean result = dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, TEST_DATE, TEST_PRICE);
        
        // Assert: Verify insertion was successful
        assertTrue(result, "Insert operation should return true");
        assertEquals(1, dbManager.getRecordCount(), "Database should contain 1 record");
    }
    
    @Test
    @DisplayName("Test inserting multiple records in batch")
    void testInsertBatch() {
        // Arrange: Create a list of test records
        List<HotelPriceRecord> records = new ArrayList<>();
        records.add(new HotelPriceRecord("Hotel A", "City X", "2025-05-01", 100.0));
        records.add(new HotelPriceRecord("Hotel B", "City Y", "2025-05-02", 200.0));
        records.add(new HotelPriceRecord("Hotel C", "City Z", "2025-05-03", 300.0));
        
        // Act: Insert batch of records
        int insertedCount = dbManager.insertBatch(records);
        
        // Assert: Verify all records were inserted
        assertEquals(3, insertedCount, "Should insert 3 records");
        assertEquals(3, dbManager.getRecordCount(), "Database should contain 3 records");
    }
    
    @Test
    @DisplayName("Test retrieving lowest prices for a hotel-city combination")
    void testGetLowestPrices() {
        // Arrange: Insert multiple records with varying prices
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, "2025-05-01", 500.0);
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, "2025-05-02", 300.0);
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, "2025-05-03", 400.0);
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, "2025-05-04", 200.0);
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, "2025-05-05", 350.0);
        
        // Act: Get lowest 3 prices
        List<HotelPriceRecord> lowestPrices = dbManager.getLowestPrices(TEST_HOTEL, TEST_CITY, 3);
        
        // Assert: Verify correct number and order
        assertEquals(3, lowestPrices.size(), "Should return 3 records");
        assertEquals(200.0, lowestPrices.get(0).getPrice(), 0.01, "First should be lowest price");
        assertEquals(300.0, lowestPrices.get(1).getPrice(), 0.01, "Second should be second lowest");
        assertEquals(350.0, lowestPrices.get(2).getPrice(), 0.01, "Third should be third lowest");
    }
    
    @Test
    @DisplayName("Test getting unique hotel-city combinations")
    void testGetUniqueHotelCityCombinations() {
        // Arrange: Insert records with multiple hotel-city combinations
        dbManager.insertHotelPrice("Hotel A", "City X", "2025-05-01", 100.0);
        dbManager.insertHotelPrice("Hotel A", "City X", "2025-05-02", 110.0);
        dbManager.insertHotelPrice("Hotel A", "City Y", "2025-05-01", 120.0);
        dbManager.insertHotelPrice("Hotel B", "City X", "2025-05-01", 130.0);
        
        // Act: Get unique combinations
        List<String[]> combinations = dbManager.getUniqueHotelCityCombinations();
        
        // Assert: Verify correct number of unique combinations
        assertEquals(3, combinations.size(), "Should have 3 unique hotel-city combinations");
    }
    
    @Test
    @DisplayName("Test clearing all records from database")
    void testClearAllRecords() {
        // Arrange: Insert some records
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, TEST_DATE, TEST_PRICE);
        dbManager.insertHotelPrice("Another Hotel", "Another City", "2025-06-01", 399.99);
        assertEquals(2, dbManager.getRecordCount(), "Should have 2 records before clear");
        
        // Act: Clear all records
        dbManager.clearAllRecords();
        
        // Assert: Verify database is empty
        assertEquals(0, dbManager.getRecordCount(), "Database should be empty after clear");
    }
    
    @Test
    @DisplayName("Test getting record count")
    void testGetRecordCount() {
        // Arrange: Start with empty database
        assertEquals(0, dbManager.getRecordCount(), "Should start with 0 records");
        
        // Act: Insert records
        dbManager.insertHotelPrice(TEST_HOTEL, TEST_CITY, TEST_DATE, TEST_PRICE);
        int countAfterFirst = dbManager.getRecordCount();
        dbManager.insertHotelPrice("Hotel 2", "City 2", "2025-06-01", 199.99);
        int countAfterSecond = dbManager.getRecordCount();
        
        // Assert: Verify counts are correct
        assertEquals(1, countAfterFirst, "Should have 1 record after first insert");
        assertEquals(2, countAfterSecond, "Should have 2 records after second insert");
    }
    
    @Test
    @DisplayName("Test connection establishment")
    void testGetConnection() throws Exception {
        // Arrange: Database manager already created
        
        // Act: Get a connection
        Connection conn = dbManager.getConnection();
        
        // Assert: Verify connection is valid
        assertNotNull(conn, "Connection should not be null");
        assertFalse(conn.isClosed(), "Connection should be open");
        
        // Cleanup
        conn.close();
        assertTrue(conn.isClosed(), "Connection should be closed after close()");
    }
    
    @Test
    @DisplayName("Test batch insert with empty list")
    void testInsertBatchEmptyList() {
        // Arrange: Create empty list
        List<HotelPriceRecord> emptyList = new ArrayList<>();
        
        // Act: Insert empty batch
        int insertedCount = dbManager.insertBatch(emptyList);
        
        // Assert: Verify no records inserted
        assertEquals(0, insertedCount, "Should insert 0 records from empty list");
        assertEquals(0, dbManager.getRecordCount(), "Database should remain empty");
    }
    
    @Test
    @DisplayName("Test getting lowest prices when no records exist")
    void testGetLowestPricesNoRecords() {
        // Arrange: Empty database (cleared in setUp)
        
        // Act: Try to get lowest prices for non-existent hotel-city
        List<HotelPriceRecord> lowestPrices = dbManager.getLowestPrices("Nonexistent", "Nowhere", 10);
        
        // Assert: Verify empty list is returned
        assertNotNull(lowestPrices, "Result should not be null");
        assertTrue(lowestPrices.isEmpty(), "Result should be empty list");
    }
}
