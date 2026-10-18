package com.tu.core;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Tự đóng popup hệ thống "... isn't responding" (ANR) của Android.
 *
 * Popup này hay xuất hiện trên emulator CI vừa boot xong (vd: "Pixel Launcher isn't responding"),
 * che mất màn hình app -> test không tìm thấy element.
 * Cách xử lý: bấm "Wait" để hệ thống chạy tiếp (không tắt app nào).
 */
public final class SystemDialogHandler {

    private static final By ANR_TITLE = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/alertTitle\").textContains(\"isn't responding\")");
    private static final By ANR_WAIT_BY_ID   = AppiumBy.id("android:id/aerr_wait");
    private static final By ANR_WAIT_BY_TEXT = AppiumBy.androidUIAutomator("new UiSelector().text(\"Wait\")");

    private SystemDialogHandler() { }

    /**
     * Đóng tất cả popup ANR đang hiện (tối đa 3 popup chồng nhau).
     * @return true nếu đã đóng ít nhất 1 popup
     */
    public static boolean dismissAnrDialogs(AndroidDriver driver) {
        if (driver == null) {
            return false;
        }
        boolean dismissed = false;
        try {
            // Framework không dùng implicit wait (mặc định = 0) -> findElements trả về ngay, không làm chậm test
            for (int i = 0; i < 3; i++) {
                List<WebElement> titles = driver.findElements(ANR_TITLE);
                if (titles.isEmpty()) {
                    break;
                }
                System.out.println("[SystemDialog] Phat hien popup: '" + titles.get(0).getText() + "' -> bam Wait");
                List<WebElement> waitBtn = driver.findElements(ANR_WAIT_BY_ID);
                if (waitBtn.isEmpty()) {
                    waitBtn = driver.findElements(ANR_WAIT_BY_TEXT);
                }
                if (waitBtn.isEmpty()) {
                    break;
                }
                waitBtn.get(0).click();
                dismissed = true;
                sleep(1000);
            }
        } catch (Exception e) {
            System.out.println("[SystemDialog] Khong dong duoc popup: " + e.getMessage());
        }
        return dismissed;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
