import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.example.hotelpricingproject.selenium.PageInteractionHelper;
import com.example.hotelpricingproject.selenium.WebDriverFactory;

public class Booking {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String BOOKING_BASE_URL = "https://www.booking.com/searchresults.html";

    public static void main(String[] args) {
        // Configuration: Ritz-Carlton in 5 cities over date range Nov 15 - May 1
        String hotelName = "Ritz-Carlton";
        List<String> cities = List.of("Dallas", "New York City", "Miami", "Dubai", "Los Angeles");
        LocalDate startDate = LocalDate.of(2025, 11, 26);
        LocalDate endDate = LocalDate.of(2026, 5, 1);

        // Ensure SQLite schema exists and show DB path
        initDatabase();
        if (!validateSchemaOrExit()) {
            System.out.println("❌ Schema invalid: id must be INTEGER PRIMARY KEY AUTOINCREMENT. Run: python .\\migrate_db.py");
            return;
        }
        System.out.println("Using SQLite at: " + new java.io.File("hotel_pricing.db").getAbsolutePath());

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║       HOTEL PRICE SCRAPER - MULTI-CITY ANALYSIS            ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println("\nHotel: " + hotelName);
        System.out.println("Cities: " + String.join(", ", cities));
        System.out.println("Date Range: " + startDate + " to " + endDate);
        System.out.println("Days to analyze: " + java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate));
        System.out.println("\n");

        Map<String, List<HotelPriceData>> allResults = new LinkedHashMap<>();

        for (String city : cities) {
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            System.out.println("📍 Scraping: " + hotelName + " in " + city);
            System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

            List<HotelPriceData> cityResults = scrapeCityPrices(hotelName, city, startDate, endDate);
            allResults.put(city, cityResults);

            if (!cityResults.isEmpty()) {
                System.out.println("✅ Found " + cityResults.size() + " price records for " + city);
                // Show lowest 3 prices
                cityResults.stream()
                        .sorted(Comparator.comparing(HotelPriceData::getPrice))
                        .limit(3)
                        .forEach(p -> System.out.println("   - " + p.getCheckInDate() + ": $" + p.getPrice()));
            } else {
                System.out.println("❌ No prices found for " + city);
            }
            System.out.println();
        }

        // Save all results to database
        System.out.println("\n📊 Saving data to SQLite database...");
        int totalSaved = 0;
        for (String city : cities) {
            List<HotelPriceData> prices = allResults.get(city);
            for (HotelPriceData data : prices) {
                if (saveToDatabase(data, hotelName)) {
                    // Soft verification: confirm row exists; continue on failure
                    if (!verifyInsert(hotelName, city, data.checkInDate)) {
                        System.out.println("⚠️  Insert not verified for " + city + " " + data.checkInDate);
                    }
                    totalSaved++;
                }
            }
            int insertedToday = countCityInsertsToday(city);
            System.out.println("ℹ️  Inserted today for " + city + ": " + insertedToday);
        }
        System.out.println("✅ Saved " + totalSaved + " records to database\n");

        // Print summary
        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ANALYSIS SUMMARY                        ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        for (String city : cities) {
            List<HotelPriceData> prices = allResults.get(city);
            if (!prices.isEmpty()) {
                BigDecimal minPrice = prices.stream()
                        .map(HotelPriceData::getPrice)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                BigDecimal avgPrice = prices.stream()
                        .map(HotelPriceData::getPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(new BigDecimal(prices.size()), 2, java.math.RoundingMode.HALF_UP);

                System.out.println(String.format("%-20s | Records: %3d | Min: $%7s | Avg: $%7s", 
                        city, prices.size(), minPrice, avgPrice));
            }
        }
    }

    /**
     * Validates schema for AUTOINCREMENT id; returns true if valid.
     */
    private static boolean validateSchemaOrExit() {
        String ddlCheck = "SELECT sql FROM sqlite_master WHERE type='table' AND name='hotel_prices'";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_pricing.db");
             java.sql.Statement stmt = conn.createStatement();
             java.sql.ResultSet rs = stmt.executeQuery(ddlCheck)) {
            if (rs.next()) {
                String sql = rs.getString(1);
                return sql != null && sql.toUpperCase().contains("INTEGER PRIMARY KEY AUTOINCREMENT");
            }
            return false;
        } catch (SQLException e) {
            System.out.println("⚠️  Schema validation failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Verifies that a row exists for the given hotel/city/check-in.
     * Soft check: returns false on error, but caller continues.
     */
    private static boolean verifyInsert(String hotelName, String city, LocalDate checkInDate) {
        String sql = "SELECT COUNT(*) FROM hotel_prices WHERE hotel_name=? AND city=? AND check_in_date=?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_pricing.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, hotelName);
            ps.setString(2, city);
            ps.setString(3, checkInDate.toString());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("⚠️  Verification query failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Counts how many rows were inserted today for a city.
     */
    private static int countCityInsertsToday(String city) {
        String sql = "SELECT COUNT(*) FROM hotel_prices WHERE city=? AND scraped_date=?";
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_pricing.db");
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, city);
            ps.setString(2, LocalDate.now().toString());
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            System.out.println("⚠️  Count query failed: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Ensure SQLite table exists with AUTOINCREMENT id.
     */
    private static void initDatabase() {
        String ddl = "CREATE TABLE IF NOT EXISTS hotel_prices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hotel_name TEXT NOT NULL, " +
                "city TEXT NOT NULL, " +
                "check_in_date TEXT NOT NULL, " +
                "check_out_date TEXT NOT NULL, " +
                "price REAL NOT NULL, " +
                "rating TEXT, " +
                "address TEXT, " +
                "scraped_date TEXT NOT NULL" +
                ")";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_pricing.db");
             java.sql.Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL");
            stmt.executeUpdate(ddl);
        } catch (SQLException e) {
            System.out.println("⚠️  Failed to initialize database: " + e.getMessage());
        }
    }

    /**
     * Scrapes hotel prices for a city across a date range
     */
    public static boolean saveToDatabase(HotelPriceData data, String hotelName) {
        String sql = "INSERT INTO hotel_prices (hotel_name, city, check_in_date, check_out_date, price, rating, address, scraped_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:hotel_pricing.db");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, hotelName);
            pstmt.setString(2, data.city);
            // Store dates as ISO-8601 text to avoid epoch integers in SQLite
            pstmt.setString(3, data.checkInDate.toString());
            pstmt.setString(4, data.checkOutDate.toString());
            pstmt.setBigDecimal(5, data.price);
            pstmt.setString(6, data.rating);
            pstmt.setString(7, data.address);
            pstmt.setString(8, LocalDate.now().toString());
            
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            // Auto-create table and retry once if missing
            if (e.getMessage() != null && e.getMessage().contains("no such table: hotel_prices")) {
                System.out.println("ℹ️  Table missing. Creating and retrying insert...");
                initDatabase();
                try (Connection conn2 = DriverManager.getConnection("jdbc:sqlite:hotel_pricing.db");
                     PreparedStatement pstmt2 = conn2.prepareStatement(sql)) {
                    pstmt2.setString(1, hotelName);
                    pstmt2.setString(2, data.city);
                    pstmt2.setString(3, data.checkInDate.toString());
                    pstmt2.setString(4, data.checkOutDate.toString());
                    pstmt2.setBigDecimal(5, data.price);
                    pstmt2.setString(6, data.rating);
                    pstmt2.setString(7, data.address);
                    pstmt2.setString(8, LocalDate.now().toString());
                    pstmt2.executeUpdate();
                    return true;
                } catch (SQLException ex2) {
                    System.out.println("⚠️  Failed to save after init: " + ex2.getMessage());
                    return false;
                }
            } else {
                System.out.println("⚠️  Failed to save: " + e.getMessage());
                return false;
            }
        }
    }

    /**
     * Scrapes hotel prices for a city across a date range
     */
    public static List<HotelPriceData> scrapeCityPrices(String hotelName, String city, 
                                                        LocalDate startDate, LocalDate endDate) {
        List<HotelPriceData> results = new ArrayList<>();
        WebDriver driver = null;

        try {
            driver = createWebDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;

            // Scrape every 3 days in the date range
            LocalDate currentDate = startDate;

            while (!currentDate.isAfter(endDate)) {
                LocalDate checkOut = currentDate.plusDays(1);

                try {
                    String url = buildBookingUrl(hotelName, city, currentDate, checkOut);
                    System.out.print("  Scraping " + currentDate + "... ");

                    driver.get(url);
                    Thread.sleep(3000);

                    PageInteractionHelper.handleCookies(driver, wait);
                    PageInteractionHelper.handlePopups(driver);
                    PageInteractionHelper.scrollPage(driver, js);

                    WebElement hotelCard = findHotelInResults(driver, hotelName);

                    if (hotelCard != null) {
                        HotelPriceData priceData = extractPriceData(hotelCard, hotelName, city, currentDate, checkOut);
                        if (priceData != null) {
                            results.add(priceData);
                            System.out.println("✓ $" + priceData.getPrice());
                        } else {
                            System.out.println("⚠ Found but price extraction failed");
                        }
                    } else {
                        System.out.println("✗ Hotel not found");
                    }

                } catch (Exception e) {
                    System.out.println("✗ Error: " + e.getMessage());
                }

                // Move to next date (every 3 days)
                currentDate = currentDate.plusDays(3);
            }

        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        return results;
    }

    private static String buildBookingUrl(String hotelName, String city, LocalDate checkIn, LocalDate checkOut) {
        String searchTerm = (hotelName + " " + city).replace(" ", "+");
        return String.format(
                "%s?ss=%s&checkin=%s&checkout=%s&group_adults=2&no_rooms=1",
                BOOKING_BASE_URL,
                searchTerm,
                checkIn.format(DATE_FORMATTER),
                checkOut.format(DATE_FORMATTER)
        );
    }

    private static WebDriver createWebDriver() {
        return WebDriverFactory.createDefault();
    }

    // Page interaction methods moved to PageInteractionHelper utility class

    private static WebElement findHotelInResults(WebDriver driver, String hotelName) {
        try {
            List<WebElement> cards = driver.findElements(By.cssSelector("[data-testid='property-card']"));

            for (WebElement card : cards) {
                try {
                    WebElement titleElement = card.findElement(By.cssSelector("[data-testid='title']"));
                    String title = titleElement.getText().toLowerCase();

                    if (title.contains(hotelName.toLowerCase())) {
                        return card;
                    }
                } catch (Exception e) {
                    // Continue searching
                }
            }
        } catch (Exception e) {
            // No results
        }
        return null;
    }

    private static HotelPriceData extractPriceData(WebElement card, String hotelName, String city, 
                                                   LocalDate checkIn, LocalDate checkOut) {
        try {
            String title = card.findElement(By.cssSelector("[data-testid='title']")).getText();
            String rating = null;
            String price = null;

            // Extract rating
            try {
                rating = card.findElement(By.cssSelector("[data-testid='review-score']")).getText();
            } catch (Exception e) {
                rating = "N/A";
            }

            // Extract price
            try {
                WebElement priceElem = card.findElement(By.cssSelector("[data-testid='price-and-discounted-price']"));
                price = priceElem.getText();
            } catch (Exception e) {
                try {
                    List<WebElement> elements = card.findElements(By.cssSelector("span, div"));
                    for (WebElement elem : elements) {
                        String text = elem.getText();
                        if (text.matches(".*\\$\\s?\\d+.*")) {
                            price = text;
                            break;
                        }
                    }
                } catch (Exception ex) {
                    // No price found
                }
            }

            if (price != null && !price.isEmpty()) {
                BigDecimal priceValue = extractPriceValue(price);
                if (priceValue != null && priceValue.compareTo(BigDecimal.ZERO) > 0) {
                    return new HotelPriceData(hotelName, city, checkIn, checkOut, priceValue, rating, title);
                }
            }

        } catch (Exception e) {
            // Failed to extract
        }

        return null;
    }

    private static BigDecimal extractPriceValue(String priceText) {
        try {
            String cleaned = priceText.replaceAll("[^0-9.]", "");
            if (!cleaned.isEmpty()) {
                return new BigDecimal(cleaned);
            }
        } catch (Exception e) {
            // Parse error
        }
        return null;
    }

    // Simple data class
    static class HotelPriceData {
        public final String hotelName;
        public final String city;
        public final LocalDate checkInDate;
        public final LocalDate checkOutDate;
        public final BigDecimal price;
        public final String rating;
        public final String address;

        HotelPriceData(String hotelName, String city, LocalDate checkIn, LocalDate checkOut, 
                      BigDecimal price, String rating, String address) {
            this.hotelName = hotelName;
            this.city = city;
            this.checkInDate = checkIn;
            this.checkOutDate = checkOut;
            this.price = price;
            this.rating = rating;
            this.address = address;
        }

        public LocalDate getCheckInDate() { return checkInDate; }
        public BigDecimal getPrice() { return price; }
        public String getRating() { return rating; }
        public String getAddress() { return address; }

        @Override
        public String toString() {
            return String.format("%s | %s-%s | $%s | Rating: %s | %s",
                    city, checkInDate, checkOutDate, price, rating, address);
        }
    }
}
