package com.example.hotelpricingproject.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.hotelpricingproject.entity.HotelPrice;
import com.example.hotelpricingproject.repository.HotelPriceRepository;
import com.example.hotelpricingproject.selenium.PageInteractionHelper;
import com.example.hotelpricingproject.selenium.WebDriverFactory;

@Service
public class HotelScraperService {

    @Autowired
    private HotelPriceRepository hotelPriceRepository;

    private static final Logger log = LoggerFactory.getLogger(HotelScraperService.class);

    private static final String BOOKING_URL = "https://www.booking.com/searchresults.html";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Centralized fallback selectors for resilient price extraction
    private static final String[] PRICE_SELECTORS = new String[]{
        "[data-testid='price-and-discounted-price']",
        ".price_price",
        "[class*='price']"
    };

    public List<HotelPrice> scrapeHotelPrices(String hotelName, String city, LocalDate startDate, LocalDate endDate) {
        List<HotelPrice> scrapedPrices = new ArrayList<>();
        WebDriver driver = null;
        try {
            driver = createWebDriver();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("Object.defineProperty(navigator, 'webdriver', {get: () => undefined})");

            LocalDate currentDate = startDate;
            while (!currentDate.isAfter(endDate)) {
                LocalDate checkOut = currentDate.plusDays(1);
                try {
                    String url = buildBookingUrl(hotelName, city, currentDate, checkOut);
                    driver.get(url);
                    wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("[data-testid='property-card']")));
                    PageInteractionHelper.handlePopups(driver);
                    PageInteractionHelper.handleCookies(driver, wait);
                    PageInteractionHelper.scrollPage(driver, js);
                    HotelPrice price = extractHotelData(driver, hotelName, city, currentDate, checkOut);
                    if (price != null) {
                        scrapedPrices.add(price);
                        hotelPriceRepository.save(price);
                        log.info("Scraped {} in {} for {} - ${}", hotelName, city, currentDate, price.getPrice());
                    }
                } catch (Exception e) {
                    log.warn("Error scraping {} in {} for date {}: {}", hotelName, city, currentDate, e.getMessage());
                }
                currentDate = currentDate.plusDays(1);
            }
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }
        return scrapedPrices;
    }

    private HotelPrice extractHotelData(WebDriver driver, String hotelName, String city, LocalDate checkIn, LocalDate checkOut) {
        try {
            List<WebElement> hotelCards = driver.findElements(By.cssSelector("[data-testid='property-card']"));
            for (WebElement card : hotelCards) {
                String cardName = extractText(card, "[data-testid='title']");
                if (cardName != null && cardName.toLowerCase().contains(hotelName.toLowerCase())) {
                    BigDecimal price = extractPrice(card);
                    String rating = extractText(card, "[data-testid='review-score']");
                    String address = extractText(card, "[data-testid='address']");
                    if (price != null) {
                        return new HotelPrice(hotelName, city, checkIn, checkOut, price, rating, address);
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Error extracting hotel data: {}", e.getMessage());
        }
        return null;
    }

    private BigDecimal extractPrice(WebElement card) {
        for (String selector : PRICE_SELECTORS) {
            try {
                WebElement priceElement = card.findElement(By.cssSelector(selector));
                String priceText = priceElement.getText().replaceAll("[^\\d.,]", "").replace(",", "");
                if (!priceText.isEmpty()) {
                    return new BigDecimal(priceText);
                }
            } catch (org.openqa.selenium.NoSuchElementException e) {
                // try next
            }
        }
        return null;
    }

    private String extractText(WebElement card, String selector) {
        try {
            return card.findElement(By.cssSelector(selector)).getText();
        } catch (org.openqa.selenium.NoSuchElementException e) {
            return null;
        }
    }

    private String buildBookingUrl(String hotelName, String city, LocalDate checkIn, LocalDate checkOut) {
        String query = hotelName.replace(" ", "%20") + "%20" + city.replace(" ", "%20");
        return BOOKING_URL + "?ss=" + query + "&checkin=" + checkIn.format(DATE_FORMATTER) + "&checkout=" + checkOut.format(DATE_FORMATTER);
    }

    private WebDriver createWebDriver() {
        return WebDriverFactory.createDefault();
    }
}
