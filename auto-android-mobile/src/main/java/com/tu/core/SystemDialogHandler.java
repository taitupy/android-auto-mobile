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
 * che mất màn hình app -> test không tìm thấy element, hoặc đọc nhầm tiêu đề
 * (popup ANR dùng chung id android:id/alertTitle với các popup khác).
 *
 * Nhận diện popup:
 *   1. Nút "Wait" có id android:id/aerr_wait  -> chỉ popup ANR mới có, không phụ thuộc ngôn ngữ/ký tự
 *   2. Dự phòng: tiêu đề chứa chữ "responding" (Android 14 viết "isn’t" bằng dấu nháy cong ’
 *      nên KHÔNG tìm theo "isn't")
 * Cách xử lý: bấm "Wait" để hệ thống chạy tiếp (không tắt app nào).
 */
public final class SystemDialogHandler {

    private static final By ANR_WAIT_BY_ID = AppiumBy.id("android:id/aerr_wait");
    private static final By ANR_TITLE = AppiumBy.androidUIAutomator(
            "new UiSelector().resourceId(\"android:id/alertTitle\").textContains(\"responding\")");
    private static final By WAIT_BY_TEXT = AppiumBy.androidUIAutomator("new UiSelector().text(\"Wait\")");

    private SystemDialogHandler() { }

    /**
     * Đóng tất cả popup ANR đang hiện (tối đa 3 popup chồng nhau).
     * Framework không dùng implicit wait (= 0) nên mỗi lần tìm trả về ngay, không làm chậm test.
     *
     * @return true nếu đã đóng ít nhất 1 popup
     */
    public static boolean dismissAnrDialogs(AndroidDriver driver) {
        if (driver == null) {
            return false;
        }
        boolean dismissed = false;
        try {
            for (int i = 0; i < 3; i++) {
                WebElement waitButton = findWaitButton(driver);
                if (waitButton == null) {
                    break;
                }
                System.out.println("[SystemDialog] Phat hien popup ANR (isn't responding) -> bam Wait");
                waitButton.click();
                dismissed = true;
                sleep(1500);
            }
        } catch (Exception e) {
            System.out.println("[SystemDialog] Khong dong duoc popup: " + e.getMessage());
        }
        return dismissed;
    }

    private static WebElement findWaitButton(AndroidDriver driver) {
        List<WebElement> byId = driver.findElements(ANR_WAIT_BY_ID);
        if (!byId.isEmpty()) {
            return byId.get(0);
        }
        // Dự phòng: chỉ bấm "Wait" khi chắc chắn đang có popup "... responding"
        if (!driver.findElements(ANR_TITLE).isEmpty()) {
            List<WebElement> byText = driver.findElements(WAIT_BY_TEXT);
            if (!byText.isEmpty()) {
                return byText.get(0);
            }
        }
        return null;
    }

    private static void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }
}
