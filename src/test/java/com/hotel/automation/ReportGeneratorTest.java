package com.hotel.automation;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for ReportGenerator class.
 * Follows Arrange-Act-Assert (AAA) pattern and uses JUnit 5 annotations.
 */
class ReportGeneratorTest {
    
    private ReportGenerator reportGenerator;
    private static final String TEST_REPORT_NAME = "Test_Report.docx";
    
    @BeforeAll
    static void setUpBeforeClass() {
        System.out.println("Starting ReportGenerator tests...");
    }
    
    @AfterAll
    static void tearDownAfterClass() {
        System.out.println("ReportGenerator tests completed.");
        // Clean up any test reports
        deleteTestReports();
    }
    
    @BeforeEach
    void setUp() {
        // Arrange: Create a new report generator before each test
        reportGenerator = new ReportGenerator();
    }
    
    @AfterEach
    void tearDown() {
        // Clean up test files after each test
        deleteTestFile(TEST_REPORT_NAME);
    }
    
    @Test
    @DisplayName("Test report generator initialization")
    void testReportGeneratorCreation() {
        // Arrange: Report generator created in setUp
        
        // Act: Verify report generator is not null
        
        // Assert: Report generator should be instantiated
        assertNotNull(reportGenerator, "Report generator should be instantiated");
    }
    
    @Test
    @DisplayName("Test generating report with valid data")
    void testGenerateReportWithValidData() {
        // Arrange: Create test data
        Map<String, List<HotelPriceRecord>> testData = createTestData();
        
        // Act: Generate report
        boolean success = reportGenerator.generateReport(testData, TEST_REPORT_NAME);
        
        // Assert: Verify report was created successfully
        assertTrue(success, "Report generation should succeed");
        
        File reportFile = new File(TEST_REPORT_NAME);
        assertTrue(reportFile.exists(), "Report file should exist");
        assertTrue(reportFile.length() > 0, "Report file should not be empty");
    }
    
    @Test
    @DisplayName("Test generating report with empty data")
    void testGenerateReportWithEmptyData() {
        // Arrange: Create empty data map
        Map<String, List<HotelPriceRecord>> emptyData = new HashMap<>();
        
        // Act: Generate report
        boolean success = reportGenerator.generateReport(emptyData, TEST_REPORT_NAME);
        
        // Assert: Verify report is still created (with summary only)
        assertTrue(success, "Report generation should succeed even with empty data");
        
        File reportFile = new File(TEST_REPORT_NAME);
        assertTrue(reportFile.exists(), "Report file should exist");
    }
    
    @Test
    @DisplayName("Test generating report with default filename")
    void testGenerateReportDefaultFilename() {
        // Arrange: Create test data
        Map<String, List<HotelPriceRecord>> testData = createTestData();
        
        // Act: Generate report with default filename
        boolean success = reportGenerator.generateReport(testData);
        
        // Assert: Verify report was created with default name
        assertTrue(success, "Report generation should succeed");
        
        File reportFile = new File(ReportGenerator.getDefaultReportName());
        assertTrue(reportFile.exists(), "Default report file should exist");
        
        // Clean up default report
        reportFile.delete();
    }
    
    @Test
    @DisplayName("Test default report name")
    void testGetDefaultReportName() {
        // Arrange: Static method call
        
        // Act: Get default report name
        String defaultName = ReportGenerator.getDefaultReportName();
        
        // Assert: Verify default name is correct
        assertNotNull(defaultName, "Default name should not be null");
        assertTrue(defaultName.endsWith(".docx"), "Default name should end with .docx");
        assertTrue(defaultName.contains("Hotel"), "Default name should contain 'Hotel'");
    }
    
    @Test
    @DisplayName("Test generating report with multiple hotel-city combinations")
    void testGenerateReportMultipleCombinations() {
        // Arrange: Create data with multiple combinations
        Map<String, List<HotelPriceRecord>> multiData = new HashMap<>();
        
        multiData.put("Four Seasons - Las Vegas", createPriceList("Four Seasons", "Las Vegas", 10));
        multiData.put("Ritz-Carlton - Miami", createPriceList("Ritz-Carlton", "Miami", 10));
        multiData.put("Park Hyatt - Paris", createPriceList("Park Hyatt", "Paris", 10));
        
        // Act: Generate report
        boolean success = reportGenerator.generateReport(multiData, TEST_REPORT_NAME);
        
        // Assert: Verify report was created
        assertTrue(success, "Report generation should succeed");
        
        File reportFile = new File(TEST_REPORT_NAME);
        assertTrue(reportFile.exists(), "Report file should exist");
        assertTrue(reportFile.length() > 1000, "Report should have substantial content");
    }
    
    @Test
    @DisplayName("Test report file is created in correct format")
    void testReportFileFormat() {
        // Arrange: Create test data
        Map<String, List<HotelPriceRecord>> testData = createTestData();
        
        // Act: Generate report
        boolean success = reportGenerator.generateReport(testData, TEST_REPORT_NAME);
        
        // Assert: Verify file format
        assertTrue(success, "Report generation should succeed");
        
        File reportFile = new File(TEST_REPORT_NAME);
        assertTrue(reportFile.getName().endsWith(".docx"), "Report should be a .docx file");
    }
    
    /**
     * Helper method to create test data.
     */
    private Map<String, List<HotelPriceRecord>> createTestData() {
        Map<String, List<HotelPriceRecord>> data = new HashMap<>();
        
        List<HotelPriceRecord> vegasRecords = new ArrayList<>();
        vegasRecords.add(new HotelPriceRecord("Four Seasons", "Las Vegas", "2025-05-01", 450.0));
        vegasRecords.add(new HotelPriceRecord("Four Seasons", "Las Vegas", "2025-05-02", 400.0));
        vegasRecords.add(new HotelPriceRecord("Four Seasons", "Las Vegas", "2025-05-03", 380.0));
        
        data.put("Four Seasons - Las Vegas", vegasRecords);
        
        return data;
    }
    
    /**
     * Helper method to create a list of price records.
     */
    private List<HotelPriceRecord> createPriceList(String hotel, String city, int count) {
        List<HotelPriceRecord> records = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            String date = String.format("2025-05-%02d", i + 1);
            double price = 300.0 + (i * 10);
            records.add(new HotelPriceRecord(hotel, city, date, price));
        }
        
        return records;
    }
    
    /**
     * Helper method to delete a test file.
     */
    private void deleteTestFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
    
    /**
     * Static helper method to delete all test reports.
     */
    private static void deleteTestReports() {
        deleteFile(TEST_REPORT_NAME);
        deleteFile(ReportGenerator.getDefaultReportName());
    }
    
    /**
     * Static helper to delete a file.
     */
    private static void deleteFile(String filename) {
        File file = new File(filename);
        if (file.exists()) {
            file.delete();
        }
    }
}
