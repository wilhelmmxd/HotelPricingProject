import java.util.List;
import java.util.Map;

import com.hotel.automation.DataAnalyzer;
import com.hotel.automation.DatabaseManager;
import com.hotel.automation.HotelPriceRecord;
import com.hotel.automation.HotelScraper;
import com.hotel.automation.ReportGenerator;

/**
 * Main application class for Hotel Data Automation.
 * Orchestrates the entire process: scraping, storing, analyzing, and reporting.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("=== Hotel Data Automation System ===");
        System.out.println("Starting data collection and analysis...\n");
        
        // Step 1: Initialize Database
        System.out.println("[1/5] Initializing database...");
        DatabaseManager dbManager = new DatabaseManager();
        dbManager.initializeDatabase();
        
        // Optional: Clear old data for fresh run
        // dbManager.clearAllRecords();
        
        // Step 2: Scrape Hotel Data
        System.out.println("\n[2/5] Scraping hotel price data...");
        HotelScraper scraper = new HotelScraper();
        scraper.initializeDriver();
        
        try {
            List<HotelPriceRecord> scrapedData = scraper.scrapeAllHotels();
            System.out.println("Scraped " + scrapedData.size() + " price records.");
            
            // Step 3: Store Data in Database
            System.out.println("\n[3/5] Storing data in database...");
            int insertedCount = dbManager.insertBatch(scrapedData);
            System.out.println("Inserted " + insertedCount + " records into database.");
            System.out.println("Total records in database: " + dbManager.getRecordCount());
            
        } finally {
            scraper.closeDriver();
        }
        
        // Step 4: Analyze Data
        System.out.println("\n[4/5] Analyzing price data...");
        DataAnalyzer analyzer = new DataAnalyzer(dbManager);
        Map<String, List<HotelPriceRecord>> analysisResults = analyzer.analyzeLowestPrices();
        
        // Display sample results
        System.out.println("\nSample Analysis Results:");
        int count = 0;
        for (Map.Entry<String, List<HotelPriceRecord>> entry : analysisResults.entrySet()) {
            if (count++ < 3) { // Show first 3 combinations
                System.out.println("\n" + entry.getKey() + ":");
                List<HotelPriceRecord> records = entry.getValue();
                for (int i = 0; i < Math.min(3, records.size()); i++) {
                    HotelPriceRecord record = records.get(i);
                    System.out.printf("  %d. %s - $%.2f%n", 
                            i + 1, record.getCheckinDate(), record.getPrice());
                }
            }
        }
        
        // Step 5: Generate Report
        System.out.println("\n[5/5] Generating Word document report...");
        ReportGenerator reportGenerator = new ReportGenerator();
        boolean success = reportGenerator.generateReport(analysisResults);
        
        if (success) {
            System.out.println("\n=== Process Complete ===");
            System.out.println("Report generated: " + ReportGenerator.getDefaultReportName());
            System.out.println("Database file: hotels.db");
        } else {
            System.err.println("\nError: Failed to generate report.");
        }
    }
}
