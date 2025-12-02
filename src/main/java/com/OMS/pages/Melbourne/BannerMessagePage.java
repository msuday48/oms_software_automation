package com.oms.pages.Melbourne;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import com.oms.utilities.AssertionUtils;
import com.oms.utilities.ExceptionUtility;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import static com.oms.actiondriver.ActionDriver.logger;
import static com.oms.base.BaseClass.getDriver;
import static com.oms.base.BaseClass.getTestEnv;
import static java.lang.Thread.sleep;

public class BannerMessagePage {
    private ActionDriver actionDriver;

WebDriver driver=getDriver();

    // Constructor initializes ActionDriver from BaseClass
    public BannerMessagePage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    // Locators
    private By melbourneDropdown = By.xpath("(//a[@class='dropdown-toggle'])[1]");
    private By bannerMessagesButton = By.xpath("//a[@href='/oms/companyInformationCntrl']");
    private By bannerMessagePageHeader = By.xpath("//h4[normalize-space()='Display messages via banner' or @text()='Display messages via banner']");
    private By bannerMessagePageHeader_Unauthrised_users = By.xpath("//div[@class='text-center']");
    private By activeBanner = By.xpath("//td[normalize-space()='No data available']");
    private By getActiveBannerDelete=By.xpath("//tr[@data-active='true']//button[@class='btn btn-sm btn-danger delete-btn']");
    private By newButton = By.xpath("//button[@id='addRowBtn' or @text()='New']");
    private By bannerMessageTitle = By.xpath("(//textarea[@class='form-control info-textarea changeCaps'])[1]/preceding::textarea[@class='form-control title-textarea changeCaps'][1]");
    private By bannerMessageInformation = By.xpath("(//textarea[@class='form-control info-textarea changeCaps'])[2]/preceding::textarea[@class='form-control info-textarea changeCaps'][1]");
    private By categoryDropdown = By.xpath("//span[@title='Select category']");
    private By categoryOptions = By.xpath("//li[contains(@class,'select2-results__option') and text()='\\\" + visibleText + \\\"']");
    private  By categoryoption= By.xpath("//li[starts-with(@id, 'select2-') and contains(@id, 'Good news')]");
    private By publishButton = By.xpath("//button[@class='btn btn-sm save-btn']");
    private By popupDisplayed = By.xpath("//h4[normalize-space()='Important message' or  @text()='Important message']");
    private By popupTitleInPopup = By.cssSelector("div[id='companyInformationModalBody'] div h5");
    private By popupInfoInPopup = By.cssSelector("div[id='companyInformationModalBody'] div div p");
    private By understoodButton = By.xpath("(//button[@id='companyInfoFooterBtn'])[1]");
    private By ActiveBanneractiveButton = By.xpath("//button[@data-active='true' or  @data-original-title=\"Click to make inactive\"]");
    private By activeBannerPopup = By.xpath( "//div[@class='bootbox modal fade bootbox-confirm in']//div[@class='modal-content']");
    private By popupNoButton = By.xpath("//div[contains(@class,'modal-footer')]/button[@data-bb-handler='confirm' and contains(@class,'btn-success')]");
    private By PopupYesButton = By.xpath("//button[text()='Yes' and @data-bb-handler=\"confirm\"]");
    private By InactiveBannerTitle=By.xpath("//table[@class=\"table table-bordered table-secondary\"]/descendant::textarea[@class=\"form-control title-textarea changeCaps\"][text()=\"How to Access to users\"]");
    private By InactivebannerInformation = By.xpath("//table[@class=\"table table-bordered table-secondary\"]/descendant::textarea[@class=\"form-control info-textarea changeCaps\"][text()=\"Refer the FAQ Document clause 12\"]");
    private By inactiveBannerinctiveButton = By.xpath("//tr[.//textarea[contains(text(),'Refer the FAQ Document clause')]]//button[contains(@class,'toggle-status-btn')][1]");
    private By inactivebannerpopup = By.xpath("//div[@class='bootbox modal fade bootbox-confirm in']/descendant::div[@class='modal-content']");
    private By deleteButtonActivePopup = By.xpath("(//button[@class='btn btn-sm btn-danger delete-btn'])[1]");
    private By BannerMessageTabPresence = By.xpath("//table[@id='activeTable']");

    // Clicks Melbourne dropdown
    public void clickMelbourneDropdown() {
      //  actionDriver.click(melbourneDropdown);
        WebElement ele = driver.findElement(melbourneDropdown);
        ele.click();
    }

    // Navigates to Banner Messages page
    public void clickBannerMessagesButton() {
        actionDriver.click(bannerMessagesButton);
    }

    // Validates that banner message page header is displayed for authorized users
    public void BannerMessagePageForAllUsers() {
        String header = actionDriver.getText(bannerMessagePageHeader);
        AssertionUtils.softAssertEquals(header, "Display messages via banner", "Banner message page title mismatch!");
    }

    // Validates unauthorized user banner access message
    public void BannerMessagePageDisplayedForUnauthorizedUsers() {
        String actual = actionDriver.getText(bannerMessagePageHeader_Unauthrised_users);
        String expected = "Note: Only the Admin (SUSUPER) / Productivity / Account / User manager / System users are allowed to make changes to this page.";
        AssertionUtils.hardAssertEquals(actual, expected, "Unauthorized user message mismatch!");
    }

    // Clicks Add New message button
    public void clickNewButton() {
        actionDriver.click(newButton);
    }

    // Enters Banner Message Title
    public void setBannerMessageTitle() {
        actionDriver.sendKeysWithActions(bannerMessageTitle, getTestEnv().getProperty("BannerMessageTitle"));
    }

    // Enters Banner Message Description
    public void setBannerMessageInformation() {
        actionDriver.sendKeysWithActions(bannerMessageInformation, getTestEnv().getProperty("BannerMessageInformation"));
    }

//Single method to select category dropdown and choose a category based on the value from properties
public  void selectCategoryDropdown() {
// 1. Click the Select2 box //body[1]/div[12]/div[3]/table[1]/tbody[1]/tr[1]/td[3]/span[1]/span[1]/span[1]
   // getDriver().findElement(By.cssSelector("span.select2-selection")).click();
    getDriver().findElement(By.xpath("//tbody[@id='activeTableBody']/tr[1]//span[contains(@class,'select2-selection__rendered')]")).click();
    // 2. Wait for dropdown and click the desired option

// 1. Wait until the element is visible (using By locator)
    By dropLocator = By.xpath("//ul[contains(@class,'select2-results__options')]//li[normalize-space(.)='Good news']");
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(4));
    WebElement drop = wait.until(ExpectedConditions.visibilityOfElementLocated(dropLocator));

          if(drop.isDisplayed()){
              ExceptionUtility.clickSafe(dropLocator);
          }
    else {
// 2. Click with JavaScriptExecutor
               JavascriptExecutor js = (JavascriptExecutor) driver;
              js.executeScript("arguments[0].click();", dropLocator);
          }
}

    // Publishes Banner Message
    public void clickPublishButton() {
        actionDriver.clickUsingJS(publishButton);
    }

    // Verifies popup is displayed after publish
    public void verifyPopupDisplayed() {
        actionDriver.waitForElementToBeVisible(popupDisplayed);
        AssertionUtils.softAssertTrue(actionDriver.isDisplayed(popupDisplayed), "Popup not displayed!");
    }

    // Validates popup title text
    public void verifyPopupTitle() {

        String title = actionDriver.getText(popupTitleInPopup);
        AssertionUtils.softAssertEquals(title, getTestEnv().getProperty("BannerMessageTitle"), "Popup title mismatch!");
    }

    // Validates popup message text
    public void verifyPopupInformation() {
        String info = actionDriver.getText(popupInfoInPopup);
        AssertionUtils.softAssertEquals(info, getTestEnv().getProperty("BannerMessageInformation"), "Popup information mismatch!");
    }

    // Closes popup using Understood button
    public void clickUnderstoodButton() {
        try {
          if(  actionDriver.isDisplayed(popupDisplayed))
            actionDriver.moveToElement(understoodButton);
          actionDriver.click(understoodButton);
        }
        catch (Exception e){
        }
    }

    public void verifyInactiveBannerMessage()
    {
        String title = actionDriver.getText(InactiveBannerTitle);
        AssertionUtils.softAssertEquals(title, getTestEnv().getProperty("BannerMessageTitle"), "Popup information mismatch!");

        String info = actionDriver.getText(InactivebannerInformation);
        AssertionUtils.softAssertEquals(info, getTestEnv().getProperty("BannerMessageInformation"), "Popup information mismatch!");

      actionDriver.clickUsingJS(inactiveBannerinctiveButton);
      actionDriver.waitForElementToBeVisible(inactivebannerpopup);
      actionDriver.click(PopupYesButton);
    }

    // Clicks No on confirmation popup if visible
    public void clickPopupNoButton() {
        if(actionDriver.isDisplayed(popupNoButton)) {
            actionDriver.moveToElement(popupNoButton);
            actionDriver.click(popupNoButton);
        }
    }

        // Clicks inactive toggle button or creates a new banner if none present
        public void InactiveButton()  {
                // If the active->inactive toggle exists, click it and confirm the popup
                if (actionDriver.isDisplayed(ActiveBanneractiveButton)) {
                    actionDriver.clickUsingJS(ActiveBanneractiveButton);

                    // Wait for the confirmation popup content to appear, then click Yes
                    actionDriver.waitForElementToBeVisible(activeBannerPopup);
                    actionDriver.clickUsingJS(PopupYesButton);

                    logger.info("Clicked inactive toggle and confirmed with YES.");
                } else {
                    // Fallback: create and publish a new banner
                    logger.info("ActiveBannerInactiveButton not present — creating a new banner as fallback.");
                    actionDriver.click(newButton);
                    actionDriver.sendKeysWithActions(bannerMessageTitle, getTestEnv().getProperty("BannerMessageTitle"));
                    actionDriver.sendKeysWithActions(bannerMessageInformation, getTestEnv().getProperty("BannerMessageInformation"));

                    selectCategoryDropdown();

                    actionDriver.moveToElement(publishButton);
                    actionDriver.click(publishButton);

                    try {
                        sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    actionDriver.waitForElementToBeVisible(ActiveBanneractiveButton);
                        actionDriver.moveToElement(ActiveBanneractiveButton);
                        actionDriver.clickUsingJS(ActiveBanneractiveButton);
                        actionDriver.waitForElementToBeVisible(activeBannerPopup);
                        if (actionDriver.isDisplayed(PopupYesButton)) {
                            actionDriver.clickUsingJS(PopupYesButton);
                        }
                        // No confirmation popup - continue
                        logger.info("No confirmation popup appeared after publish (fallback flow).");
                }
            }
/*
        // Confirms popup using Yes button (uses corrected activeBannerPopup locator)
    public void clickPopupYesButton() {

        // 1. Wait for the popup content to be visible using the fixed locator
        actionDriver.waitForElementToBeVisible(activeBannerPopup);

        // 2. Click the YES button (using JS to avoid interception issues common with modals)
        actionDriver.clickUsingJS(PopupYesButton);
    }*/

    // Activates inactive banner
    public void ActiveBannerMessageActiveButton() {

        String titleVal = getTestEnv().getProperty("BannerMessageTitle");
        String infoVal  = getTestEnv().getProperty("BannerMessageInformation");

        actionDriver.waitForPageLoad(2);
        // If title input isn't visible, open the form by clicking New
        if (actionDriver.isDisplayed(activeBanner)) {
            actionDriver.click(newButton);

            actionDriver.waitForElementToBeVisible(bannerMessageTitle);
            actionDriver.enterText(bannerMessageTitle, titleVal);
            actionDriver.enterText(bannerMessageInformation, infoVal);

            // Select category and publish
            selectCategoryDropdown();
            actionDriver.click(publishButton);

            logger.info("Banner created/updated and published.");
        }

        // Fill fields

         ExceptionUtility.clickSafe(ActiveBanneractiveButton);
         actionDriver.waitForElementToBeVisible(PopupYesButton);
       ExceptionUtility.clickSafe(PopupYesButton);
    }

    // Displays confirmation popup for inactive banner activation
    public void InactiveBannerPopup(){
        actionDriver.clickUsingJS(inactiveBannerinctiveButton);
        actionDriver.isDisplayed(inactivebannerpopup);
    }

    // Clicks Yes on inactive banner activation popup
    public void InactiveBannerPopupYesButton() {
        try {
            actionDriver.scrollToElement(PopupYesButton);
            actionDriver.clickUsingJS(PopupYesButton);
            logger.info("Successfully clicked Popup YES button.");
        } catch (Exception e) {
            logger.error("Failed to click Popup YES button: " + e.getMessage());
            throw e;
        }
    }

    public void clickPopupYesButton()  {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));

        // Ensure the confirmation popup is visible
        actionDriver.waitForElementToBeVisible(activeBannerPopup);

        for (int attempts = 1; attempts <= 3; attempts++) {
            try {
                // Wait for Yes button to be clickable
                WebElement yesBtn = wait.until(ExpectedConditions.elementToBeClickable(PopupYesButton));

                // Click using JS (modals often block normal click)
                ((JavascriptExecutor) getDriver()).executeScript("arguments[0].click();", yesBtn);

                logger.info("Clicked YES button successfully on attempt " + attempts);

                // Wait for popup to disappear
                wait.until(ExpectedConditions.invisibilityOfElementLocated(activeBannerPopup));
                return; // success

            } catch (StaleElementReferenceException | ElementClickInterceptedException e) {
                logger.warn("Click issue on attempt " + attempts + ": " + e.getClass().getSimpleName());
                try {
                    sleep(400);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            } catch (TimeoutException e) {
                logger.error("Timed out waiting for YES button to become clickable.");
                break;
            } catch (Exception e) {
                logger.error("Unexpected error clicking YES button: " + e.getMessage());
                throw e;
            }
        }

        throw new RuntimeException("Failed to click YES button after 3 attempts.");
    }

    // Deletes active banner message
    public void deleteActiveBannerMessage() {
        if (actionDriver.isDisplayed(deleteButtonActivePopup)) {
            actionDriver.click(deleteButtonActivePopup);
            logger.info("Delete/Toggle button clicked.");
        }
        clickPopupYesButton();
    }

    // Simple create/update banner flow
    public void createBannerMessage()
    {
        String titleVal = getTestEnv().getProperty("BannerMessageTitle");
        String infoVal  = getTestEnv().getProperty("BannerMessageInformation");

        actionDriver.waitForPageLoad(2);
        // If title input isn't visible, open the form by clicking New
        if (actionDriver.isDisplayed(activeBanner)) {
            actionDriver.click(newButton);

            actionDriver.waitForElementToBeVisible(bannerMessageTitle);
        }

        // Fill fields
        actionDriver.enterText(bannerMessageTitle, titleVal);
        actionDriver.enterText(bannerMessageInformation, infoVal);

        // Select category and publish
        selectCategoryDropdown();
        actionDriver.click(publishButton);
actionDriver.waitForPageLoad(3);
        logger.info("Banner created/updated and published.");
    }

    public void onebannermessageactive() {
        String titleVal = getTestEnv().getProperty("BannerMessageTitle");
        String infoVal  = getTestEnv().getProperty("BannerMessageInformation");

        // Helper method to perform the banner creation steps
        Runnable createBanner = () -> {

          //  actionDriver.waitForElementToBeClickable(newButton);
            actionDriver.click(newButton);
            actionDriver.waitForElementToBeVisible(bannerMessageTitle);

            actionDriver.enterText(bannerMessageTitle, titleVal);
            actionDriver.enterText(bannerMessageInformation, infoVal);

            selectCategoryDropdown();
            actionDriver.click(publishButton);

            if(actionDriver.isDisplayed(activeBannerPopup)){
                clickPopupYesButton();
                WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(3));
                wait.until(ExpectedConditions.invisibilityOfElementLocated(activeBannerPopup));
            }

            actionDriver.waitForPageLoad(2);

            logger.info("Banner created/updated and published.");
        };

        if (actionDriver.isDisplayed(activeBanner)) {
            // Case 1: Title not displayed → perform twice
            createBanner.run();
            createBanner.run();
        } else {
            // Case 2: Title displayed → fill once, then repeat with New
            actionDriver.enterText(bannerMessageTitle, titleVal);
            actionDriver.enterText(bannerMessageInformation, infoVal);

            selectCategoryDropdown();
            actionDriver.click(publishButton);
            logger.info("Banner created/updated and published.");

            // Then repeat full sequence again
            createBanner.run();
        }
    }
}