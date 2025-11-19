package com.oms.pages.Melbourne;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import com.oms.utilities.AssertionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import java.util.List;
import static com.oms.base.BaseClass.prop;

public class BannerMessagePage {

    private ActionDriver actionDriver;

    public BannerMessagePage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    //  Locators
    private By melbourneDropdown = By.xpath("(//a[@class='dropdown-toggle'])[1]");
    private By bannerMessagesButton = By.xpath("//a[@href='/oms/companyInformationCntrl']");
    private By bannerMessagePageHeader = By.xpath("//h4[normalize-space()='Display messages via banner']");
    private By bannerMessagePageHeader_Unauthrised_users = By.xpath("//div[@class='text-center']");
    private By newButton = By.xpath("//button[@id='addRowBtn']");
    private By bannerMessageTitle = By.xpath("(//textarea[@class='form-control info-textarea changeCaps'])[1]/preceding::textarea[@class='form-control title-textarea changeCaps'][1]");
    private By bannerMessageInformation = By.xpath("(//textarea[@class='form-control info-textarea changeCaps'])[2]/preceding::textarea[@class='form-control info-textarea changeCaps'][1]");
    private By categoryDropdown = By.xpath("//span[@title='Select category']");
    private By categoryOptions = By.xpath("//ul[@id='select2-knkd-results' or @class='select2-results__options']/li");
    private By publishButton = By.xpath("//button[@class='btn btn-sm save-btn']");
    private By popupDisplayed = By.xpath("//h4[normalize-space()='Important message']");
    private By popupTitleInPopup = By.cssSelector("div[id='companyInformationModalBody'] div h5");
    private By popupInfoInPopup = By.cssSelector("div[id='companyInformationModalBody'] div div p");
    private By understoodButton = By.xpath("(//button[@id='companyInfoFooterBtn'])[1]");
    private By clickInactiveButton = By.xpath("(//button[@data-active='true'])[1]");
    private By popupNoButton = By.xpath("//div[contains(@class,'modal-footer')]/button[@data-bb-handler='cancel' and contains(@class,'btn-danger')]");
    private By activeButton = By.xpath("//table[@class='table table-bordered table-secondary']/descendant::tr[@data-id='6912c593ffee874bb475936f']/descendant::button[@data-original-title='Click to make active']");
    private By deleteButton = By.xpath("(//button[@class='btn btn-sm btn-danger delete-btn'])[1]");
    private By inactiveBannerTitle = By.xpath("//table[@class='table table-bordered table-secondary']/descendant::textarea[@class='form-control title-textarea changeCaps'][text()='How to Access to users']");
    private By inactiveBannerInfo = By.xpath("//table[@class='table table-bordered table-secondary']/descendant::textarea[@class='form-control info-textarea changeCaps'][text()='Refer the FAQ Document clause 12']");

    // ================= Actions ==================

    public void clickMelbourneDropdown() {
        actionDriver.click(melbourneDropdown);
    }

    public void clickBannerMessagesButton() {
        actionDriver.click(bannerMessagesButton);
    }

    public void BannerMessagePageForAllUsers() {
        String header = actionDriver.getText(bannerMessagePageHeader);
        AssertionUtils.hardAssertEquals(header, "Display messages via banner", "Banner message page title mismatch!");
    }

    public void BannerMessagePageDisplayedForUnauthorizedUsers() {
        String header = actionDriver.getText(bannerMessagePageHeader_Unauthrised_users);
        String expected = "Note: Only the Admin (SUSUPER) / Productivity / Account / User manager / System users are allowed to make changes to this page.";
        AssertionUtils.hardAssertEquals(header, expected, "Unauthorized user message mismatch!");
    }

    public void clickNewButton() {
        actionDriver.click(newButton);
    }

    public void setBannerMessageTitle(String title) {
        actionDriver.clearText(bannerMessageTitle);
        actionDriver.sendKeysWithActions(bannerMessageTitle, title);
    }

    public void setBannerMessageInformation(String info) {
        actionDriver.clearText(bannerMessageInformation);
        actionDriver.sendKeysWithActions(bannerMessageInformation, info);
    }

    public void selectBannerMessageCategory(String categoryName) {
        actionDriver.click(categoryDropdown);
        List<String> options = actionDriver.getDropdownOptions(categoryOptions);
        for (String option : options) {
            if (option.equalsIgnoreCase(categoryName)) {
                actionDriver.click(By.xpath("//ul[@id='select2-knkd-results' or @class='select2-results__options']/li[text()='" + option + "']"));
                break;
            }
        }
    }

    public void clickPublishButton() {
        actionDriver.click(publishButton);
    }

    public void verifyPopupDisplayed() {
        AssertionUtils.softAssertTrue(actionDriver.isDisplayed(popupDisplayed), "Popup not displayed!");
    }

    public void verifyPopupTitle() {
        String title = actionDriver.getText(popupTitleInPopup);
        AssertionUtils.hardAssertEquals(title, prop.getProperty("Banner_Message_Title"), "Popup title mismatch!");
    }

    public void verifyPopupInformation() {
        String info = actionDriver.getText(popupInfoInPopup);
        AssertionUtils.hardAssertEquals(info, prop.getProperty("Banner_Message_Information"), "Popup information mismatch!");
    }

    public void clickUnderstoodButton() {
        actionDriver.click(understoodButton);
    }

    public void clickInactiveButton() {
        actionDriver.click(clickInactiveButton);
    }

    public void clickPopupNoButton() {
        actionDriver.click(popupNoButton);
    }

    public void clickPopupYesButton() {
        actionDriver.click(By.xpath("//div[contains(@class,'modal-footer')]/button[@data-bb-handler='confirm' and contains(@class,'btn-success')]"));
    }

    public void verifyInactiveBannerMessageTitle() {
        String title = actionDriver.getText(inactiveBannerTitle);
        AssertionUtils.hardAssertEquals(title, prop.getProperty("Banner_Message_Title"), "Inactive Banner message title mismatch!");
    }

    public void verifyInactiveBannerMessageInfo() {
        String info = actionDriver.getText(inactiveBannerInfo);
        AssertionUtils.hardAssertEquals(info, prop.getProperty("Banner_Message_Information"), "Inactive Banner message info mismatch!");
    }

    public void clickActiveButton() {
        actionDriver.click(activeButton);
    }

    public void deleteBannerMessage() {
        if (actionDriver.isDisplayed(deleteButton)) {
            actionDriver.click(deleteButton);
            clickPopupYesButton();
        }
    }}