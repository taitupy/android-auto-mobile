package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class WifiSettingsDialog extends BasePage {
    private final By title      = AppiumBy.id("android:id/alertTitle");
    private final By wifiInput  = AppiumBy.id("android:id/edit");
    private final By okButton   = AppiumBy.id("android:id/button1");
    private final By cancelBtn  = AppiumBy.id("android:id/button2");

    public String getTitle() { return getText(title); }

    public String getWifiName() { return getText(wifiInput); }

    public WifiSettingsDialog enterWifiName(String name) {
        type(wifiInput, name);
        return this;
    }

    public PreferenceDependenciesPage clickOk() {
        click(okButton);
        return new PreferenceDependenciesPage();
    }

    public PreferenceDependenciesPage clickCancel() {
        click(cancelBtn);
        return new PreferenceDependenciesPage();
    }
}