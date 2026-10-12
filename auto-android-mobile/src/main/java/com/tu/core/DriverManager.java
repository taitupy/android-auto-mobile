package com.tu.core;

import io.appium.java_client.android.AndroidDriver;

public class DriverManager {
    private static final ThreadLocal<AndroidDriver> DRIVER = new ThreadLocal<>();

    public static AndroidDriver getDriver() { return DRIVER.get(); }

    public static void setDriver(AndroidDriver driver) { DRIVER.set(driver); }

    public static void quitDriver() {
        if (DRIVER.get() != null) {
            DRIVER.get().quit();
            DRIVER.remove();
        }
    }
}