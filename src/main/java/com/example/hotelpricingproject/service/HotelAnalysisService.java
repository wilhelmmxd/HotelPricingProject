package com.example.hotelpricingproject.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.hotelpricingproject.entity.HotelPrice;
import com.example.hotelpricingproject.repository.HotelPriceRepository;

@Service
public class HotelAnalysisService {

    @Autowired
    private HotelPriceRepository hotelPriceRepository;

    /**
     * Finds the 10 lowest prices for a given hotel in a specific city.
     */
    public List<HotelPrice> findLowestPrices(String hotelName, String city) {
        Pageable topTen = PageRequest.of(0, 10);
        return hotelPriceRepository.findLowestPricesByHotelAndCity(hotelName, city, topTen);
    }

    /**
     * Finds the 10 lowest prices for a given hotel in a specific city within a date range.
     */
    public List<HotelPrice> findLowestPrices(String hotelName, String city, LocalDate startDate, LocalDate endDate) {
        Pageable topTen = PageRequest.of(0, 10);
        return hotelPriceRepository.findLowestPricesByHotelCityAndDateRange(hotelName, city, startDate, endDate, topTen);
    }

    /**
     * Gets all prices for a hotel in a city.
     */
    public List<HotelPrice> getPricesForHotelInCity(String hotelName, String city) {
        return hotelPriceRepository.findByHotelNameAndCity(hotelName, city);
    }

    /**
     * Generates a summary report for analysis.
     */
    public String generateAnalysisReport(String hotelName, List<String> cities) {
        StringBuilder report = new StringBuilder();
        report.append("=== Hotel Price Analysis Report ===\n");
        report.append("Hotel: ").append(hotelName).append("\n\n");

        for (String city : cities) {
            report.append("City: ").append(city).append("\n");
            List<HotelPrice> lowestPrices = findLowestPrices(hotelName, city);

            if (lowestPrices.isEmpty()) {
                report.append("  No data available\n\n");
                continue;
            }

            report.append("  Top 10 Lowest Prices:\n");
            for (int i = 0; i < lowestPrices.size(); i++) {
                HotelPrice price = lowestPrices.get(i);
                report.append(String.format("  %d. Check-in: %s, Price: $%s, Rating: %s\n",
                        i + 1, price.getCheckInDate(), price.getPrice(), price.getRating()));
            }
            report.append("\n");
        }

        return report.toString();
    }
}
