package com.tu.pages;

import com.tu.core.ConfigReader;
import com.tu.core.DriverManager;
import com.tu.core.SystemDialogHandler;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {
    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        // Thời gian chờ lấy từ config: dev = 15s, staging/CI = 30s (emulator CI chậm hơn)
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.explicit.wait", 15)));
    }

    /**
     * Chờ 1 điều kiện. Nếu hết giờ mà màn hình đang bị popup hệ thống "isn't responding" che
     * -> đóng popup rồi chờ lại thêm 1 lần.
     */
    private <T> T waitUntil(ExpectedCondition<T> condition) {
        try {
            return wait.until(condition);
        } catch (TimeoutException e) {
            if (SystemDialogHandler.dismissAnrDialogs(driver)) {
                return wait.until(condition);
            }
            throw e;
        }
    }

    protected WebElement waitVisible(By locator) {
        return waitUntil(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitUntil(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    protected void type(By locator, String text) {
        WebElement el = waitVisible(locator);
        el.clear();
        el.sendKeys(text);
    }

    protected String getText(By locator) {
        return waitVisible(locator).getText();
    }

    protected boolean isChecked(By locator) {
        return "true".equals(waitVisible(locator).getAttribute("checked"));
    }

    protected boolean isEnabled(By locator) {
        return waitVisible(locator).isEnabled();
    }
}
