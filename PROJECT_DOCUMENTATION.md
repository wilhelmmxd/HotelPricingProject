# Hotel Pricing Project - Full Implementation Guide

## Project Overview
This Spring Boot 3.5.6 application (Java 17) performs comprehensive hotel price analysis from Booking.com using Selenium WebDriver. The system automatically scrapes prices across multiple cities and date ranges, storing data in SQLite for analysis.

## Features Implemented

### ✅ Complete Functionality
1. **Hotel Data Storage** - SQLite database with full JPA support
2. **Multi-City Support** - Scrape across 5+ cities simultaneously
3. **Date Range Analysis** - Nov 15, 2025 - May 1, 2026
4. **Lowest Price Detection** - Finds 10 lowest-priced dates per hotel/city
5. **REST API Endpoints** - Full CRUD and analysis operations
6. **Comprehensive Testing** - AAA-style unit tests with @BeforeEach/@AfterEach
7. **Robust Web Scraping** - Anti-detection measures, error handling, lazy-loading support

## Project Architecture

```
HotelPricingProject/
├── src/main/java/
│   ├── Booking.java                    # Standalone Selenium scraper (can be run directly)
│   └── com/example/hotelpricingproject/
│       ├── HotelPricingProjectApplication.java  # Spring Boot main class
│       ├── entity/
│       │   └── HotelPrice.java         # JPA entity for price data
│       ├── repository/
│       │   └── HotelPriceRepository.java # Spring Data JPA repository
│       ├── service/
│       │   ├── HotelScraperService.java  # Selenium web scraping logic
│       │   └── HotelAnalysisService.java # Price analysis & reporting
│       └── controller/
│           └── HotelController.java    # REST API endpoints
├── src/test/java/
│   ├── entity/
│   │   └── HotelPriceTest.java         # Entity unit tests (AAA style)
│   └── service/
│       └── HotelAnalysisServiceTest.java # Service unit tests
├── src/main/resources/
│   └── application.properties          # SQLite configuration
└── pom.xml                             # Maven dependencies
└── (optional) Python helpers            # Minimal DB utilities: init_db.py, inspect_db.py
```

## Key Components

### 1. HotelPrice Entity (`entity/HotelPrice.java`)
Represents a single hotel price record with:
- `hotelName`, `city`, `checkInDate`, `checkOutDate`
- `price` (BigDecimal), `rating`, `address`
- `scrapedDate` (automatic timestamp)
- Auto-incrementing `id` primary key

### 2. HotelScraperService (`service/HotelScraperService.java`)
Core scraping engine with:
- **Anti-Detection**: User-agent spoofing, webdriver masking, cookie handling
- **Robust Extraction**: Multiple CSS selector fallbacks for resilience
- **Error Handling**: Graceful degradation for each extraction step
- **Lazy Loading**: JavaScript scrolling to load dynamic content
- **Database Integration**: Auto-saves prices to SQLite

**Key Methods:**
```java
scrapeHotelPrices(hotelName, city, startDate, endDate)
extractHotelData(driver, hotelName, city, checkIn, checkOut)
extractPrice(card)
buildBookingUrl(hotelName, city, checkIn, checkOut)
```

### 3. HotelAnalysisService (`service/HotelAnalysisService.java`)
Analysis and reporting:
- Find 10 lowest prices per hotel/city
- Filter by date ranges
- Generate comprehensive reports
- Query historical data

### 4. HotelController (`controller/HotelController.java`)
REST API endpoints:

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/hotels/scrape` | POST | Trigger scraping for hotel/city/dates |
| `/api/hotels/lowest-prices` | GET | Get 10 lowest prices |
| `/api/hotels/lowest-prices-by-range` | GET | Filter lowest prices by date range |
| `/api/hotels/prices` | GET | Get all prices for hotel/city |
| `/api/hotels/analysis-report` | GET | Generate summary report |

### 5. Database Configuration
SQLite database with Hibernate ORM:
```properties
spring.datasource.url=jdbc:sqlite:hotel_pricing.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.jpa.database-platform=org.hibernate.community.dialect.SQLiteDialect
spring.jpa.hibernate.ddl-auto=update
```

### 6. Minimal Python Utilities (Scope)
- `init_db.py`: Initialize or reset the `hotel_prices` table if needed.
- `inspect_db.py`: Inspect schema (`PRAGMA table_info`) and sample rows for diagnostics.
- Removed utilities (e.g., bulk population, smart upsert) to keep the project focused; Java/JPA handles persistence for this one-time deliverable.

## Running the Application

### Build the Project
```bash
cd C:\Users\brend\HotelPricingProject
.\mvnw.cmd clean package
```

### Run Tests
```bash
.\mvnw.cmd test
```

### Run the Application
```bash
.\mvnw.cmd spring-boot:run
```

### Direct Scraper Execution
```bash
java -cp "target\classes;target\dependency\*" Booking
```

## Testing

### Test Framework
- **Framework**: JUnit 5 (Jupiter)
- **Style**: AAA (Arrange-Act-Assert)
- **Annotations**: @BeforeEach, @AfterEach, @Test, @DisplayName
- **Database**: H2 in-memory for integration tests

### Test Files

#### 1. HotelPriceTest.java (4 tests)
- ✅ Entity creation with all fields
- ✅ Setters and getters
- ✅ Automatic scraped date setting
- ✅ String representation

#### 2. HotelAnalysisServiceTest.java (5 tests)
- ✅ Find lowest prices by hotel/city
- ✅ Get prices for hotel in city
- ✅ Handle empty results gracefully
- ✅ Generate analysis reports
- ✅ Filter by date range

### Test Results
```
Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## Usage Examples

### API Usage

**1. Scrape hotel prices:**
```bash
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=Las%20Vegas&startDate=2025-11-27&endDate=2025-12-27"
```

**2. Get lowest prices:**
```bash
curl "http://localhost:8080/api/hotels/lowest-prices?hotelName=Ritz-Carlton&city=Las%20Vegas"
```

**3. Generate analysis report:**
```bash
curl "http://localhost:8080/api/hotels/analysis-report?hotelName=Ritz-Carlton&cities=Las%20Vegas&cities=New%20York%20City&cities=Miami"
```

### Programmatic Usage

```java
// Inject services
@Autowired
private HotelScraperService scraperService;

@Autowired
private HotelAnalysisService analysisService;

// Scrape prices
LocalDate start = LocalDate.of(2025, 11, 15);
LocalDate end = LocalDate.of(2026, 5, 1);
List<HotelPrice> prices = scraperService.scrapeHotelPrices(
    "Ritz-Carlton", "Las Vegas", start, end
);

// Analyze data
List<HotelPrice> lowestPrices = analysisService.findLowestPrices(
    "Ritz-Carlton", "Las Vegas"
);
```

## Dependencies

### Core
- Spring Boot 3.5.6
- Spring Data JPA
- Spring Data JDBC
- Spring Boot Web Services

### Database
- SQLite JDBC
- Hibernate ORM (with SQLite dialect)
- Hibernate Community Dialects

### Web Scraping
- Selenium Java (4.x)
- Chrome WebDriver

### Testing
- JUnit 5 (Jupiter)
- Spring Boot Test
- H2 Database (test-only)

### Build
- Maven 3.8.9+

## Best Practices Followed

### Architecture
- **Separation of Concerns**: Service, controller, and repository layers
- **DRY Principle**: Reusable service methods, shared utility functions
- **Immutability**: Entity uses proper getters/setters

### Testing
- **AAA Pattern**: Clear Arrange-Act-Assert structure
- **Test Isolation**: @BeforeEach/@AfterEach for setup/teardown
- **Integration Tests**: Full Spring context with H2 database
- **Descriptive Names**: @DisplayName annotations for clarity

### Selenium
- **Anti-Detection**: Comprehensive bot-prevention techniques
- **Error Resilience**: Multiple fallback selectors
- **Resource Cleanup**: Proper WebDriver teardown
- **Scalability**: Service-oriented design for multi-threaded scraping

## Database Schema

```sql
CREATE TABLE hotel_prices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    hotel_name VARCHAR(255) NOT NULL,
    city VARCHAR(255) NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    rating VARCHAR(255),
    address TEXT,
    scraped_date DATE NOT NULL
);

CREATE INDEX idx_hotel_city ON hotel_prices(hotel_name, city);
CREATE INDEX idx_check_in_date ON hotel_prices(check_in_date);
CREATE INDEX idx_price ON hotel_prices(price);
```

## Future Enhancements

1. **Concurrency**: Thread pool for multi-city simultaneous scraping
2. **Caching**: Redis cache for frequently queried analyses
3. **Notifications**: Email alerts for price drops
4. **UI Dashboard**: React/Angular frontend for visualization
5. **Scheduling**: Cron jobs for automatic daily scraping
6. **Export**: PDF/Excel report generation
7. **ML Predictions**: Price trend forecasting

## Troubleshooting

### Chrome Driver Issues
- Ensure Chrome/Chromium is installed
- Update ChromeDriver to match Chrome version
- Check `C:\Program Files\Google\Chrome\Application\`

### Booking.com Selector Changes
- Selectors may break if Booking.com updates UI
- Use browser DevTools (F12) to inspect current selectors
- Update CSS selectors in `extractHotelData()` method

### Database Locks
- SQLite uses file-level locking
- Ensure only one instance runs at a time
- Delete `hotel_pricing.db` to reset database

## Contact & Support
For issues or questions, review the copilot-instructions.md or examine test cases for usage examples.
