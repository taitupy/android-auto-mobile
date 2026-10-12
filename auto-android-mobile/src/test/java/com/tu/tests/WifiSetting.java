package com.tu.tests;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;
import java.time.Duration;

public class WifiSetting {
    private AndroidDriver driver;

    @BeforeMethod
    public void setUp() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("io.appium.android.apis")
                .setAppActivity(".ApiDemos");

        driver = new AndroidDriver(URI.create("http://127.0.0.1:4723").toURL(), options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
    }

    @Test
    public void setWifiName() {
        // 1. Màn hình chính -> Preference
        driver.findElement(AppiumBy.accessibilityId("Preference")).click();

        // 2. Preference -> 3. Preference dependencies
        driver.findElement(AppiumBy.accessibilityId("3. Preference dependencies")).click();

        // 3. Tick checkbox WiFi
        driver.findElement(AppiumBy.id("android:id/checkbox")).click();

        // 4. Mở WiFi settings
        driver.findElement(AppiumBy.xpath("//android.widget.TextView[@text='WiFi settings']")).click();

        // 5. Kiểm tra tiêu đề popup
        String title = driver.findElement(AppiumBy.id("android:id/alertTitle")).getText();
        Assert.assertEquals(title, "WiFi settings");

        // 6. Nhập tên WiFi và bấm OK
        driver.findElement(AppiumBy.id("android:id/edit")).sendKeys("Tu WiFi");
        driver.findElement(AppiumBy.id("android:id/button1")).click();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}