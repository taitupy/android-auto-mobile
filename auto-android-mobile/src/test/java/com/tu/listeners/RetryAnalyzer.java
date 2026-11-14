package com.tu.listeners;

import com.tu.core.ConfigReader;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Chạy lại test bị fail tối đa N lần (N = retry.count trong config).
 *   - dev (máy cá nhân): 0 -> không chạy lại, thấy lỗi ngay để sửa
 *   - staging (CI)     : 1 -> chống flaky do emulator CI chậm / popup hệ thống
 * Allure hiện các lần chạy lại ở tab "Retries" của test.
 */
public class RetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRY = ConfigReader.getInt("retry.count", 0);
    private int count = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (count < MAX_RETRY) {
            count++;
            System.out.println("[Retry] Chay lai " + result.getMethod().getMethodName()
                    + " (lan " + count + "/" + MAX_RETRY + ")");
            return true;
        }
        return false;
    }
}
