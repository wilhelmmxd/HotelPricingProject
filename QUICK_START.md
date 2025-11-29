# Quick Start Guide - Hotel Pricing Project

## 5-Minute Setup

### 1. Build & Run Tests
```bash
cd C:\Users\brend\HotelPricingProject
.\mvnw.cmd clean package
# ✅ All 10 tests pass - BUILD SUCCESS
```

### 2. Start the Application
```bash
.\mvnw.cmd spring-boot:run
# Application starts at http://localhost:8080
```

### 3. Run a Scraping Task (Example)

**Option A: Using cURL**
```bash
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=Las%20Vegas&startDate=2025-11-27&endDate=2025-12-05"
```

**Option B: Using PowerShell**
```powershell
$uri = "http://localhost:8080/api/hotels/scrape"
$params = @{
    hotelName = "Ritz-Carlton"
    city = "Las Vegas"
    startDate = "2025-11-27"
    endDate = "2025-12-05"
}
Invoke-WebRequest -Uri $uri -Method POST -Body $params
```

### 4. Query Results

**Get 10 lowest prices:**
```bash
curl "http://localhost:8080/api/hotels/lowest-prices?hotelName=Ritz-Carlton&city=Las%20Vegas"
```

**Response:**
```json
[
  {
    "id": 1,
    "hotelName": "Ritz-Carlton",
    "city": "Las Vegas",
    "checkInDate": "2025-11-27",
    "checkOutDate": "2025-11-28",
    "price": 200.00,
    "rating": "4.5",
    "address": "123 Main St, Las Vegas",
    "scrapedDate": "2025-11-26"
  },
  ...
]
```

## Complete Workflow - Multi-City Analysis

### Setup: 5 Cities Analysis
```bash
# Cities to analyze:
# 1. Las Vegas
# 2. New York City
# 3. Miami
# 4. Paris
# 5. Los Angeles

# Date range: Nov 15, 2025 - May 1, 2026
```

### Step 1: Scrape Each City

```bash
# Las Vegas
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=Las%20Vegas&startDate=2025-11-15&endDate=2026-05-01"

# New York City  
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=New%20York%20City&startDate=2025-11-15&endDate=2026-05-01"

# Miami
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=Miami&startDate=2025-11-15&endDate=2026-05-01"

# Paris
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=Paris&startDate=2025-11-15&endDate=2026-05-01"

# Los Angeles
curl -X POST "http://localhost:8080/api/hotels/scrape?hotelName=Ritz-Carlton&city=Los%20Angeles&startDate=2025-11-15&endDate=2026-05-01"
```

### Step 2: Generate Analysis Report

```bash
curl "http://localhost:8080/api/hotels/analysis-report?hotelName=Ritz-Carlton&cities=Las%20Vegas&cities=New%20York%20City&cities=Miami&cities=Paris&cities=Los%20Angeles"
```

**Output:**
```
=== Hotel Price Analysis Report ===
Hotel: Ritz-Carlton

City: Las Vegas
  Top 10 Lowest Prices:
  1. Check-in: 2025-11-27, Price: $180, Rating: 4.5
  2. Check-in: 2025-12-03, Price: $195, Rating: 4.5
  ... (8 more)

City: New York City
  Top 10 Lowest Prices:
  1. Check-in: 2025-12-15, Price: $250, Rating: 4.6
  ... (9 more)

[Additional cities...]
```

### Step 3: Export to Word/CSV

**Via API (JSON response):**
```bash
curl "http://localhost:8080/api/hotels/lowest-prices?hotelName=Ritz-Carlton&city=Las%20Vegas" > analysis.json
```

**Then convert to Word/Excel using Python:**
```python
import json
import csv

# Load JSON
with open('analysis.json', 'r') as f:
    data = json.load(f)

# Export to CSV
keys = data[0].keys()
with open('hotel_prices.csv', 'w', newline='') as f:
    writer = csv.DictWriter(f, fieldnames=keys)
    writer.writeheader()
    writer.writerows(data)

print("✅ Exported to hotel_prices.csv")
```

## Direct Java Execution (Standalone Scraper)

### IMPORTANT: Shell vs Python
All commands shown below must be run in a PowerShell terminal, NOT inside the Python REPL (`>>>`). If you see `>>>`, type `exit()` or press `Ctrl+Z` then Enter to return to PowerShell.

### Run `Booking` with Maven Exec Plugin
We added an exec plugin so dependencies (Selenium, SQLite) are on the classpath automatically.
```powershell
cd C:\Users\brend\HotelPricingProject
.\mvnw.cmd clean compile
.\mvnw.cmd exec:java@run-booking
```

### Alternative: Manual classpath (needs copied dependencies)
```powershell
.\mvnw.cmd dependency:copy-dependencies -DincludeScope=runtime
$depJars = (Get-ChildItem -Path target\dependency -Filter *.jar | ForEach-Object { $_.FullName }) -join ';'
java -cp "target\classes;src\main\java;$depJars" Booking
```

### Initialize / Inspect SQLite
```powershell
python .\init_db.py       # Creates hotel_prices table if missing
python .\inspect_db.py    # Shows tables and sample rows
```

**Note:** Press Enter only if the program prompts; otherwise closing the Chrome window terminates the run.

## Running Unit Tests

### All tests:
```bash
.\mvnw.cmd test
```

### Specific test class:
```bash
.\mvnw.cmd test -Dtest=HotelPriceTest
.\mvnw.cmd test -Dtest=HotelAnalysisServiceTest
```

### Specific test method:
```bash
.\mvnw.cmd test -Dtest=HotelPriceTest#testHotelPriceCreation
```

### With coverage report:
```bash
.\mvnw.cmd jacoco:report test
# View: target/site/jacoco/index.html
```

## Database Inspection

### Direct SQLite Access (PowerShell)

```bash
# Install sqlite3 command-line tool (if needed)
# Or download: https://www.sqlite.org/download.html

# View database
sqlite3 hotel_pricing.db

# Query prices
SELECT * FROM hotel_prices WHERE hotel_name = 'Ritz-Carlton' ORDER BY price ASC LIMIT 10;

# Count by city
SELECT city, COUNT(*) as count FROM hotel_prices GROUP BY city;

# Lowest prices per city
SELECT city, MIN(price) as lowest_price FROM hotel_prices GROUP BY city;

# Export to CSV
.mode csv
.output prices_export.csv
SELECT * FROM hotel_prices;
.quit
```

## Project File Structure

```
HotelPricingProject/
├── pom.xml                          ← Maven configuration (dependencies)
├── mvnw / mvnw.cmd                  ← Maven wrapper
├── hotel_pricing.db                 ← SQLite database (auto-created)
├── PROJECT_DOCUMENTATION.md         ← Full project documentation
├── QUICK_START.md                   ← This file
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── Booking.java         ← Standalone scraper
    │   │   └── com/example/hotelpricingproject/
    │   │       ├── HotelPricingProjectApplication.java
    │   │       ├── controller/HotelController.java
    │   │       ├── service/
    │   │       │   ├── HotelScraperService.java
    │   │       │   └── HotelAnalysisService.java
    │   │       ├── repository/HotelPriceRepository.java
    │   │       └── entity/HotelPrice.java
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/
            ├── entity/HotelPriceTest.java
            └── service/HotelAnalysisServiceTest.java
```

  ## Python Utilities Scope (Trimmed)
  - Retained: `init_db.py` (initialize/reset schema), `inspect_db.py` (diagnose schema/data).
  - Removed: bulk data generators and upsert/migration helpers to keep the project lean.
  - Rationale: Java/JPA handles schema and persistence for this one-time project; Python remains only for light DB inspection.

## Requirements Met

### ✅ Project Objectives

| Objective | Status | Details |
|-----------|--------|---------|
| Reliable execution without errors | ✅ DONE | All tests pass, anti-detection measures, error handling |
| SQLite database storage | ✅ DONE | Full JPA integration, auto schema generation |
| Find 10 lowest prices | ✅ DONE | Repository query, analysis service, API endpoint |
| Date range: Nov 15 - May 1 | ✅ DONE | 168-day analysis window implemented |
| Multi-city support | ✅ DONE | Service layer handles 5+ cities |
| AAA testing style | ✅ DONE | All 10 tests follow Arrange-Act-Assert |
| Annotations (@BeforeEach, @After Each, etc) | ✅ DONE | Comprehensive test lifecycle management |
| DRY principle | ✅ DONE | Shared service methods, reusable utilities |

## Common Issues & Solutions

### Issue: "Chrome driver not found"
**Solution:**
```bash
# Check Chrome installation
"C:\Program Files\Google\Chrome\Application\chrome.exe" --version

# If not installed, download from: https://www.google.com/chrome/
```

### Issue: "Port 8080 already in use"
**Solution:**
```bash
# Change port in application.properties:
server.port=8081
```

### Issue: Commands produce SyntaxError inside Python
**Cause:** Commands entered while in Python REPL (`>>>`).
**Solution:** Exit Python (`exit()`), then run commands in PowerShell.

### Issue: "SQLite database locked"
**Solution (PowerShell):**
```powershell
Remove-Item hotel_pricing.db -ErrorAction SilentlyContinue
python .\init_db.py
```

### Issue: Tests fail with "NULL not allowed for column ID"
**Solution:** Already fixed; ensure you are not using legacy temp tables. Run `python .\inspect_db.py` and confirm table is `hotel_prices`.

### Issue: JPQL query fails with LIMIT
**Solution:** Use `Pageable` in repository method; already patched.

## Next Steps

1. **Add more cities** - Modify city list in analysis workflow
2. **Export reports** - Use CSV/JSON output for Word documents
3. **Schedule scraping** - Add Spring @Scheduled tasks for automatic runs
4. **Monitor prices** - Implement price change alerts
5. **API authentication** - Add JWT security layer
6. **Performance** - Implement parallel city scraping with ExecutorService

## Support & Debugging

**Enable detailed logging:**
```properties
# In application.properties
logging.level.com.example.hotelpricingproject=DEBUG
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

**Check application logs:**
```bash
.\mvnw.cmd spring-boot:run | tee app.log
```

**Browser inspection during scraping:**
The Booking.java standalone mode displays Chrome browser - manually inspect DOM for selector changes.

---

**Happy analyzing! 🏨📊**
