package com.example.hotelpricingproject.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.hotelpricingproject.entity.HotelPrice;

@Repository
public interface HotelPriceRepository extends JpaRepository<HotelPrice, Long> {

    List<HotelPrice> findByHotelNameAndCity(String hotelName, String city);

    List<HotelPrice> findByHotelNameAndCityAndCheckInDateBetween(
            String hotelName, String city, LocalDate startDate, LocalDate endDate);

    // Use Pageable instead of JPQL LIMIT (LIMIT is invalid in JPQL)
    @Query("SELECT h FROM HotelPrice h WHERE h.hotelName = :hotelName AND h.city = :city ORDER BY h.price ASC")
    List<HotelPrice> findLowestPricesByHotelAndCity(@Param("hotelName") String hotelName,
                                                           @Param("city") String city,
                                                           Pageable pageable);

    @Query("SELECT h FROM HotelPrice h WHERE h.hotelName = :hotelName AND h.city = :city " +
            "AND h.checkInDate BETWEEN :startDate AND :endDate ORDER BY h.price ASC")
    List<HotelPrice> findLowestPricesByHotelCityAndDateRange(@Param("hotelName") String hotelName,
                                                                     @Param("city") String city,
                                                                     @Param("startDate") LocalDate startDate,
                                                                     @Param("endDate") LocalDate endDate,
                                                                     Pageable pageable);

}
