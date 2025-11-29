package com.example.hotelpricingproject.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "hotel_prices")
public class HotelPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String hotelName;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private LocalDate checkInDate;

    @Column(nullable = false)
    private LocalDate checkOutDate;

    @Column(nullable = false)
    private BigDecimal price;

    @Column
    private String rating;

    @Column
    private String address;

    @Column(nullable = false, updatable = false)
    private LocalDate scrapedDate;

    // Constructors
    public HotelPrice() {
        this.scrapedDate = LocalDate.now();
    }

    public HotelPrice(String hotelName, String city, LocalDate checkInDate, LocalDate checkOutDate, 
                      BigDecimal price, String rating, String address) {
        this.hotelName = hotelName;
        this.city = city;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.price = price;
        this.rating = rating;
        this.address = address;
        this.scrapedDate = LocalDate.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getScrapedDate() {
        return scrapedDate;
    }

    @Override
    public String toString() {
        return "HotelPrice{" +
                "id=" + id +
                ", hotelName='" + hotelName + '\'' +
                ", city='" + city + '\'' +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                ", price=" + price +
                ", rating='" + rating + '\'' +
                ", address='" + address + '\'' +
                ", scrapedDate=" + scrapedDate +
                '}';
    }
}
