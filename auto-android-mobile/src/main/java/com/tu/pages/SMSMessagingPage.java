package com.tu.pages;

import io.appium.java_client.AppiumBy;
import org.openqa.selenium.By;

/**
 * Màn hình: Home > OS > SMS Messaging
 *
 * Mỗi dòng nhập liệu gồm 2 element RIÊNG:
 *   Recipient #   [_______________]
 *   └ TextView    └ EditText
 *     (nhãn - chỉ đọc)   (ô nhập - sendKeys vào đây)
 *
 * Lưu ý: KHÔNG bấm nút Send trong test (màn này có thể gửi SMS thật nếu chạy trên điện thoại thật).
 */
public class SMSMessagingPage extends BasePage {

    // ===== Locators: nhãn (TextView) - dùng để verify UI =====
    private final By recipientLabel = AppiumBy.xpath("//android.widget.TextView[@content-desc=\"Recipient #\"]");
    private final By messageLabel   = AppiumBy.xpath("//android.widget.TextView[@content-desc=\"Message Body\"]");

    // ===== Locators: ô nhập (EditText) + checkbox + nút =====
    private final By smsReceiverCheckbox = AppiumBy.id("io.appium.android.apis:id/sms_enable_receiver");
    private final By recipientInput      = AppiumBy.id("io.appium.android.apis:id/sms_recipient");
    private final By messageInput        = AppiumBy.id("io.appium.android.apis:id/sms_content");
    private final By sendButton          = AppiumBy.id("io.appium.android.apis:id/sms_send_message");

    // =====================================================================
    // Actions
    // =====================================================================

    public SMSMessagingPage enterRecipient(String phone) {
        type(recipientInput, phone);
        hideKeyboard();
        return this;
    }

    public SMSMessagingPage enterMessage(String message) {
        type(messageInput, message);
        hideKeyboard();
        return this;
    }

    public SMSMessagingPage clearRecipient() {
        clear(recipientInput);
        return this;
    }

    public SMSMessagingPage clearMessage() {
        clear(messageInput);
        return this;
    }

    public SMSMessagingPage tapSmsReceiverCheckbox() {
        click(smsReceiverCheckbox);
        return this;
    }

    // =====================================================================
    // Get / verify: checkbox "Enable SMS broadcast receiver"
    // =====================================================================

    public boolean isSmsReceiverCheckboxDisplayed() { return waitVisible(smsReceiverCheckbox).isDisplayed(); }
    public String  getSmsReceiverCheckboxText()     { return getText(smsReceiverCheckbox); }
    public boolean isSmsReceiverChecked()           { return isChecked(smsReceiverCheckbox); }

    // =====================================================================
    // Get / verify: Recipient #
    // =====================================================================

    public boolean isRecipientLabelDisplayed() { return waitVisible(recipientLabel).isDisplayed(); }
    public String  getRecipientLabel()         { return getText(recipientLabel); }
    public boolean isRecipientInputDisplayed() { return waitVisible(recipientInput).isDisplayed(); }
    public String  getRecipient()              { return getText(recipientInput); }

    // =====================================================================
    // Get / verify: Message Body
    // =====================================================================

    public boolean isMessageLabelDisplayed() { return waitVisible(messageLabel).isDisplayed(); }
    public String  getMessageLabel()         { return getText(messageLabel); }
    public boolean isMessageInputDisplayed() { return waitVisible(messageInput).isDisplayed(); }
    public String  getMessage()              { return getText(messageInput); }

    // =====================================================================
    // Get / verify: nút Send (chỉ kiểm tra, không bấm)
    // =====================================================================

    public boolean isSendButtonDisplayed() { return waitVisible(sendButton).isDisplayed(); }
    public boolean isSendButtonEnabled()   { return isEnabled(sendButton); }
}
