package com.oms.pages.Melbourne;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import com.oms.utilities.AssertionUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import static com.oms.actiondriver.ActionDriver.logger;
import static com.oms.base.BaseClass.getTestEnv;

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
    private By PopupYesButton = By.xpath("//div[contains(@class,'modal-footer')]/button[@data-bb-handler='confirm' and contains(@class,'btn-success')]");
    private By activeButton = By.xpath("//table[@class='table table-bordered table-secondary']/descendant::tr[@data-id='6912c593ffee874bb475936f']/descendant::button[@data-original-title='Click to make active']");
    private By deleteButtonActivePopup = By.xpath("(//button[@class='btn btn-sm btn-danger delete-btn'])[1]");
    private By inactiveBannerTitle = By.xpath("//table[@class='table table-bordered table-secondary']/descendant::textarea[@class='form-control title-textarea changeCaps'][text()='How to Access to users']");
    private By inactiveBannerInfo = By.xpath("//table[@class='table table-bordered table-secondary']/descendant::textarea[@class='form-control info-textarea changeCaps'][text()='Refer the FAQ Document clause 12']");
    private By inactiveBannerActiveButton = By.xpath("//table[@class=\"table table-bordered table-secondary\"]/descendant::tr[starts-with(@data-id, '6')] /descendant::button[@data-original-title=\"Click to make active\"]");
    private By inactivebannerpopup = By.xpath("//div[@class='bootbox modal fade bootbox-confirm in']/descendant::div[@class='modal-content']");
    private By BannerMessageTabPresence= By.xpath("//table[@id='activeTable']");

    //private By
        //========Popup locators===============//

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
        String actual = actionDriver.getText(bannerMessagePageHeader_Unauthrised_users);
        String expected = "Note: Only the Admin (SUSUPER) / Productivity / Account / User manager / System users are allowed to make changes to this page.";
        AssertionUtils.hardAssertEquals(actual, expected, "Unauthorized user message mismatch!");
    }

    public void clickNewButton() {
        actionDriver.click(newButton);
    }

    public void setBannerMessageTitle() {
        actionDriver.sendKeysWithActions(bannerMessageTitle, getTestEnv().getProperty("BannerMessageTitle"));
    }

    public void setBannerMessageInformation()
    {
        actionDriver.sendKeysWithActions(bannerMessageInformation, getTestEnv().getProperty("BannerMessageInformation"));
    }

    public void selectBannerMessageCategory()
    {
        actionDriver.selectBootstrapDropdownOption(categoryDropdown,categoryOptions, getTestEnv().getProperty("CategoryName"));
    }

    public void clickPublishButton() {
        actionDriver.clickUsingJS(publishButton);
    }

    public void verifyPopupDisplayed() {
        AssertionUtils.softAssertTrue(actionDriver.isDisplayed(popupDisplayed), "Popup not displayed!");
    }

    public void verifyPopupTitle() {
     String title =   actionDriver.getText(popupTitleInPopup);
        AssertionUtils.softAssertEquals(title, getTestEnv().getProperty("BannerMessageTitle"), "Popup title mismatch!");
    }

    public void verifyPopupInformation() {
        String info = actionDriver.getText(popupInfoInPopup);
        AssertionUtils.softAssertEquals(info, getTestEnv().getProperty("BannerMessageInformation"), "Popup information mismatch!");
    }

    public void clickUnderstoodButton() {
        actionDriver.moveToElement(understoodButton);
        actionDriver.click(understoodButton);
    }

    public void clickInactiveButton() {
        actionDriver.click(clickInactiveButton);
    }

    public void clickPopupNoButton() {
        if(actionDriver.isDisplayed(popupNoButton))
        {
            actionDriver.moveToElement(popupNoButton);
            actionDriver.click(popupNoButton);
        }
    }

    public void clickPopupYesButton()
    {
        actionDriver.moveToElement(PopupYesButton);
        actionDriver.clickUsingJS(PopupYesButton);
    }

    public void verifyInactiveBannerMessageTitle() {
        String title = actionDriver.getText(inactiveBannerTitle);
        AssertionUtils.softAssertEquals(title, getTestEnv().getProperty("BannerMessageTitle"), "Inactive Banner message title mismatch!");
    }

    public void verifyInactiveBannerMessageInfo() {
        String info = actionDriver.getText(inactiveBannerInfo);
        AssertionUtils.softAssertEquals(info, getTestEnv().getProperty("BannerMessageInformation"), "Inactive Banner message info mismatch!");
    }

    public void InactiveBannerMessageActiveButton() {
        actionDriver.click(inactiveBannerActiveButton);
    }

    public void InactiveBannerPopup(){
        actionDriver.clickUsingJS(inactiveBannerActiveButton);
        actionDriver.isDisplayed(inactivebannerpopup);
    }
    // Improved method to handle the Yes button with better waiting logic
    public void InactiveBannerPopupYesButton() {
        try {
            // 1. Wait specifically for the YES button to be visible
            // This uses the WebDriverWait from ActionDriver indirectly via waitForElementToBeVisible logic
            // locator: //button[normalize-space()='Yes']

            // 2. Scroll to it to ensure it's in view (handling modal overlays)
            actionDriver.scrollToElement(PopupYesButton);

            // 3. Use JS Click because modal buttons are often intercepted by their own containers
            actionDriver.clickUsingJS(PopupYesButton);

            logger.info("Successfully clicked Popup YES button.");
        } catch (Exception e) {
            logger.error("Failed to click Popup YES button: " + e.getMessage());
            // Optional: Add a hard fail here if this button is critical for the test flow
            throw e;
        }
    }
    /*
    public void deleteActiveBannerMessage() {

        // First check if delete button is displayed and click it
        if (actionDriver.isDisplayed(deleteButtonActivePopup)) {
            actionDriver.clickUsingJS(deleteButtonActivePopup);
            logger.info("Delete button clicked in active popup.");
        }

        // After clicking delete, confirm by clicking YES button

    }
*/
    public void deleteActiveBannerMessage() {
        // 1. Click Delete/Toggle Status
        if (actionDriver.isDisplayed(deleteButtonActivePopup)) { // or inactiveBannerActiveButton
            actionDriver.clickUsingJS(deleteButtonActivePopup);
            logger.info("Delete/Toggle button clicked.");

            // Small static wait might be necessary if the animation is very slow
            // (Only use if explicit wait fails)
            // try { Thread.sleep(500); } catch (InterruptedException e) {}
        }

        // 2. Click Yes
        // We don't need to check isDisplayed inside the 'if' because clickPopupYesButton
        // handles the wait logic internally now.
        clickPopupYesButton();
    }
    // Make this PUBLIC so Test07 can call it
    public void createBannerMessage() {
        // 1. Enter Details
        actionDriver.click(newButton);
        actionDriver.sendKeysWithActions(bannerMessageTitle, getTestEnv().getProperty("BannerMessageTitle"));
        actionDriver.sendKeysWithActions(bannerMessageInformation, getTestEnv().getProperty("BannerMessageInformation"));
        actionDriver.selectBootstrapDropdownOption(categoryDropdown, categoryOptions, getTestEnv().getProperty("CategoryName"));

        // 2. Click Publish
        actionDriver.clickUsingJS(publishButton);

        // 3. SMART HANDLER: Check for "Deactivate Previous" Popup
        // If a banner already exists, this popup appears. We must accept it.
        try {
            // Wait briefly for popup (using a short implicit check via isDisplayed)
            if (actionDriver.isDisplayed(PopupYesButton)) {
                logger.info("Confirmation Popup displayed (Deactivating old banner). Clicking Yes...");
                clickPopupYesButton(); // Re-use your existing robust method
            }
        } catch (Exception e) {
            logger.info("No confirmation popup appeared (First banner creation).");
        }

        logger.info("Banner creation process completed.");
    }

    // Update BannerMessagetab to use this helper method
    public void BannerMessagetab() {
        if (actionDriver.isDisplayed(BannerMessageTabPresence)) {
            // Case A: Tab exists (1 banner active). Create 1 new one (triggers popup).
            createBannerMessage();
        } else {
            // Case B: No active banners. Create 2 banners to force the "Only One Active" scenario.
            createBannerMessage(); // No popup
            createBannerMessage(); // Triggers popup
        }
    }
}