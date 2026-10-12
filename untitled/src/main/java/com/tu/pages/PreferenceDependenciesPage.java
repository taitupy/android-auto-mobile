package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class PreferenceDependenciesPage extends BasePage {

    // ===== Locators =====
    private final By wifiCheckbox = AppiumBy.id("android:id/checkbox");
    private final By wifiSettings = AppiumBy.xpath("//android.widget.TextView[@text='WiFi settings']");

    // ===== Actions =====
    public PreferenceDependenciesPage enableWifi() {
        if (!isChecked(wifiCheckbox)) {
            click(wifiCheckbox);
        }
        return this;
    }

    public WifiSettingsDialog openWifiSettings() {
        click(wifiSettings);
        return new WifiSettingsDialog();
    }

    // ===== Verify helpers =====
    public boolean isWifiChecked() {
        return isChecked(wifiCheckbox);
    }

    public boolean isWifiSettingsEnabled() {
        return isEnabled(wifiSettings);
    }
}