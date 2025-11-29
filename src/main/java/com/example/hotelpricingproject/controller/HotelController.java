package com.example.hotelpricingproject.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.hotelpricingproject.entity.HotelPrice;
import com.example.hotelpricingproject.service.HotelAnalysisService;
import com.example.hotelpricingproject.service.HotelScraperService;

@RestController
@RequestMapping("/api/hotels")
public class HotelController {

    private final HotelScraperService scraperService;
    private final HotelAnalysisService analysisService;

    public HotelController(HotelScraperService scraperService, HotelAnalysisService analysisService) {
        this.scraperService = scraperService;
        this.analysisService = analysisService;
    }

    /**
     * Scrapes hotel prices for a given hotel, city, and date range.
     */
    @PostMapping("/scrape")
    public ResponseEntity<String> scrapeHotels(
            @RequestParam String hotelName,
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<HotelPrice> prices = scraperService.scrapeHotelPrices(hotelName, city, startDate, endDate);
            return ResponseEntity.ok("Successfully scraped " + prices.size() + " price records.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error during scraping: " + e.getMessage());
        }
    }

    /**
     * Bulk scrape Ritz-Carlton across 5 cities (Nov 15, 2025 - May 1, 2026)
     */
    @PostMapping("/scrape-multi-city")
    public ResponseEntity<String> scrapeMultiCity() {
        try {
            String hotelName = "Ritz-Carlton";
            List<String> cities = List.of("Las Vegas", "New York City", "Miami", "Paris", "Los Angeles");
            LocalDate startDate = LocalDate.of(2025, 11, 15);
            LocalDate endDate = LocalDate.of(2026, 5, 1);
            
            int totalScraped = 0;
            for (String city : cities) {
                List<HotelPrice> prices = scraperService.scrapeHotelPrices(hotelName, city, startDate, endDate);
                totalScraped += prices.size();
            }
            
            return ResponseEntity.ok("✅ Successfully scraped " + totalScraped + " price records across 5 cities!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("❌ Error during scraping: " + e.getMessage());
        }
    }

    /**
     * Gets the 10 lowest prices for a hotel in a specific city.
     */
    @GetMapping("/lowest-prices")
    public ResponseEntity<List<HotelPrice>> getLowestPrices(
            @RequestParam String hotelName,
            @RequestParam String city) {
        List<HotelPrice> prices = analysisService.findLowestPrices(hotelName, city);
        return ResponseEntity.ok(prices);
    }

    /**
     * Gets the 10 lowest prices for a hotel in a city within a date range.
     */
    @GetMapping("/lowest-prices-by-range")
    public ResponseEntity<List<HotelPrice>> getLowestPricesByRange(
            @RequestParam String hotelName,
            @RequestParam String city,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<HotelPrice> prices = analysisService.findLowestPrices(hotelName, city, startDate, endDate);
        return ResponseEntity.ok(prices);
    }

    /**
     * Gets all prices for a hotel in a city.
     */
    @GetMapping("/prices")
    public ResponseEntity<List<HotelPrice>> getPrices(
            @RequestParam String hotelName,
            @RequestParam String city) {
        List<HotelPrice> prices = analysisService.getPricesForHotelInCity(hotelName, city);
        return ResponseEntity.ok(prices);
    }

    /**
     * Generates an analysis report for a hotel across multiple cities.
     */
    @GetMapping("/analysis-report")
    public ResponseEntity<String> getAnalysisReport(
            @RequestParam String hotelName,
            @RequestParam List<String> cities) {
        String report = analysisService.generateAnalysisReport(hotelName, cities);
        return ResponseEntity.ok(report);
    }
}
