package com.hotel.automation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Analyzes hotel price data from the database.
 * Finds the best prices for each hotel-city combination.
 * Follows DRY principle with reusable analysis methods.
 */
public class DataAnalyzer {
    private DatabaseManager dbManager;
    
    /**
     * Constructor that initializes the analyzer with a database manager.
     * 
     * @param dbManager DatabaseManager instance for querying data
     */
    public DataAnalyzer(DatabaseManager dbManager) {
        this.dbManager = dbManager;
    }
    
    /**
     * Analyzes all hotel-city combinations and finds the 10 lowest prices for each.
     * This is the main analysis method that orchestrates the entire process.
     * 
     * @return Map where key is "HotelName - City" and value is list of 10 lowest price records
     */
    public Map<String, List<HotelPriceRecord>> analyzeLowestPrices() {
        Map<String, List<HotelPriceRecord>> analysisResults = new HashMap<>();
        
        // Get all unique hotel-city combinations
        List<String[]> combinations = dbManager.getUniqueHotelCityCombinations();
        
        System.out.println("Analyzing " + combinations.size() + " hotel-city combinations...");
        
        // For each combination, get the 10 lowest prices
        for (String[] combination : combinations) {
            String hotelName = combination[0];
            String city = combination[1];
            
            List<HotelPriceRecord> lowestPrices = getLowestPricesForHotelCity(hotelName, city, 10);
            
            String key = formatCombinationKey(hotelName, city);
            analysisResults.put(key, lowestPrices);
            
            System.out.println("Found " + lowestPrices.size() + " prices for " + key);
        }
        
        return analysisResults;
    }
    
    /**
     * Gets the lowest prices for a specific hotel in a specific city.
     * 
     * @param hotelName Name of the hotel
     * @param city City where the hotel is located
     * @param limit Number of lowest prices to retrieve
     * @return List of HotelPriceRecord objects ordered by price (lowest first)
     */
    public List<HotelPriceRecord> getLowestPricesForHotelCity(String hotelName, String city, int limit) {
        return dbManager.getLowestPrices(hotelName, city, limit);
    }
    
    /**
     * Calculates summary statistics for a list of price records.
     * 
     * @param records List of price records
     * @return PriceStatistics object containing min, max, and average prices
     */
    public PriceStatistics calculateStatistics(List<HotelPriceRecord> records) {
        if (records == null || records.isEmpty()) {
            return new PriceStatistics(0, 0, 0);
        }
        
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        
        for (HotelPriceRecord record : records) {
            double price = record.getPrice();
            min = Math.min(min, price);
            max = Math.max(max, price);
            sum += price;
        }
        
        double average = sum / records.size();
        
        return new PriceStatistics(min, max, average);
    }
    
    /**
     * Formats a hotel-city combination into a standard key format.
     * 
     * @param hotelName Hotel name
     * @param city City name
     * @return Formatted string "HotelName - City"
     */
    private String formatCombinationKey(String hotelName, String city) {
        return hotelName + " - " + city;
    }
    
    /**
     * Gets all unique hotel names from the database.
     * 
     * @return List of unique hotel names
     */
    public List<String> getUniqueHotels() {
        List<String> hotels = new ArrayList<>();
        List<String[]> combinations = dbManager.getUniqueHotelCityCombinations();
        
        for (String[] combination : combinations) {
            String hotel = combination[0];
            if (!hotels.contains(hotel)) {
                hotels.add(hotel);
            }
        }
        
        return hotels;
    }
    
    /**
     * Gets all unique cities from the database.
     * 
     * @return List of unique city names
     */
    public List<String> getUniqueCities() {
        List<String> cities = new ArrayList<>();
        List<String[]> combinations = dbManager.getUniqueHotelCityCombinations();
        
        for (String[] combination : combinations) {
            String city = combination[1];
            if (!cities.contains(city)) {
                cities.add(city);
            }
        }
        
        return cities;
    }
    
    /**
     * Inner class to hold price statistics.
     */
    public static class PriceStatistics {
        private final double minPrice;
        private final double maxPrice;
        private final double averagePrice;
        
        public PriceStatistics(double minPrice, double maxPrice, double averagePrice) {
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
            this.averagePrice = averagePrice;
        }
        
        public double getMinPrice() {
            return minPrice;
        }
        
        public double getMaxPrice() {
            return maxPrice;
        }
        
        public double getAveragePrice() {
            return averagePrice;
        }
        
        @Override
        public String toString() {
            return String.format("PriceStatistics{min=%.2f, max=%.2f, avg=%.2f}", 
                    minPrice, maxPrice, averagePrice);
        }
    }
}
