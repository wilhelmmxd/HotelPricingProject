# Hotel Data Automation System

## Project Overview
This project is a comprehensive Java application that automates the collection, storage, analysis, and reporting of hotel room prices from luxury hotel chains across major cities.

## Features

### 1. **Web Scraping with Selenium**
- Automatically scrapes room prices for luxury hotel chain:
  - Ritz-Carlton
- Searches across 5 major cities:
  - Las Vegas
  - New York City
  - Miami
  - Paris
  - Los Angeles
- Collects price data starting from May 1st of the current year

### 2. **SQLite Database Storage**
- Stores all scraped data in a SQLite database (`hotels.db`)
- Table structure:
  - `id` - Auto-incrementing primary key
  - `hotel_name` - Name of the hotel
  - `city` - City location
  - `checkin_date` - Check-in date (YYYY-MM-DD format)
  - `price` - Room price

### 3. **Data Analysis**
- Analyzes collected data to find the 10 lowest-priced dates for each hotel in each city
- Provides statistical analysis (min, max, average prices)
- Supports querying by hotel-city combination

### 4. **Report Generation**
- Creates professional Microsoft Word reports using Apache POI
- Reports include:
  - Summary of analyzed data
  - Detailed tables for each hotel-city combination
  - Top 10 lowest-priced dates for each combination
  - Formatted with colors, headers, and professional styling

## Project Structure

```
src/
├── App.java                          # Main application entry point
└── main/java/com/hotel/automation/
    ├── DatabaseManager.java          # Database operations (JDBC)
    ├── HotelPriceRecord.java         # Data model class
    ├── HotelScraper.java             # Web scraping with Selenium
    ├── DataAnalyzer.java             # Data analysis logic
    └── ReportGenerator.java          # Word document generation

src/test/java/com/hotel/automation/
├── DatabaseManagerTest.java          # Unit tests for DatabaseManager
├── HotelPriceRecordTest.java        # Unit tests for HotelPriceRecord
├── HotelScraperTest.java            # Unit tests for HotelScraper
├── DataAnalyzerTest.java            # Unit tests for DataAnalyzer
└── ReportGeneratorTest.java         # Unit tests for ReportGenerator
```

## Dependencies

- **Selenium WebDriver 4.15.0** - Web automation
- **SQLite JDBC 3.44.1.0** - Database connectivity
- **Apache POI 5.2.5** - Word document generation
- **JUnit 5.10.1** - Unit testing
- **WebDriverManager 5.6.2** - Automatic driver management

## Technical Specifications

- **Java Version**: Java 21 LTS
- **Build Tool**: Maven 3.11.0
- **Test Framework**: JUnit 5 (Jupiter)
- **Database**: SQLite

## How to Run

### Prerequisites
- Java 21 LTS (latest Long-Term Support version)
- Maven 3.6 or higher

### Building the Project
```bash
mvn clean install
```

### Running the Application
```bash
mvn exec:java -Dexec.mainClass="App"
```

Or compile and run directly:
```bash
mvn compile
java -cp target/classes App
```

### Running Tests
```bash
mvn test
```

## Testing

The project includes comprehensive unit tests following the **Arrange-Act-Assert (AAA)** pattern with JUnit 5 annotations:

- `@BeforeAll` / `@AfterAll` - Setup/teardown for all tests in a class
- `@BeforeEach` / `@AfterEach` - Setup/teardown for each test
- `@Test` - Marks test methods
- `@DisplayName` - Provides descriptive test names

### Test Coverage
- **DatabaseManagerTest**: Tests database operations, CRUD operations, queries (10 tests)
- **HotelPriceRecordTest**: Tests data model class, equals/hashCode, getters/setters (12 tests)
- **HotelScraperTest**: Tests scraping logic, data validation (9 tests)
- **DataAnalyzerTest**: Tests data analysis, statistics calculation (10 tests)
- **ReportGeneratorTest**: Tests report generation, file creation (7 tests)

**Total: 48 tests - All passing ✅**

## Code Quality

### DRY Principle
The project follows the "Don't Repeat Yourself" principle:
- Reusable database connection methods
- Common scraping utilities
- Shared formatting functions
- Parameterized helper methods

### Design Patterns
- **DAO Pattern**: `DatabaseManager` handles all database operations
- **Model Class**: `HotelPriceRecord` represents domain data
- **Separation of Concerns**: Each class has a single, well-defined responsibility

## Output

After running the application, you'll find:
1. **hotels.db** - SQLite database with all scraped price data
2. **Hotel_Price_Analysis_Report.docx** - Professional Word document report

## Notes for Students

### Web Scraping Implementation
The current implementation uses **mock data generation** instead of actual web scraping. This is because:
- Real websites have anti-scraping measures
- Site structures change frequently
- Legal and ethical considerations

To implement real scraping:
1. Uncomment the actual scraping logic in `HotelScraper.java`
2. Update CSS selectors based on the target website's structure
3. Add proper error handling and rate limiting
4. Ensure compliance with the website's Terms of Service

### Extension Ideas
- Add GUI using JavaFX or Swing
- Implement price trend visualization
- Add email notifications for price drops
- Support for more hotel chains and cities
- Export to additional formats (PDF, Excel)

## License
This is an academic project for educational purposes.

## Author
Created for Hotel Data Automation class assignment.

