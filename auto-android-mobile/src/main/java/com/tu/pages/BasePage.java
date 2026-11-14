package com.tu.pages;

import com.tu.core.ConfigReader;
import com.tu.core.DriverManager;
import com.tu.core.SystemDialogHandler;
import io.appium.java_client.android.AndroidDriver;
import io.qameta.allure.Allure;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Lớp cha của mọi Page.
 * - Giữ driver + wait dùng chung
 * - Cung cấp hàm thao tác cơ bản: click, type, getText, ...
 * - Mỗi thao tác được ghi thành 1 STEP trong Allure report -> đọc report biết test đã làm gì, fail ở bước nào
 */
public abstract class BasePage {
    protected final AndroidDriver driver;
    protected final WebDriverWait wait;

    public BasePage() {
        this.driver = DriverManager.getDriver();
        // Thời gian chờ lấy từ config: dev = 15s, staging/CI = 30s (emulator CI chậm hơn)
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(ConfigReader.getInt("timeout.explicit.wait", 15)));
    }

    // =====================================================================
    // Chờ element (có xử lý popup hệ thống "... isn't responding")
    // =====================================================================

    /**
     * - Trước khi thao tác: nếu đang có popup ANR -> đóng ngay
     *   (tránh đọc nhầm tiêu đề popup ANR, vì nó dùng chung id android:id/alertTitle)
     * - Hết giờ chờ: kiểm tra lại popup (có thể vừa hiện ra trong lúc chờ) -> đóng và chờ thêm 1 lần
     */
    private <T> T waitUntil(ExpectedCondition<T> condition) {
        SystemDialogHandler.dismissAnrDialogs(driver);
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

    // =====================================================================
    // Thao tác (mỗi hàm = 1 step trong Allure report)
    // =====================================================================

    protected void click(By locator) {
        Allure.step("Click " + describe(locator), () ->
                waitUntil(ExpectedConditions.elementToBeClickable(locator)).click());
    }

    /** Xóa nội dung cũ rồi nhập text mới */
    protected void type(By locator, String text) {
        Allure.step("Nhập \"" + text + "\" vào " + describe(locator), () -> {
            WebElement el = waitVisible(locator);
            el.clear();
            el.sendKeys(text);
        });
    }

    protected void clear(By locator) {
        Allure.step("Xóa nội dung " + describe(locator), () -> waitVisible(locator).clear());
    }

    /** Ẩn bàn phím ảo (bàn phím có thể che các element ở nửa dưới màn hình) */
    protected void hideKeyboard() {
        try {
            if (driver.isKeyboardShown()) {
                driver.hideKeyboard();
            }
        } catch (Exception ignored) {
            // không ẩn được cũng không ảnh hưởng test
        }
    }

    // =====================================================================
    // Đọc thông tin để verify (không ghi step để report gọn)
    // =====================================================================

    protected String getText(By locator) {
        return waitVisible(locator).getText();
    }

    protected String getAttribute(By locator, String name) {
        return waitVisible(locator).getAttribute(name);
    }

    protected boolean isChecked(By locator) {
        return "true".equals(getAttribute(locator, "checked"));
    }

    protected boolean isEnabled(By locator) {
        return waitVisible(locator).isEnabled();
    }

    /**
     * Element có đang hiện trên màn hình không - KHÔNG chờ, KHÔNG ném lỗi.
     * Dùng cho case kiểm tra element KHÔNG được hiện (waitVisible sẽ chờ hết giờ rồi báo lỗi).
     */
    protected boolean isPresent(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    /** "AppiumBy.id: io.appium.android.apis:id/sms_recipient" -> "[id: sms_recipient]" cho report dễ đọc */
    private String describe(By locator) {
        String s = locator.toString().replace("AppiumBy.", "").replace("By.", "");
        return "[" + s.replace("io.appium.android.apis:id/", "") + "]";
    }
}
