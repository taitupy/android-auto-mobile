package com.tu.base;

import com.tu.core.ConfigReader;
import com.tu.core.DriverFactory;
import com.tu.core.DriverManager;
import com.tu.core.SystemDialogHandler;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

public class BaseTest {

    /** Thư mục Allure ghi kết quả (pom.xml đặt = target/allure-results) */
    private static final Path ALLURE_RESULTS =
            Path.of(System.getProperty("allure.results.directory", "target/allure-results"));
    private static volatile boolean environmentWritten = false;

    // =====================================================================
    // Trước cả bộ test: chép file phân loại lỗi (categories.json) vào Allure
    // =====================================================================
    @BeforeSuite(alwaysRun = true)
    public void prepareAllure() {
        try (InputStream in = BaseTest.class.getClassLoader().getResourceAsStream("allure/categories.json")) {
            if (in != null) {
                Files.createDirectories(ALLURE_RESULTS);
                Files.copy(in, ALLURE_RESULTS.resolve("categories.json"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            System.out.println("[Allure] Khong chep duoc categories.json: " + e.getMessage());
        }
    }

    // =====================================================================
    // Trước mỗi test: mở app
    // =====================================================================
    @BeforeMethod(alwaysRun = true)
    public void setUp() throws Exception {
        DriverManager.setDriver(DriverFactory.createAndroidDriver());
        writeAllureEnvironment(DriverManager.getDriver());
        // Emulator vừa boot hay hiện popup "... isn't responding" che app -> đóng trước khi test
        SystemDialogHandler.dismissAnrDialogs(DriverManager.getDriver());
        startVideoRecording();
    }

    // =====================================================================
    // Sau mỗi test: test fail -> đính kèm bằng chứng vào report, rồi đóng app
    // =====================================================================
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        try {
            boolean failed = result.getStatus() == ITestResult.FAILURE;
            if (failed) {
                takeScreenshot(result);
                attachPageSource();
            }
            stopVideoRecording(failed);   // chỉ đính kèm video khi test fail
        } finally {
            DriverManager.quitDriver();
        }
    }

    /** Chụp màn hình: lưu vào thư mục screenshots/ và đính kèm vào Allure */
    private void takeScreenshot(ITestResult result) {
        if (DriverManager.getDriver() == null) {
            return;   // driver chưa tạo được (vd: Appium chưa chạy) -> không có gì để chụp
        }
        try {
            String fileName = result.getTestClass().getRealClass().getSimpleName()
                    + "_" + result.getMethod().getMethodName()
                    + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + ".png";

            byte[] png = DriverManager.getDriver().getScreenshotAs(OutputType.BYTES);
            Path dest = Path.of("screenshots", fileName);
            Files.createDirectories(dest.getParent());
            Files.write(dest, png);
            System.out.println("[Screenshot] " + dest.toAbsolutePath());

            Allure.addAttachment("Screenshot khi fail", "image/png", new ByteArrayInputStream(png), ".png");
        } catch (Exception e) {
            System.out.println("[Screenshot] Khong chup duoc anh: " + e.getMessage());
        }
    }

    /**
     * Đính kèm cấu trúc màn hình (XML) lúc fail - giống cây element trong Appium Inspector.
     * Rất hữu ích khi lỗi "không tìm thấy element": mở ra xem màn hình lúc đó có những element/id nào.
     */
    private void attachPageSource() {
        if (DriverManager.getDriver() == null) {
            return;
        }
        try {
            String xml = DriverManager.getDriver().getPageSource();
            Allure.addAttachment("Page source khi fail (XML)", "text/xml",
                    new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)), ".xml");
        } catch (Exception e) {
            System.out.println("[PageSource] Khong lay duoc page source: " + e.getMessage());
        }
    }

    // =====================================================================
    // Quay video màn hình (bật/tắt bằng record.video trong config)
    // =====================================================================

    private static boolean isVideoEnabled() {
        return Boolean.parseBoolean(ConfigReader.get("record.video", "false"));
    }

    /** Bắt đầu quay màn hình device (Android giới hạn tối đa 3 phút/lần quay) */
    private void startVideoRecording() {
        if (!isVideoEnabled() || DriverManager.getDriver() == null) {
            return;
        }
        try {
            DriverManager.getDriver().startRecordingScreen(
                    new AndroidStartScreenRecordingOptions()
                            .withTimeLimit(Duration.ofMinutes(3))
                            .withBitRate(2_000_000));          // 2 Mbps: đủ nét, file nhỏ
        } catch (Exception e) {
            System.out.println("[Video] Khong bat dau quay duoc: " + e.getMessage());
        }
    }

    /** Dừng quay; nếu test fail thì đính kèm video vào Allure (test pass thì bỏ để report nhẹ) */
    private void stopVideoRecording(boolean attach) {
        if (!isVideoEnabled() || DriverManager.getDriver() == null) {
            return;
        }
        try {
            String base64 = DriverManager.getDriver().stopRecordingScreen();
            if (attach && base64 != null && !base64.isEmpty()) {
                byte[] video = Base64.getMimeDecoder().decode(base64);
                Allure.addAttachment("Video khi fail", "video/mp4", new ByteArrayInputStream(video), ".mp4");
            }
        } catch (Exception e) {
            System.out.println("[Video] Khong lay duoc video: " + e.getMessage());
        }
    }

    /** Ghi thông tin môi trường -> hiện ở mục "Environment" trên trang Overview của Allure (ghi 1 lần) */
    private static synchronized void writeAllureEnvironment(AndroidDriver driver) {
        if (environmentWritten || driver == null) {
            return;
        }
        try {
            Map<String, Object> caps = driver.getCapabilities().asMap();
            String content = String.join("\n",
                    "Environment=" + System.getProperty("env", "dev"),
                    "Suite=" + System.getProperty("suite", "(chạy từ IDE)"),
                    "Platform=Android " + caps.getOrDefault("appium:platformVersion", caps.get("platformVersion")),
                    "Device=" + caps.getOrDefault("appium:deviceModel", caps.get("deviceModel")),
                    "Device.Name=" + caps.getOrDefault("appium:deviceName", caps.get("deviceName")),
                    "App.Package=" + ConfigReader.get("app.package", ""),
                    "Appium.URL=" + ConfigReader.get("appium.url", ""),
                    "Java=" + System.getProperty("java.version"));
            Files.createDirectories(ALLURE_RESULTS);
            Files.writeString(ALLURE_RESULTS.resolve("environment.properties"), content, StandardCharsets.UTF_8);
            environmentWritten = true;
        } catch (Exception e) {
            System.out.println("[Allure] Khong ghi duoc environment: " + e.getMessage());
        }
    }
}
