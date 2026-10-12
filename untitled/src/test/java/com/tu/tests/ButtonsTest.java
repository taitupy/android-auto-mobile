package com.tu.tests;

import com.tu.base.BaseTest;
import com.tu.pages.ButtonsPage;
import com.tu.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class ButtonsTest extends BaseTest {

    private ButtonsPage openButtonsPage() {
        return new HomePage().openViews().openButtons();
    }

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

    @Test(description = "TC02 - Nút Normal và Small bấm được")
    public void verifyButtonsClickable() {
        ButtonsPage page = openButtonsPage();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(page.isNormalClickable(), "Nút Normal không clickable");
        soft.assertTrue(page.isSmallClickable(), "Nút Small không clickable");

        soft.assertAll();
    }

    @Test(description = "TC03 - Nút Small có chiều cao nhỏ hơn nút Normal")
    public void verifySmallButtonSize() {
        ButtonsPage page = openButtonsPage();

        int normal = page.getNormalHeight();
        int small  = page.getSmallHeight();

        Assert.assertFalse(small < normal,
                "Small (" + small + "px) phải thấp hơn Normal (" + normal + "px)");
    }

    @Test(description = "TC04 - Toggle mặc định là OFF")
    public void verifyToggleDefaultOff() {
        ButtonsPage page = openButtonsPage();
        SoftAssert soft = new SoftAssert();

        soft.assertEquals(page.getToggleClass(), "android.widget.ToggleButton");
        soft.assertEquals(page.getToggleText(), "OFF");
        soft.assertFalse(page.isToggleOn(), "Toggle phải đang tắt");

        soft.assertAll();
    }

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