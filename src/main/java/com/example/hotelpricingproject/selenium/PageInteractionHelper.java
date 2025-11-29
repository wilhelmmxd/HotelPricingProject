package com.example.hotelpricingproject.selenium;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class PageInteractionHelper {
    private PageInteractionHelper() {}

    public static void handleCookies(WebDriver driver, WebDriverWait wait) {
        try {
            WebElement cookieButton = wait.until(ExpectedConditions.elementToBeClickable(
                    By.cssSelector("[aria-label='Dismiss']")));
            cookieButton.click();
        } catch (TimeoutException ignored) {
        }
    }

    public static void handlePopups(WebDriver driver) {
        try {
            List<WebElement> closeButtons = driver.findElements(By.cssSelector("button[aria-label='Dismiss'], button.close"));
            for (WebElement button : closeButtons) {
                try {
                    button.click();
                    Thread.sleep(300);
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    public static void scrollPage(WebDriver driver, JavascriptExecutor js) {
        for (int i = 0; i < 3; i++) {
            js.executeScript("window.scrollBy(0, window.innerHeight);");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        js.executeScript("window.scrollTo(0, 0);");
    }
}