package com.tu.base;

import com.tu.core.DriverFactory;
import com.tu.core.DriverManager;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.time.Duration;

public class BaseTest {

    @BeforeMethod
    public void setUp() throws Exception {
        DriverManager.setDriver(DriverFactory.createAndroidDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        DriverManager.quitDriver();
    }
}