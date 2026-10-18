package com.tu.base;

import com.tu.core.DriverFactory;
import com.tu.core.DriverManager;
import com.tu.core.SystemDialogHandler;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BaseTest {

    @BeforeMethod
    public void setUp() throws Exception {
        DriverManager.setDriver(DriverFactory.createAndroidDriver());
        // Emulator vừa boot hay hiện popup "... isn't responding" che app -> đóng trước khi test
        SystemDialogHandler.dismissAnrDialogs(DriverManager.getDriver());
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            // Test fail -> chụp màn hình TRƯỚC khi đóng driver
            if (result.getStatus() == ITestResult.FAILURE) {
                takeScreenshot(result);
            }
        } finally {
            DriverManager.quitDriver();
        }
    }

    private void takeScreenshot(ITestResult result) {
        if (DriverManager.getDriver() == null) {
            return;   // driver chưa tạo được (vd: Appium chưa chạy) -> không có gì để chụp
        }
        try {
            String fileName = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName()
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".png";

            File src = DriverManager.getDriver().getScreenshotAs(OutputType.FILE);
            Path dest = Path.of("screenshots", fileName);
            Files.createDirectories(dest.getParent());
            Files.copy(src.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("[Screenshot] " + dest.toAbsolutePath());

            // Đính kèm ảnh vào Allure report (hiện ngay trong bước bị fail)
            Allure.addAttachment("Screenshot khi fail", "image/png",
                    new ByteArrayInputStream(Files.readAllBytes(dest)), ".png");
        } catch (Exception e) {
            System.out.println("[Screenshot] Khong chup duoc anh: " + e.getMessage());
        }
    }
}
