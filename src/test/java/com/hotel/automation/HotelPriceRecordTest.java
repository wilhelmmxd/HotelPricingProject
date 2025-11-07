package com.hotel.automation;

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

/**
 * Unit tests for HotelPriceRecord class.
 * Follows Arrange-Act-Assert (AAA) pattern and uses JUnit 5 annotations.
 */
class HotelPriceRecordTest {
    
    private HotelPriceRecord record;
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Starting HotelPriceRecord tests...");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("HotelPriceRecord tests completed.");
    }
    
    @BeforeEach
    void setUp() {
        // Arrange: Create a new record before each test
        record = new HotelPriceRecord("Four Seasons", "Las Vegas", "2025-05-15", 450.99);
    }
    
    @AfterEach
    void tearDown() {
        // No cleanup needed for this test
        record = null;
    }
    
    @Test
    @DisplayName("Test parameterized constructor")
    void testParameterizedConstructor() {
        // Arrange & Act: Constructor called in setUp
        
        // Assert: Verify all fields are set correctly
        assertEquals("Four Seasons", record.getHotelName());
        assertEquals("Las Vegas", record.getCity());
        assertEquals("2025-05-15", record.getCheckinDate());
        assertEquals(450.99, record.getPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test default constructor and setters")
    void testDefaultConstructorAndSetters() {
        // Arrange: Create record with default constructor
        HotelPriceRecord newRecord = new HotelPriceRecord();
        
        // Act: Set values using setters
        newRecord.setHotelName("Ritz-Carlton");
        newRecord.setCity("New York City");
        newRecord.setCheckinDate("2025-06-01");
        newRecord.setPrice(599.99);
        
        // Assert: Verify all values are set correctly
        assertEquals("Ritz-Carlton", newRecord.getHotelName());
        assertEquals("New York City", newRecord.getCity());
        assertEquals("2025-06-01", newRecord.getCheckinDate());
        assertEquals(599.99, newRecord.getPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test getters return correct values")
    void testGetters() {
        // Arrange: Record already created in setUp
        
        // Act: Get values
        String hotelName = record.getHotelName();
        String city = record.getCity();
        String date = record.getCheckinDate();
        double price = record.getPrice();
        
        // Assert: Verify all getters work correctly
        assertEquals("Four Seasons", hotelName);
        assertEquals("Las Vegas", city);
        assertEquals("2025-05-15", date);
        assertEquals(450.99, price, 0.01);
    }
    
    @Test
    @DisplayName("Test toString method")
    void testToString() {
        // Arrange: Record already created in setUp
        
        // Act: Get string representation
        String stringRepresentation = record.toString();
        
        // Assert: Verify string contains key information
        assertNotNull(stringRepresentation);
        assertTrue(stringRepresentation.contains("Four Seasons"));
        assertTrue(stringRepresentation.contains("Las Vegas"));
        assertTrue(stringRepresentation.contains("2025-05-15"));
        assertTrue(stringRepresentation.contains("450.99"));
    }
    
    @Test
    @DisplayName("Test equals method with identical objects")
    void testEqualsIdentical() {
        // Arrange: Create identical record
        HotelPriceRecord identicalRecord = new HotelPriceRecord("Four Seasons", "Las Vegas", "2025-05-15", 450.99);
        
        // Act: Compare records
        boolean areEqual = record.equals(identicalRecord);
        
        // Assert: Verify they are equal
        assertTrue(areEqual, "Identical records should be equal");
    }
    
    @Test
    @DisplayName("Test equals method with different objects")
    void testEqualsDifferent() {
        // Arrange: Create different record
        HotelPriceRecord differentRecord = new HotelPriceRecord("Ritz-Carlton", "Miami", "2025-06-01", 399.99);
        
        // Act: Compare records
        boolean areEqual = record.equals(differentRecord);
        
        // Assert: Verify they are not equal
        assertFalse(areEqual, "Different records should not be equal");
    }
    
    @Test
    @DisplayName("Test equals method with same reference")
    void testEqualsSameReference() {
        // Arrange: Use same reference
        HotelPriceRecord sameRecord = record;
        
        // Act: Compare
        boolean areEqual = record.equals(sameRecord);
        
        // Assert: Verify equality
        assertTrue(areEqual, "Same reference should be equal");
    }
    
    @Test
    @DisplayName("Test equals method with null")
    void testEqualsNull() {
        // Arrange: null reference
        HotelPriceRecord nullRecord = null;
        
        // Act: Compare with null
        boolean areEqual = record.equals(nullRecord);
        
        // Assert: Verify not equal to null
        assertFalse(areEqual, "Record should not be equal to null");
    }
    
    @Test
    @DisplayName("Test hashCode consistency")
    void testHashCodeConsistency() {
        // Arrange: Record already created
        
        // Act: Get hash codes multiple times
        int hashCode1 = record.hashCode();
        int hashCode2 = record.hashCode();
        
        // Assert: Verify hash codes are consistent
        assertEquals(hashCode1, hashCode2, "Hash codes should be consistent");
    }
    
    @Test
    @DisplayName("Test hashCode for equal objects")
    void testHashCodeEqualObjects() {
        // Arrange: Create identical record
        HotelPriceRecord identicalRecord = new HotelPriceRecord("Four Seasons", "Las Vegas", "2025-05-15", 450.99);
        
        // Act: Get hash codes
        int hashCode1 = record.hashCode();
        int hashCode2 = identicalRecord.hashCode();
        
        // Assert: Verify equal objects have equal hash codes
        assertEquals(hashCode1, hashCode2, "Equal objects should have equal hash codes");
    }
    
    @Test
    @DisplayName("Test setting price to zero")
    void testSetPriceZero() {
        // Arrange: Record already created
        
        // Act: Set price to zero
        record.setPrice(0.0);
        
        // Assert: Verify price is zero
        assertEquals(0.0, record.getPrice(), 0.01);
    }
    
    @Test
    @DisplayName("Test setting large price value")
    void testSetLargePrice() {
        // Arrange: Record already created
        
        // Act: Set very large price
        record.setPrice(9999.99);
        
        // Assert: Verify large price is handled correctly
        assertEquals(9999.99, record.getPrice(), 0.01);
    }
}
