# AI Agent Instructions for HotelPricingProject

## Project Overview
**HotelPricingProject** is a production-ready Spring Boot 3.5.6 application (Java 17) that performs automated hotel price extraction from Booking.com using Selenium WebDriver. The system combines Spring Boot REST APIs with web scraping, SQLite persistence, and Python database utilities.

### Key Architecture Decisions
- **Hybrid Java/Python stack**: Spring Boot for application logic; Python utilities for database management
- **SQLite database**: Lightweight, file-based persistence (`hotel_pricing.db`) with JPA/Hibernate ORM
- **Service-oriented design**: Clear separation between scraping (`HotelScraperService`), analysis (`HotelAnalysisService`), and REST endpoints (`HotelController`)
- **Anti-detection web scraping**: ChromeDriver with user-agent spoofing, webdriver property masking, and popup handling
- **AAA testing pattern**: JUnit 5 tests use Arrange-Act-Assert with `@BeforeEach`/`@AfterEach` lifecycle hooks

## Critical Workflows

### Building & Running
```powershell
# Build the project (uses Maven wrapper)
.\mvnw.cmd clean package

# Run all tests (10 tests across entity + service layers)
.\mvnw.cmd test

# Run Spring Boot application (REST API on port 8080)
.\mvnw.cmd spring-boot:run

# Run standalone Booking.java scraper directly
java -jar target\HotelPricingProject-0.0.1-SNAPSHOT.jar
```

### Database Maintenance (Python Utilities)
**Requirements**: Python 3.x (tested with Python 3.13), `sqlite3` module (included in stdlib)
```powershell
# Inspect database schema and contents
python .\inspect_db.py

# Migrate table to ensure AUTOINCREMENT on id column
python .\migrate_db.py

# Smart insert with duplicate detection (UPDATE vs INSERT)
python .\smart_insert.py

# Initialize fresh database schema
python .\init_db.py

# Bulk populate test data
python .\populate_db.py
```

### Key Dependencies
- **Spring Boot**: `spring-boot-starter-data-jpa`, `spring-boot-starter-web-services`
- **Database**: `sqlite-jdbc`, `hibernate-community-dialects` (for SQLite dialect)
- **Selenium**: `selenium-java` for ChromeDriver automation
- **Testing**: JUnit 5 (`spring-boot-starter-test`), H2 in-memory database for test isolation

## Important Patterns & Conventions

### Selenium Anti-Detection Pattern (HotelScraperService.java)
Every ChromeDriver instance must use these configurations to bypass Booking.com bot detection:
```java
ChromeOptions options = new ChromeOptions();
options.addArguments("--disable-blink-features=AutomationControlled");
options.addArguments("user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36...");
options.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
options.setExperimentalOption("useAutomationExtension", false);
// Mask webdriver property via JavaScript
js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");
```
**Critical**: Always include popup handling (`handleCookies()`, `handlePopups()`) and scroll-to-load (`scrollPage()`) after page navigation.

### Data Extraction with Fallback Selectors
The scraper uses **multiple CSS selector attempts** to handle UI variations:
```java
String[] selectors = {
    "[data-testid='price-and-discounted-price']",
    ".price_price",
    "[class*='price']"
};
for (String selector : selectors) {
    try {
        return card.findElement(By.cssSelector(selector));
    } catch (NoSuchElementException e) {
        // Try next selector
    }
}
```
**Pattern location**: `extractPrice()`, `extractText()` methods in `HotelScraperService.java`.

### JPA Entity Pattern with Auto-Timestamp
`HotelPrice` entity automatically sets `scrapedDate` in no-arg constructor:
```java
@Column(nullable = false, updatable = false)
private LocalDate scrapedDate;

public HotelPrice() {
    this.scrapedDate = LocalDate.now(); // Auto-timestamp on creation
}
```
**Never manually set `scrapedDate`** — it's managed by the entity constructor.

### REST API Endpoint Structure
All endpoints follow `/api/hotels/*` pattern:
- **POST** `/api/hotels/scrape` — Trigger scraping for hotel/city/date range
- **POST** `/api/hotels/scrape-multi-city` — Bulk scrape across 5 cities (Las Vegas, NYC, Miami, Paris, LA)
- **GET** `/api/hotels/lowest-prices` — Get 10 lowest prices for hotel/city
- **GET** `/api/hotels/lowest-prices-by-range` — Filter lowest prices by date range
- **GET** `/api/hotels/prices` — Get all prices for hotel in city
- **GET** `/api/hotels/analysis-report` — Generate multi-city summary report

### AAA Testing Style (JUnit 5)
All tests follow strict **Arrange-Act-Assert** structure:
```java
@BeforeEach
void setUp() {
    // Arrange - Create test data
    testPrice1 = new HotelPrice("Ritz-Carlton", "Las Vegas", ...);
    hotelPriceRepository.save(testPrice1);
}

@Test
@DisplayName("Should find lowest prices for a hotel in a city")
void testFindLowestPrices() {
    // Act
    List<HotelPrice> lowestPrices = hotelAnalysisService.findLowestPrices(...);
    
    // Assert
    assertEquals(2, lowestPrices.size());
    assertEquals(new BigDecimal("200.00"), lowestPrices.get(0).getPrice());
}

@AfterEach
void tearDown() {
    hotelPriceRepository.deleteAll();
}
```
**Test isolation**: Use H2 in-memory database (`@TestPropertySource`) to avoid polluting SQLite production DB.

### Python-Java Database Bridge
Python utilities handle schema migrations and bulk operations that are cumbersome in JPA:
- **migrate_db.py**: Ensures `id INTEGER PRIMARY KEY AUTOINCREMENT` (SQLite quirk)
- **smart_insert.py**: Implements "INSERT or UPDATE on conflict" logic for re-scraping same dates
- **inspect_db.py**: Human-readable database inspection with `PRAGMA table_info` and SELECT queries

**When to use Python vs Java**:
- Java (JPA) → Transactional app logic, CRUD operations, query by business logic
- Python → Schema changes, bulk data manipulation, database diagnostics

## Integration Points & External Dependencies

### Booking.com Web Scraping
- **Target URLs**: `https://www.booking.com/searchresults.html?ss={hotel}%20{city}&checkin={date}&checkout={date}`
- **Critical selectors**: `[data-testid='property-card']`, `[data-testid='title']`, `[data-testid='price-and-discounted-price']`
- **Fragility warning**: Booking.com UI changes break selectors monthly — use browser DevTools (F12) to inspect current DOM structure
- **Rate limiting**: Single-threaded scraping with implicit delays (page load waits); add explicit `Thread.sleep()` if scaling

### SQLite Database Configuration
**application.properties** (production config):
```properties
spring.datasource.url=jdbc:sqlite:hotel_pricing.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
```
**Gotcha**: SQLite uses file-level locking — only one writer at a time. For concurrent scraping, use PostgreSQL/MySQL.

### ChromeDriver Requirements
- **Installation**: Chrome/Chromium must be installed at default location (`C:\Program Files\Google\Chrome\Application\chrome.exe` on Windows)
- **Version compatibility**: Maven manages ChromeDriver via Selenium dependency; ensure Chrome browser is up-to-date
- **No explicit path needed**: ChromeDriver auto-detected via Selenium WebDriverManager or system PATH
- **Headless mode**: Add `options.addArguments("--headless")` for background execution (used in `HotelScraperService`)

## Developer Workflow Notes

### Running Multi-City Price Analysis
The canonical use case is scraping **Ritz-Carlton** across **5 cities** for **Nov 15, 2025 - May 1, 2026**:
```bash
curl -X POST http://localhost:8080/api/hotels/scrape-multi-city
```
This triggers sequential scraping of Las Vegas, NYC, Miami, Paris, Los Angeles with automatic database persistence.

### Debugging Selenium Failures
1. **Remove headless mode**: Comment out `options.addArguments("--headless")` to see browser UI
2. **Add pause for inspection**: Insert `System.in.read();` after navigation
3. **Check selector validity**: Use browser DevTools → Elements tab → Ctrl+F to test CSS selectors
4. **Verify ChromeDriver**: Run `chromedriver --version` in terminal

### Modifying Date Ranges
Date ranges are inclusive and scrape **every day** in the range by default:
```java
LocalDate startDate = LocalDate.of(2025, 11, 15);
LocalDate endDate = LocalDate.of(2026, 5, 1);
while (!currentDate.isAfter(endDate)) {
    // Scrape logic...
    currentDate = currentDate.plusDays(1); // Daily increments (168 days for 11/15-5/1)
}
```
**Performance consideration**: Daily scraping is intensive (168 requests for 6-month range). For testing, modify to `plusDays(7)` for weekly sampling, but production uses daily.

### Test Execution Strategy
Tests use **separate H2 database** to avoid SQLite file locking:
```java
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driverClassName=org.h2.Driver"
})
```
This allows `.\mvnw.cmd test` to run while Spring Boot app is active on SQLite.

## Project Structure Reference
```
HotelPricingProject/
├── src/main/java/
│   ├── Booking.java                              # Legacy standalone scraper (kept for direct execution)
│   └── com/example/hotelpricingproject/
│       ├── HotelPricingProjectApplication.java   # Spring Boot main class
│       ├── entity/HotelPrice.java                # JPA entity with auto-timestamp
│       ├── repository/HotelPriceRepository.java  # Spring Data JPA (custom query methods)
│       ├── service/
│       │   ├── HotelScraperService.java          # Selenium scraping logic + anti-detection
│       │   └── HotelAnalysisService.java         # Query/reporting logic (lowest prices, reports)
│       └── controller/HotelController.java       # REST endpoints (5 routes)
├── src/test/java/
│   └── com/example/hotelpricingproject/
│       ├── entity/HotelPriceTest.java            # 4 entity tests (AAA style)
│       └── service/HotelAnalysisServiceTest.java # 5 service tests (H2 in-memory)
├── *.py files (migrate_db, smart_insert, etc.)   # Python database utilities
├── hotel_pricing.db                              # SQLite database (auto-created by Hibernate)
└── pom.xml                                       # Maven dependencies (Selenium, SQLite, H2)
```

## Logging & Error Handling
**Console logging pattern**: Services use `System.out.println()` for status updates:
```java
System.out.println("✓ Scraped: " + hotelName + " - $" + price);
System.out.println("✗ Error scraping " + hotelName + ": " + e.getMessage());
```
**Success indicators**: ✓, ✅ for successful operations  
**Failure indicators**: ✗, ❌ for errors  
**For production**: Replace `System.out` with SLF4J logger (`@Slf4j` + `log.info()`/`log.error()`) and configure Logback for file rotation.

## Known Limitations & Production Considerations
- **Single-threaded scraping**: Sequential city processing; daily scraping of 168 days takes ~2-3 hours per city
- **SQLite file locking**: Only one writer; use PostgreSQL for multi-user scenarios
- **Selector brittleness**: Booking.com UI changes require selector updates in `HotelScraperService`
- **No retry logic**: Network failures abort entire scrape; add exponential backoff for production
- **No caching**: Every analysis query hits database; consider Redis for frequently accessed lowest-prices
- **Console logging only**: Replace `System.out.println()` with proper logging framework (SLF4J + Logback) for production deployments
