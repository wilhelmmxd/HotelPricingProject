# AI Agent Instructions for HotelPricingProject

## Project Overview
**HotelPricingProject** is a Spring Boot 3.5.6 application (Java 17) that performs hotel price extraction from Booking.com using Selenium WebDriver. The architecture combines a Spring Boot REST application framework with a standalone web scraping module.

### Key Architecture Decisions
- **Spring Boot 3.x**: Modern framework with data-jdbc and JPA support; minimal configuration for this phase
- **Selenium-based scraping**: The `Booking.java` class uses Selenium for automated hotel searches on Booking.com
- **Web driver automation**: Heavy use of ChromeDriver with anti-detection measures (user-agent spoofing, webdriver property masking)
- **Single package structure**: Core logic in `com.example.hotelpricingproject` with Booking scraper at root level

## Critical Workflows

### Building & Running
```bash
# Build the project (uses Maven wrapper)
.\mvnw.cmd clean package

# Run tests
.\mvnw.cmd test

# Run the application
.\mvnw.cmd spring-boot:run
```

### Key Dependencies
- **Spring Boot Starters**: `spring-boot-starter-data-jdbc`, `spring-boot-starter-data-jpa`, `spring-boot-starter-web-services`
- **MySQL**: `mysql-connector-j` (runtime scope) for database operations
- **Selenium WebDriver**: Manages ChromeDriver for browser automation (check `Booking.java` imports)
- **Testing**: JUnit 5 via `spring-boot-starter-test`

## Important Patterns & Conventions

### Selenium Anti-Detection Pattern
The `Booking.java` scraper implements multiple techniques to avoid bot detection:
- **Chrome options masking**: Disables automation flags, excludes automation extensions
- **User-agent spoofing**: Sets a legitimate Windows Chrome user-agent
- **JavaScript property override**: Masks `navigator.webdriver` property
- **Visual presentation**: Uses `--start-maximized` for consistent rendering

**Example from `getPriceOfRoom()` and `main()` methods**: Always include these setup options when creating new ChromeDriver instances.

### Hotel Data Extraction Workflow
1. **Navigation**: Build search URL using `buildBookingUrl()` with hotel name, check-in/check-out dates
2. **Wait & Handle UI**: Use WebDriverWait (20s timeout) for element presence; handle cookies and popups
3. **Scroll loading**: Execute JavaScript to scroll page and load lazy-loaded hotel cards
4. **CSS selector fallback**: Implement multiple selector fallbacks (e.g., in `findHotel()` using `[data-testid='property-card']`)
5. **Data extraction**: Extract hotel name, price, rating, address using targeted CSS selectors with try-catch blocks

**File**: `Booking.java` lines 40-250 for main scraping logic.

### Date Handling Convention
Hotel searches use relative dates (tomorrow check-in, next day check-out) via:
```java
LocalDate checkIn = LocalDate.now().plusDays(1);
LocalDate checkOut = checkIn.plusDays(1);
```
This ensures tests remain consistent regardless of execution date.

### Error Handling Approach
- **Graceful degradation**: Each extraction step (name, price, rating, address) has independent try-catch blocks
- **Fallback selectors**: Multiple CSS selector attempts before logging failure
- **User feedback**: Console logging indicates success (✓, ✅) and failure (✗) states for debugging

### Package Organization
- **Root-level Booking.java**: Standalone Selenium utility; not integrated into Spring packages yet
- **Spring package** (`com.example.hotelpricingproject`): Contains `HotelPricingProjectApplication.java` boot class only
- **No REST endpoints yet**: Application currently runs Spring Boot container but doesn't expose endpoints

## Integration Points & External Dependencies

### Booking.com Web Scraping
- **Target URLs**: Dynamically built `https://www.booking.com/searchresults.html` with query parameters
- **Selectors depend on**: Booking.com HTML structure (data-testid attributes, CSS classes); **fragile to UI changes**
- **Rate limiting**: No explicit throttling implemented; add delays between requests if scaling

### Database Configuration
- **Properties file**: `src/main/resources/application.properties` (currently minimal, only `spring.application.name`)
- **Next step**: Database connection details needed in properties when integrating hotel data persistence
- **JPA/JDBC**: Already included; models should follow Spring Data conventions

### Browser Resources
- **ChromeDriver**: Requires Chrome/Chromium installation on execution environment
- **Headless option**: Used in `getPriceOfRoom()` for background execution; removed in `main()` for UI inspection

## Developer Workflow Notes

1. **Testing the scraper**: Run `Booking.java` main method directly; includes `System.in.read()` to pause browser for manual inspection
2. **Adding new hotels**: Change `hotelName` variable (lines 44-45) and re-run
3. **Debugging selectors**: Check browser DevTools against actual Booking.com DOM; selectors frequently break with site updates
4. **Spring integration**: Route scraper calls through REST endpoints in future controllers; currently standalone

## Known Limitations & Future Work
- No persistent storage yet (JPA configured but unused)
- Single-threaded scraping; no concurrent hotel searches
- Booking.com selectors are fragile; maintenance required for site changes
- No error recovery; failed requests terminate without retry logic
