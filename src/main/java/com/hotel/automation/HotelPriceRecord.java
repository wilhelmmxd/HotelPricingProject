package com.hotel.automation;

/**
 * Data model class representing a hotel price record.
 * This class follows JavaBean conventions with proper encapsulation.
 */
public class HotelPriceRecord {
    private String hotelName;
    private String city;
    private String checkinDate;  // Format: 'YYYY-MM-DD'
    private double price;
    
    /**
     * Default constructor.
     */
    public HotelPriceRecord() {
    }
    
    /**
     * Parameterized constructor.
     * 
     * @param hotelName Name of the hotel
     * @param city City where the hotel is located
     * @param checkinDate Check-in date in 'YYYY-MM-DD' format
     * @param price Room price
     */
    public HotelPriceRecord(String hotelName, String city, String checkinDate, double price) {
        this.hotelName = hotelName;
        this.city = city;
        this.checkinDate = checkinDate;
        this.price = price;
    }
    
    // Getters and Setters
    
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
    
    public String getCheckinDate() {
        return checkinDate;
    }
    
    public void setCheckinDate(String checkinDate) {
        this.checkinDate = checkinDate;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return String.format("HotelPriceRecord{hotel='%s', city='%s', date='%s', price=%.2f}",
                hotelName, city, checkinDate, price);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        HotelPriceRecord that = (HotelPriceRecord) o;
        
        if (Double.compare(that.price, price) != 0) return false;
        if (hotelName != null ? !hotelName.equals(that.hotelName) : that.hotelName != null) return false;
        if (city != null ? !city.equals(that.city) : that.city != null) return false;
        return checkinDate != null ? checkinDate.equals(that.checkinDate) : that.checkinDate == null;
    }
    
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = hotelName != null ? hotelName.hashCode() : 0;
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (checkinDate != null ? checkinDate.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
