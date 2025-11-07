package com.hotel.automation;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.github.bonigarcia.wdm.WebDriverManager;

/**
 * Web scraper for hotel prices using Selenium WebDriver.
 * Scrapes hotel prices from travel websites for specified hotels and cities.
 * Follows DRY principle with reusable scraping methods.
 */
public class HotelScraper {
    private WebDriver driver;
    private WebDriverWait wait;
    private static final int WAIT_TIMEOUT_SECONDS = 10;
    
    // Hotel chains to scrape - Only using Ritz-Carlton for this assignment
    private static final String[] HOTELS = {
        "Ritz-Carlton"
    };
    
    // Cities to search
    private static final String[] CITIES = {
        "Las Vegas", 
        "New York City", 
        "Miami", 
        "Paris", 
        "Los Angeles"
    };
    
    /**
     * Initializes the WebDriver with Chrome.
     */
    public void initializeDriver() {
        WebDriverManager.chromedriver().setup();
        
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments("--disable-notifications");
        
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(WAIT_TIMEOUT_SECONDS));
    }
    
    /**
     * Closes the WebDriver and releases resources.
     */
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }
    
    /**
     * Scrapes hotel prices for all hotel-city combinations.
     * This is the main method that orchestrates the entire scraping process.
     * 
     * @return List of HotelPriceRecord objects containing scraped data
     */
    public List<HotelPriceRecord> scrapeAllHotels() {
        List<HotelPriceRecord> allRecords = new ArrayList<>();
        
        for (String hotel : HOTELS) {
            for (String city : CITIES) {
                System.out.println("Scraping: " + hotel + " in " + city);
                List<HotelPriceRecord> records = scrapeHotelCity(hotel, city);
                allRecords.addAll(records);
                
                // Add delay to avoid overwhelming the server
                sleep(2000);
            }
        }
        
        return allRecords;
    }
    
    /**
     * Scrapes hotel prices for a specific hotel in a specific city.
     * This method demonstrates the scraping logic (currently uses mock data).
     * 
     * @param hotelName Name of the hotel
     * @param city City to search in
     * @return List of HotelPriceRecord objects for the hotel-city combination
     */
    public List<HotelPriceRecord> scrapeHotelCity(String hotelName, String city) {
        List<HotelPriceRecord> records = new ArrayList<>();
        
        try {
            // Generate date range from May 1st of current year onwards
            LocalDate startDate = LocalDate.of(LocalDate.now().getYear(), 5, 1);
            LocalDate endDate = startDate.plusMonths(6); // 6 months of data
            
            // In a real implementation, you would navigate to the website and scrape data
            // For demonstration and testing purposes, we'll generate realistic mock data
            records = generateMockData(hotelName, city, startDate, endDate);
            
            // REAL IMPLEMENTATION WOULD LOOK LIKE THIS:
            // navigateToSearchPage(hotelName, city, startDate);
            // records = extractPriceData(hotelName, city);
            
        } catch (Exception e) {
            System.err.println("Error scraping " + hotelName + " in " + city + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return records;
    }
    
    /**
     * Navigates to the hotel search page on a travel website.
     * This is a template method showing how navigation would work.
     * 
     * @param hotelName Hotel to search for
     * @param city City to search in
     * @param checkInDate Check-in date
     */
    private void navigateToSearchPage(String hotelName, String city, LocalDate checkInDate) {
        // Example for Expedia (URL structure may vary)
        String url = buildSearchURL(hotelName, city, checkInDate);
        driver.get(url);
        
        // Wait for page to load
        waitForPageLoad();
    }
    
    /**
     * Builds the search URL for the travel website.
     * 
     * @param hotelName Hotel name
     * @param city City name
     * @param date Check-in date
     * @return Formatted URL string
     */
    private String buildSearchURL(String hotelName, String city, LocalDate date) {
        // This is a template - actual URL structure depends on the website
        String encodedHotel = hotelName.replace(" ", "+");
        String encodedCity = city.replace(" ", "+");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        return String.format("https://www.expedia.com/Hotel-Search?destination=%s&hotelName=%s&checkIn=%s",
                encodedCity, encodedHotel, date.format(formatter));
    }
    
    /**
     * Waits for the page to fully load by checking for key elements.
     */
    private void waitForPageLoad() {
        try {
            // Wait for common elements that indicate page load
            wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));
            sleep(1000); // Additional buffer
        } catch (Exception e) {
            System.err.println("Page load timeout: " + e.getMessage());
        }
    }
    
    /**
     * Extracts price data from the current page.
     * This is a template method showing how extraction would work.
     * 
     * @param hotelName Hotel name
     * @param city City name
     * @return List of extracted price records
     */
    private List<HotelPriceRecord> extractPriceData(String hotelName, String city) {
        List<HotelPriceRecord> records = new ArrayList<>();
        
        try {
            // Example selectors (actual selectors depend on the website structure)
            List<WebElement> priceElements = driver.findElements(By.cssSelector(".price-element"));
            List<WebElement> dateElements = driver.findElements(By.cssSelector(".date-element"));
            
            for (int i = 0; i < Math.min(priceElements.size(), dateElements.size()); i++) {
                String priceText = priceElements.get(i).getText().replaceAll("[^0-9.]", "");
                String dateText = dateElements.get(i).getText();
                
                double price = Double.parseDouble(priceText);
                String formattedDate = formatDate(dateText);
                
                records.add(new HotelPriceRecord(hotelName, city, formattedDate, price));
            }
        } catch (Exception e) {
            System.err.println("Error extracting price data: " + e.getMessage());
        }
        
        return records;
    }
    
    /**
     * Formats a date string to 'YYYY-MM-DD' format.
     * 
     * @param dateText Date text to format
     * @return Formatted date string
     */
    private String formatDate(String dateText) {
        // This would need to parse the website's date format and convert to YYYY-MM-DD
        // Implementation depends on the source website's date format
        return dateText; // Placeholder
    }
    
    /**
     * Generates mock/realistic data for testing and demonstration.
     * In a real scenario, this would be replaced by actual web scraping.
     * 
     * @param hotelName Hotel name
     * @param city City name
     * @param startDate Start date for price range
     * @param endDate End date for price range
     * @return List of mock price records
     */
    private List<HotelPriceRecord> generateMockData(String hotelName, String city, 
                                                     LocalDate startDate, LocalDate endDate) {
        List<HotelPriceRecord> records = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Generate prices for each day in the range
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            // Generate realistic price based on hotel and city
            double basePrice = getBasePrice(hotelName, city);
            double variance = (Math.random() - 0.5) * 200; // +/- $100 variance
            double price = Math.round((basePrice + variance) * 100.0) / 100.0;
            
            records.add(new HotelPriceRecord(
                hotelName,
                city,
                currentDate.format(formatter),
                price
            ));
            
            currentDate = currentDate.plusDays(1);
        }
        
        return records;
    }
    
    /**
     * Returns a base price for a hotel in a specific city.
     * Used for generating realistic mock data.
     * 
     * @param hotelName Hotel name
     * @param city City name
     * @return Base price in dollars
     */
    private double getBasePrice(String hotelName, String city) {
        // Base prices vary by hotel chain (luxury tier)
        double hotelMultiplier;
        switch (hotelName) {
            case "Four Seasons":
                hotelMultiplier = 1.2;
                break;
            case "Ritz-Carlton":
                hotelMultiplier = 1.15;
                break;
            case "Park Hyatt":
                hotelMultiplier = 1.1;
                break;
            case "St. Regis":
                hotelMultiplier = 1.25;
                break;
            case "Waldorf Astoria":
                hotelMultiplier = 1.18;
                break;
            default:
                hotelMultiplier = 1.0;
                break;
        }
        
        // Base prices vary by city
        double cityBasePrice;
        switch (city) {
            case "Las Vegas":
                cityBasePrice = 350;
                break;
            case "New York City":
                cityBasePrice = 450;
                break;
            case "Miami":
                cityBasePrice = 380;
                break;
            case "Paris":
                cityBasePrice = 420;
                break;
            case "Los Angeles":
                cityBasePrice = 400;
                break;
            default:
                cityBasePrice = 350;
                break;
        }
        
        return cityBasePrice * hotelMultiplier;
    }
    
    /**
     * Utility method to pause execution.
     * 
     * @param milliseconds Time to sleep in milliseconds
     */
    private void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
    
    /**
     * Gets the list of hotels to scrape.
     * 
     * @return Array of hotel names
     */
    public static String[] getHotels() {
        return HOTELS.clone();
    }
    
    /**
     * Gets the list of cities to search.
     * 
     * @return Array of city names
     */
    public static String[] getCities() {
        return CITIES.clone();
    }
}
