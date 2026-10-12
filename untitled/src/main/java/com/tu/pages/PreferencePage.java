package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class PreferencePage extends BasePage {

    // ===== Locators =====
    private final By preferenceDependencies = AppiumBy.accessibilityId("3. Preference dependencies");

    // ===== Actions =====
    public PreferenceDependenciesPage openPreferenceDependencies() {
        click(preferenceDependencies);
        return new PreferenceDependenciesPage();
    }
}