package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class HomePage extends BasePage {

    private final By preferenceMenu = AppiumBy.accessibilityId("Preference");
    private final By viewsMenu = AppiumBy.accessibilityId("Views");

    public PreferencePage openPreference() {
        click(preferenceMenu);
        return new PreferencePage();
    }

    public ViewsPage openViews(){
        click(viewsMenu);
        return new ViewsPage();
    }
}