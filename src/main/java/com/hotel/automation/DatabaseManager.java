package com.hotel.automation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages all database operations for the Hotel Data Automation system.
 * Follows DRY principle with reusable methods for database interactions.
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:hotels.db";
    
    /**
     * Establishes and returns a connection to the SQLite database.
     * 
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }
    
    /**
     * Initializes the database by creating the hotel_prices table if it doesn't exist.
     */
    public void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS hotel_prices (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hotel_name TEXT NOT NULL, " +
                "city TEXT NOT NULL, " +
                "checkin_date TEXT NOT NULL, " +
                "price REAL NOT NULL" +
                ")";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Inserts a single hotel price record into the database.
     * 
     * @param hotelName Name of the hotel
     * @param city City where the hotel is located
     * @param checkinDate Check-in date in 'YYYY-MM-DD' format
     * @param price Room price
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertHotelPrice(String hotelName, String city, String checkinDate, double price) {
        String insertSQL = "INSERT INTO hotel_prices (hotel_name, city, checkin_date, price) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, hotelName);
            pstmt.setString(2, city);
            pstmt.setString(3, checkinDate);
            pstmt.setDouble(4, price);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error inserting hotel price: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Inserts multiple hotel price records in a batch operation for better performance.
     * 
     * @param priceRecords List of HotelPriceRecord objects to insert
     * @return Number of records successfully inserted
     */
    public int insertBatch(List<HotelPriceRecord> priceRecords) {
        String insertSQL = "INSERT INTO hotel_prices (hotel_name, city, checkin_date, price) VALUES (?, ?, ?, ?)";
        int insertedCount = 0;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            conn.setAutoCommit(false);
            
            for (HotelPriceRecord record : priceRecords) {
                pstmt.setString(1, record.getHotelName());
                pstmt.setString(2, record.getCity());
                pstmt.setString(3, record.getCheckinDate());
                pstmt.setDouble(4, record.getPrice());
                pstmt.addBatch();
            }
            
            int[] results = pstmt.executeBatch();
            conn.commit();
            
            for (int result : results) {
                if (result > 0) insertedCount++;
            }
            
            System.out.println("Batch insert completed: " + insertedCount + " records inserted.");
        } catch (SQLException e) {
            System.err.println("Error in batch insert: " + e.getMessage());
            e.printStackTrace();
        }
        
        return insertedCount;
    }
    
    /**
     * Retrieves the 10 lowest prices for a specific hotel in a specific city.
     * 
     * @param hotelName Name of the hotel
     * @param city City where the hotel is located
     * @return List of HotelPriceRecord objects ordered by price (lowest first)
     */
    public List<HotelPriceRecord> getLowestPrices(String hotelName, String city, int limit) {
        String querySQL = "SELECT hotel_name, city, checkin_date, price " +
                "FROM hotel_prices " +
                "WHERE hotel_name = ? AND city = ? " +
                "ORDER BY price ASC " +
                "LIMIT ?";
        
        List<HotelPriceRecord> records = new ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(querySQL)) {
            pstmt.setString(1, hotelName);
            pstmt.setString(2, city);
            pstmt.setInt(3, limit);
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                HotelPriceRecord record = new HotelPriceRecord(
                    rs.getString("hotel_name"),
                    rs.getString("city"),
                    rs.getString("checkin_date"),
                    rs.getDouble("price")
                );
                records.add(record);
            }
        } catch (SQLException e) {
            System.err.println("Error querying lowest prices: " + e.getMessage());
            e.printStackTrace();
        }
        
        return records;
    }
    
    /**
     * Retrieves all unique hotel-city combinations from the database.
     * 
     * @return List of String arrays where [0] is hotel_name and [1] is city
     */
    public List<String[]> getUniqueHotelCityCombinations() {
        String querySQL = "SELECT DISTINCT hotel_name, city FROM hotel_prices ORDER BY hotel_name, city";
        List<String[]> combinations = new ArrayList<>();
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(querySQL)) {
            
            while (rs.next()) {
                String[] combination = new String[] {
                    rs.getString("hotel_name"),
                    rs.getString("city")
                };
                combinations.add(combination);
            }
        } catch (SQLException e) {
            System.err.println("Error getting unique combinations: " + e.getMessage());
            e.printStackTrace();
        }
        
        return combinations;
    }
    
    /**
     * Clears all records from the hotel_prices table.
     * Useful for testing or resetting the database.
     */
    public void clearAllRecords() {
        String deleteSQL = "DELETE FROM hotel_prices";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            int deletedRows = stmt.executeUpdate(deleteSQL);
            System.out.println("Cleared " + deletedRows + " records from database.");
        } catch (SQLException e) {
            System.err.println("Error clearing records: " + e.getMessage());
        }
    }
    
    /**
     * Gets the total count of records in the database.
     * 
     * @return Total number of records
     */
    public int getRecordCount() {
        String countSQL = "SELECT COUNT(*) as count FROM hotel_prices";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSQL)) {
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting record count: " + e.getMessage());
        }
        
        return 0;
    }
}
