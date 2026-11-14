package com.tu.tests;

import com.tu.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import com.tu.pages.HomePage;
import com.tu.pages.PreferenceDependenciesPage;
import com.tu.pages.WifiSettingsDialog;
import com.tu.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ApiDemos")
@Feature("Preference > WiFi settings")
public class WifiSettingPOM extends BaseTest {

    @Story("Đặt tên WiFi")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Đặt tên WiFi thành công")
    public void setWifiName() {
        String wifiName = TestDataReader.getString("wifi.json", "wifiName");   // đọc từ testdata/wifi.json

        // 1-3. Home -> Preference -> Preference dependencies -> tick WiFi
        PreferenceDependenciesPage page = new HomePage()
                .openPreference()
                .openPreferenceDependencies()
                .enableWifi();

        // 4. Mở popup WiFi settings
        WifiSettingsDialog dialog = page.openWifiSettings();

        // 5. Kiểm tra tiêu đề popup
        Assert.assertEquals(dialog.getTitle(), "WiFi settings");

        // 6. Nhập tên WiFi và bấm OK
        dialog.enterWifiName(wifiName).clickOk();

        // 7. Mở lại popup để kiểm tra giá trị đã được lưu
        String savedName = page.openWifiSettings().getWifiName();
        Assert.assertEquals(savedName, wifiName);
    }
}