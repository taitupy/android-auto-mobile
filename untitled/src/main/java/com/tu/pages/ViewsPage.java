package com.tu.pages;
import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

public class ViewsPage extends BasePage{
    private final By buttonsMenu = AppiumBy.accessibilityId("Buttons");

    public ButtonsPage openButtons() {
        click(buttonsMenu);
        return new ButtonsPage();
    }
}
