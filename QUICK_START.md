# Quick Start Guide - Hotel Data Automation

## Prerequisites Check

Before running the project, ensure you have:

- [ ] **Java 11 or higher** installed
  ```powershell
  java -version
  ```
  Should show version 11 or higher

- [ ] **Maven** installed
  ```powershell
  mvn -version
  ```
  If Maven is not installed, download from: https://maven.apache.org/download.cgi

## Installation Steps

### 1. Install Maven (Windows)

If Maven is not installed:

1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract to `C:\Program Files\Apache\maven`
3. Add to PATH:
   - Open System Properties → Environment Variables
   - Add to PATH: `C:\Program Files\Apache\maven\bin`
4. Verify: `mvn -version`

### 2. Navigate to Project Directory

```powershell
cd c:\Users\brend\scrapper
```

### 3. Build the Project

```powershell
mvn clean install
```

This will:
- Download all dependencies (first time only)
- Compile all source files
- Run all tests
- Create the build artifacts

**Expected output**: `BUILD SUCCESS`

## Running the Application

### Option 1: Using Maven Exec Plugin

```powershell
mvn exec:java -Dexec.mainClass="App"
```

### Option 2: Direct Java Execution

```powershell
# First compile
mvn compile

# Then run
java -cp "target/classes;%USERPROFILE%\.m2\repository\org\xerial\sqlite-jdbc\3.44.1.0\sqlite-jdbc-3.44.1.0.jar;%USERPROFILE%\.m2\repository\org\apache\poi\poi\5.2.5\poi-5.2.5.jar;%USERPROFILE%\.m2\repository\org\apache\poi\poi-ooxml\5.2.5\poi-ooxml-5.2.5.jar" App
```

### Option 3: Package and Run as JAR

```powershell
# Package the application
mvn package

# Run the JAR
java -jar target/hotel-data-automation-1.0-SNAPSHOT.jar
```

## Running Tests

```powershell
mvn test
```

Expected output:
```
Tests run: 51, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

## What You'll See

### Console Output

```
=== Hotel Data Automation System ===
Starting data collection and analysis...

[1/5] Initializing database...
Database initialized successfully.

[2/5] Scraping hotel price data...
Scraping: Four Seasons in Las Vegas
Scraping: Four Seasons in New York City
...
Scraped 5475 price records.

[3/5] Storing data in database...
Batch insert completed: 5475 records inserted.
Total records in database: 5475

[4/5] Analyzing price data...
Analyzing 25 hotel-city combinations...
Found 10 prices for Four Seasons - Las Vegas
...

[5/5] Generating Word document report...
Report generated successfully: Hotel_Price_Analysis_Report.docx

=== Process Complete ===
Report generated: Hotel_Price_Analysis_Report.docx
Database file: hotels.db
```

### Generated Files

1. **`hotels.db`** - SQLite database with all price data
   - Can be viewed with DB Browser for SQLite
   - Download from: https://sqlitebrowser.org/

2. **`Hotel_Price_Analysis_Report.docx`** - Professional Word report
   - Open with Microsoft Word
   - Contains tables with 10 lowest prices for each hotel-city

## Viewing the Results

### View Database

```powershell
# Install DB Browser for SQLite (optional)
# Then open hotels.db

# Or use SQLite command line:
sqlite3 hotels.db "SELECT * FROM hotel_prices LIMIT 10;"
```

### View Report

Simply open `Hotel_Price_Analysis_Report.docx` with Microsoft Word.

## Common Issues and Solutions

### Issue: "mvn is not recognized"
**Solution**: Maven is not installed or not in PATH. Follow Step 1 above.

### Issue: "Java version mismatch"
**Solution**: Update `pom.xml` to match your Java version:
```xml
<maven.compiler.source>YOUR_VERSION</maven.compiler.source>
<maven.compiler.target>YOUR_VERSION</maven.compiler.target>
```

### Issue: "Cannot find or load main class App"
**Solution**: Make sure you're in the project directory and have compiled:
```powershell
mvn clean compile
```

### Issue: Dependencies not downloading
**Solution**: 
```powershell
# Clear Maven cache and rebuild
mvn clean
mvn dependency:purge-local-repository
mvn install
```

### Issue: ChromeDriver error
**Solution**: The project uses WebDriverManager which handles ChromeDriver automatically. If issues persist, ensure Chrome browser is installed.

## Project Structure Quick Reference

```
scrapper/
├── pom.xml                              # Maven configuration
├── README.md                            # Full documentation
├── IMPLEMENTATION_SUMMARY.md            # Implementation details
├── QUICK_START.md                       # This file
│
├── src/
│   ├── App.java                         # ← START HERE (main)
│   │
│   ├── main/java/com/hotel/automation/
│   │   ├── DatabaseManager.java         # Database operations
│   │   ├── HotelPriceRecord.java        # Data model
│   │   ├── HotelScraper.java            # Web scraping
│   │   ├── DataAnalyzer.java            # Analysis logic
│   │   └── ReportGenerator.java         # Word report
│   │
│   └── test/java/com/hotel/automation/
│       ├── DatabaseManagerTest.java     # Tests
│       ├── HotelPriceRecordTest.java
│       ├── HotelScraperTest.java
│       ├── DataAnalyzerTest.java
│       └── ReportGeneratorTest.java
│
└── target/                              # Build output
    ├── classes/                         # Compiled .class files
    └── test-classes/                    # Compiled test files
```

## Testing Individual Components

### Test Database Only
```powershell
mvn test -Dtest=DatabaseManagerTest
```

### Test Scraper Only
```powershell
mvn test -Dtest=HotelScraperTest
```

### Test Analyzer Only
```powershell
mvn test -Dtest=DataAnalyzerTest
```

### Test Report Generator Only
```powershell
mvn test -Dtest=ReportGeneratorTest
```

## Cleaning Up

### Remove Generated Files
```powershell
# Remove database
Remove-Item hotels.db

# Remove report
Remove-Item Hotel_Price_Analysis_Report.docx

# Remove build artifacts
mvn clean
```

## Next Steps

1. ✅ Build the project: `mvn clean install`
2. ✅ Run all tests: `mvn test`
3. ✅ Run the application: `mvn exec:java -Dexec.mainClass="App"`
4. ✅ Open the generated report in Word
5. ✅ Review the code and documentation

## Need More Help?

- 📖 See `README.md` for complete documentation
- 📋 See `IMPLEMENTATION_SUMMARY.md` for implementation details
- 💬 Each Java file has extensive JavaDoc comments
- 🧪 Test files demonstrate usage examples

**Happy Coding! 🚀**
