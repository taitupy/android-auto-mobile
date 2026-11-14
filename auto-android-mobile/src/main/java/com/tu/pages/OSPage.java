package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/** Màn hình: Home > OS */
public class OSPage extends BasePage {
    private final By smsMessagingMenu = AppiumBy.accessibilityId("SMS Messaging");

    public SMSMessagingPage openSmsMessaging() {
        click(smsMessagingMenu);
        return new SMSMessagingPage();
    }
}
