package com.tu.core;

import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.io.File;
import java.net.URI;
import java.time.Duration;

public class DriverFactory {

    public static AndroidDriver createAndroidDriver() throws Exception {
        String appiumUrl = ConfigReader.get("appium.url", "http://127.0.0.1:4723");

        UiAutomator2Options options = new UiAutomator2Options()
                .setDeviceName(ConfigReader.get("device.name", "emulator-5554"))
                .setAppPackage(ConfigReader.get("app.package", "io.appium.android.apis"))
                .setAppActivity(ConfigReader.get("app.activity", ".ApiDemos"))
                .setNewCommandTimeout(Duration.ofSeconds(ConfigReader.getInt("timeout.new.command", 120)))
                // Emulator trên CI chạy chậm hơn máy thật -> tăng timeout cài UiAutomator2 server và lệnh adb
                .setUiautomator2ServerInstallTimeout(Duration.ofSeconds(ConfigReader.getInt("timeout.uia2.install", 120)))
                .setAdbExecTimeout(Duration.ofSeconds(ConfigReader.getInt("timeout.adb.exec", 60)));

        // Chỉ định chính xác device khi có nhiều máy cùng cắm (chạy song song)
        String udid = ConfigReader.get("device.udid", "");
        if (!udid.isBlank()) {
            options.setUdid(udid);
        }

        // Có file apk -> Appium tự cài app (bắt buộc trên CI vì emulator mới tinh chưa có app)
        // Không có file apk -> dùng app đã cài sẵn trên device (như cách chạy trước đây)
        String appPath = ConfigReader.get("app.path", "");
        if (!appPath.isBlank()) {
            File apk = new File(appPath);
            if (!apk.isAbsolute()) {
                apk = new File(System.getProperty("user.dir"), appPath);
            }
            if (apk.exists()) {
                options.setApp(apk.getCanonicalPath());
            } else {
                System.out.println("[DriverFactory] Khong thay apk tai " + apk.getAbsolutePath()
                        + " -> dung app da cai san tren device");
            }
        }

        return new AndroidDriver(URI.create(appiumUrl).toURL(), options);
    }
}
