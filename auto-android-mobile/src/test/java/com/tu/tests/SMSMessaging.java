package com.tu.tests;

import com.tu.base.BaseTest;
import com.tu.pages.HomePage;
import com.tu.pages.SMSMessagingPage;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

/**
 * Màn hình: Home > OS > SMS Messaging
 * Nguyên tắc verify nhập liệu: NHẬP vào -> ĐỌC LẠI giá trị trên ô -> SO SÁNH với giá trị đã nhập.
 * Không bấm nút Send (tránh gửi SMS thật).
 */
@Epic("ApiDemos")
@Feature("OS > SMS Messaging")
public class SMSMessaging extends BaseTest {

    private SMSMessagingPage openSmsMessagingPage() {
        return new HomePage().openOS().openSmsMessaging();
    }

    // =====================================================================
    // Giao diện
    // =====================================================================

    @Story("Giao diện")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "TC01 - Hiển thị đủ checkbox, 2 ô nhập và nút Send")
    public void verifySmsMessagingDisplayed() {
        SMSMessagingPage page = openSmsMessagingPage();
        SoftAssert soft = new SoftAssert();

        soft.assertTrue(page.isSmsReceiverCheckboxDisplayed(), "Không thấy checkbox Enable SMS broadcast receiver");
        soft.assertEquals(page.getSmsReceiverCheckboxText(), "Enable SMS broadcast receiver");

        soft.assertTrue(page.isRecipientLabelDisplayed(), "Không thấy nhãn Recipient #");
        soft.assertEquals(page.getRecipientLabel(), "Recipient #");
        soft.assertTrue(page.isRecipientInputDisplayed(), "Không thấy ô nhập Recipient");

        soft.assertTrue(page.isMessageLabelDisplayed(), "Không thấy nhãn Message Body");
        soft.assertEquals(page.getMessageLabel(), "Message Body");
        soft.assertTrue(page.isMessageInputDisplayed(), "Không thấy ô nhập Message Body");

        soft.assertTrue(page.isSendButtonDisplayed(), "Không thấy nút Send");
        soft.assertTrue(page.isSendButtonEnabled(), "Nút Send bị disable");

        soft.assertAll();   // BẮT BUỘC: thiếu dòng này test luôn pass dù có điểm sai
    }

    // =====================================================================
    // Nhập liệu
    // =====================================================================

    @Story("Nhập Recipient")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "TC02 - Nhập số điện thoại vào Recipient")
    public void verifyEnterRecipient() {
        String phone = "0901234567";

        SMSMessagingPage page = openSmsMessagingPage().enterRecipient(phone);

        Assert.assertEquals(page.getRecipient(), phone, "Recipient hiển thị sai giá trị đã nhập");
    }

    @Story("Nhập Message Body")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "TC03 - Nhập Message Body có tiếng Việt và ký tự đặc biệt")
    public void verifyEnterMessage() {
        String message = "Xin chào OCB! Test 123 @#$%";

        SMSMessagingPage page = openSmsMessagingPage().enterMessage(message);

        Assert.assertEquals(page.getMessage(), message, "Message Body hiển thị sai giá trị đã nhập");
    }

    @Story("Nhập Recipient + Message Body")
    @Severity(SeverityLevel.NORMAL)
    @Test(description = "TC04 - Nhập cả 2 ô, giá trị không bị lẫn sang nhau")
    public void verifyEnterBothFields() {
        String phone = "0987654321";
        String message = "Hello Appium";

        SMSMessagingPage page = openSmsMessagingPage()
                .enterRecipient(phone)
                .enterMessage(message);

        SoftAssert soft = new SoftAssert();
        soft.assertEquals(page.getRecipient(), phone, "Recipient bị thay đổi sau khi nhập Message Body");
        soft.assertEquals(page.getMessage(), message, "Message Body sai");
        soft.assertAll();
    }

    @Story("Nhập Recipient")
    @Severity(SeverityLevel.NORMAL)
    @Test(description = "TC05 - Nhập lại thì giá trị mới thay thế giá trị cũ")
    public void verifyReEnterReplacesOldValue() {
        SMSMessagingPage page = openSmsMessagingPage()
                .enterRecipient("0111111111")
                .enterRecipient("0222222222");

        Assert.assertEquals(page.getRecipient(), "0222222222", "Giá trị cũ không bị thay thế");
    }

    @Story("Nhập Recipient")
    @Severity(SeverityLevel.MINOR)
    @Test(description = "TC06 - Xóa nội dung Recipient")
    public void verifyClearRecipient() {
        SMSMessagingPage page = openSmsMessagingPage()
                .enterRecipient("0901234567")
                .clearRecipient();

        // Ô trống có thể trả về chữ gợi ý (hint) thay vì "" -> chỉ kiểm tra giá trị cũ đã mất
        Assert.assertNotEquals(page.getRecipient(), "0901234567", "Recipient chưa được xóa");
    }

    @DataProvider(name = "messages")
    public Object[][] messages() {
        return new Object[][] {
                {"Ngắn",              "Hi"},
                {"Tiếng Việt có dấu", "Chúc mừng bạn nhận được 100 điểm thưởng"},
                {"Ký tự đặc biệt",    "!@#$%^&*()_+-="},
                {"Có số",             "OTP của bạn là 123456"},
        };
    }

    @Story("Nhập Message Body")
    @Severity(SeverityLevel.NORMAL)
    @Test(dataProvider = "messages", description = "TC07 - Nhập Message Body với nhiều loại dữ liệu")
    public void verifyMessageWithDifferentData(String caseName, String message) {
        SMSMessagingPage page = openSmsMessagingPage().enterMessage(message);

        Assert.assertEquals(page.getMessage(), message, "Case [" + caseName + "] hiển thị sai");
    }

    // =====================================================================
    // Checkbox
    // =====================================================================

    @Story("Checkbox SMS receiver")
    @Severity(SeverityLevel.NORMAL)
    @Test(description = "TC08 - Checkbox mặc định chưa tick, bấm để tick, bấm lại để bỏ tick")
    public void verifySmsReceiverCheckbox() {
        SMSMessagingPage page = openSmsMessagingPage();

        Assert.assertFalse(page.isSmsReceiverChecked(), "Mặc định checkbox phải chưa tick");

        page.tapSmsReceiverCheckbox();
        Assert.assertTrue(page.isSmsReceiverChecked(), "Bấm 1 lần -> checkbox phải được tick");

        page.tapSmsReceiverCheckbox();
        Assert.assertFalse(page.isSmsReceiverChecked(), "Bấm lần 2 -> checkbox phải bỏ tick");
    }
}
