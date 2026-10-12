package com.tu.tests;

import com.tu.base.BaseTest;
import com.tu.pages.HomePage;
import com.tu.pages.PreferenceDependenciesPage;
import com.tu.pages.WifiSettingsDialog;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WifiSettingPOM extends BaseTest {

    @Test(description = "Đặt tên WiFi thành công")
    public void setWifiName() {
        String wifiName = "Tu WiFi";

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