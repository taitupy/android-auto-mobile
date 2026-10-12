package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class ButtonsPage extends BasePage {

    // ===== Locators =====
    private final By normalButton = AppiumBy.id("io.appium.android.apis:id/button_normal");
    private final By smallButton  = AppiumBy.id("io.appium.android.apis:id/button_small");
    private final By toggleButton = AppiumBy.id("io.appium.android.apis:id/button_toggle");

    // ===== Normal =====
    public boolean isNormalDisplayed() { return waitVisible(normalButton).isDisplayed(); }
    public String  getNormalText()     { return getText(normalButton); }
    public boolean isNormalClickable() { return "true".equals(waitVisible(normalButton).getAttribute("clickable")); }
    public int     getNormalHeight()   { return waitVisible(normalButton).getRect().getHeight(); }

    // ===== Small =====
    public boolean isSmallDisplayed() { return waitVisible(smallButton).isDisplayed(); }
    public String  getSmallText()     { return getText(smallButton); }
    public boolean isSmallClickable() { return "true".equals(waitVisible(smallButton).getAttribute("clickable")); }
    public int     getSmallHeight()   { return waitVisible(smallButton).getRect().getHeight(); }

    // ===== Toggle =====
    public boolean isToggleDisplayed() { return waitVisible(toggleButton).isDisplayed(); }
    public String  getToggleText()     { return getText(toggleButton); }
    public boolean isToggleOn()        { return isChecked(toggleButton); }
    public String  getToggleClass()    { return waitVisible(toggleButton).getAttribute("class"); }

    public ButtonsPage tapToggle() {
        click(toggleButton);
        return this;   // vẫn ở màn Buttons nên trả về this
    }
}