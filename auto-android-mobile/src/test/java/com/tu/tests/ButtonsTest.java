package com.tu.tests;

import com.tu.base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import com.tu.pages.ButtonsPage;
import com.tu.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

@Epic("ApiDemos")
@Feature("Views > Buttons")
public class ButtonsTest extends BaseTest {

    private ButtonsPage openButtonsPage() {
        return new HomePage().openViews().openButtons();
    }

    @Story("Hiển thị nút")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "TC01 - Hiển thị đủ 3 nút với text đúng")
    public void verifyButtonsDisplayed() {
        ButtonsPage page = openButtonsPage();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(page.isNormalDisplayed(), "Không thấy nút Normal");
        soft.assertEquals(page.getNormalText(), "Normal");

        soft.assertTrue(page.isSmallDisplayed(), "Không thấy nút Small");
        soft.assertEquals(page.getSmallText(), "Small");

        soft.assertTrue(page.isToggleDisplayed(), "Không thấy nút Toggle");
        soft.assertEquals(page.getToggleText(), "OFF");

        soft.assertAll();
    }

    @Story("Trạng thái nút")
    @Severity(SeverityLevel.NORMAL)
    @Test(description = "TC02 - Nút Normal và Small bấm được")
    public void verifyButtonsClickable() {
        ButtonsPage page = openButtonsPage();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(page.isNormalClickable(), "Nút Normal không clickable");
        soft.assertTrue(page.isSmallClickable(), "Nút Small không clickable");

        soft.assertAll();
    }

    @Story("Kích thước nút")
    @Severity(SeverityLevel.MINOR)
    @Test(description = "TC03 - Nút Small có chiều cao nhỏ hơn nút Normal")
    public void verifySmallButtonSize() {
        ButtonsPage page = openButtonsPage();

        int normal = page.getNormalHeight();
        int small  = page.getSmallHeight();

        Assert.assertTrue(small < normal,
                "Small (" + small + "px) phải thấp hơn Normal (" + normal + "px)");
    }

    @Story("Bật/tắt Toggle")
    @Severity(SeverityLevel.NORMAL)
    @Test(description = "TC04 - Toggle mặc định là OFF")
    public void verifyToggleDefaultOff() {
        ButtonsPage page = openButtonsPage();
        SoftAssert soft = new SoftAssert();

        soft.assertEquals(page.getToggleClass(), "android.widget.ToggleButton");
        soft.assertEquals(page.getToggleText(), "OFF");
        soft.assertFalse(page.isToggleOn(), "Toggle phải đang tắt");

        soft.assertAll();
    }

    @Story("Bật/tắt Toggle")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "TC05 - Bấm toggle: OFF → ON → OFF")
    public void verifyToggleOnOff() {
        ButtonsPage page = openButtonsPage();

        // Bấm lần 1: OFF -> ON
        page.tapToggle();
        Assert.assertEquals(page.getToggleText(), "ON");
        Assert.assertTrue(page.isToggleOn(), "Toggle phải đang bật");

        // Bấm lần 2: ON -> OFF
        page.tapToggle();
        Assert.assertEquals(page.getToggleText(), "OFF");
        Assert.assertFalse(page.isToggleOn(), "Toggle phải đã tắt");
    }
}