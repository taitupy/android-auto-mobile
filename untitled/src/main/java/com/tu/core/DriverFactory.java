package com.tu.core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.URI;
import java.time.Duration;

public class DriverFactory {
    private static final String APPIUM_URL = "http://127.0.0.1:4723";

    public static AndroidDriver createAndroidDriver() throws Exception {
        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName("emulator-5554")
                .setAppPackage("io.appium.android.apis")
                .setAppActivity(".ApiDemos")
                .setNewCommandTimeout(Duration.ofSeconds(120));

        return new AndroidDriver(URI.create(APPIUM_URL).toURL(), options);
    }
}