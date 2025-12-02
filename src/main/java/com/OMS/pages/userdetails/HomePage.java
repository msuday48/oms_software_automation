package com.oms.pages.UserDetails;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.*;

import static com.oms.base.BaseClass.getDriver;

public class HomePage {

    private static final Logger logger = LogManager.getLogger(HomePage.class);
    private final ActionDriver actionDriver;
    WebDriver driver = getDriver();

    // Constructor initializes ActionDriver from BaseClass
    public HomePage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    // Locators
    public By TestReportAproval         = By.xpath("//button[@id='testerApprovalModalClose']");
    public By TestReportApprovalclose   = By.xpath("//button[@id='testerApprovalModalClose']");
    public static By Revisedpolicy            = By.xpath("//h4[normalize-space()='A new / revised policy has been implemented.']");
    public  By Revisedpolicyclose         = By.xpath("(//button[normalize-space()='Remind later'])[1]");
    public static By BannerMessagePopup       = By.xpath("//h4[normalize-space()='Important message']");
    public By BannerMessageclose        = By.xpath("//button[@id='companyInfoFooterBtn' ] [text() ='Understood']");
    public By NewApproveReuest          =By.xpath("//h4[text()= \"New approval request received\"]");
    public By NewApproveReuestclose     =By.xpath("//button[@onclick='closePendingApprovalModalAndSnooze()' and text()='Close']");
    public By HoldReports               =By.xpath("//h4[normalize-space()='Hold reports']");
    public By HoldreportsClose          =By.xpath("//button[@id='rohPopUpCloseBtn']");
    public By IncompleteReports         =By.xpath("//h4[normalize-space()='Incomplete reports']");
    public By IncompleteClose           =By.xpath("//button[@id='roiPopUpCloseBtn']");
    public By Timesheet                 = By.xpath("//h4[normalize-space()='No time sheet entry found yesterday.']");
    public By Timesheetclose            = By.xpath("//div[@id='understoodInstructionModal']//button[1]");
    public By chatbotframe              =By.xpath("//iframe[@frameborder=\"0\" and @title=\"chat widget\" and starts-with(@id,'skbmt')]");
    public By chat                      =By.xpath("//div[@isroundwidget='true']//div//*[name()='svg']");

     public void handleChatbotIfPresent() {
         try {
             WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

             // Try switching to the chatbot frame
             wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(0));
             logger.info("Switched to chatbot frame: " + chatbotframe);

             // Wait for chat element to be visible
             wait.until(ExpectedConditions.visibilityOfElementLocated(chat));
             logger.info("Chat element is visible: " + chat);

             // Perform click
             actionDriver.click(chat);
             logger.info("Clicked on chat element: " + chat);

         } catch (TimeoutException e) {
             logger.warn("Chatbot frame or chat element not found within timeout.");
         } catch (NoSuchElementException e) {
             logger.warn("Chat element not present in DOM.");
         } catch (Exception e) {
             logger.error("Unexpected error while handling chatbot: ", e);
         } finally {
             // Always switch back to default content
             actionDriver.switchToDefaultContent();
         }
     }
     public  void timesheet(){
         if(actionDriver.isDisplayed(Timesheet)){
             actionDriver.click(Timesheetclose);
             actionDriver.waitForPageLoad(5);
         }
     }

    // Map of popup header → close button
    public Map<By, By> popups = Map.of(
            TestReportAproval, TestReportApprovalclose,
            Revisedpolicy, Revisedpolicyclose,
            BannerMessagePopup, BannerMessageclose,
            NewApproveReuest, NewApproveReuestclose,
            HoldReports, HoldreportsClose,
            IncompleteReports, IncompleteClose
    );

    // Overload: Close or leave ALL popups (no target specified)
    public void handlePopups(boolean shouldClose, By bannerMessagePopup) {
        Popups(shouldClose, null);
    }

    // Existing method: Close all except target popup
    public void Popups(boolean shouldClose, By targetPopup) {
        try {
            for (Map.Entry<By, By> entry : popups.entrySet()) {
                By popupLocator = entry.getKey();
                By closeLocator = entry.getValue();

                if (actionDriver.isDisplayed(popupLocator)) {
                    // Case 1: Specific popup requested → keep it open, close others
                    if (targetPopup != null) {
                        if (popupLocator.equals(targetPopup)) {
                            logger.info("Target popup remains open: " + popupLocator);
                            continue; // skip closing this one
                        } else if (shouldClose) {
                            closePopup(closeLocator, popupLocator);
                        }
                    }
                    // Case 2: No target → close all if flag true
                    else if (shouldClose) {
                        closePopup(closeLocator, popupLocator);
                    } else {
                        logger.info("Popup left open intentionally: " + popupLocator);
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.warn("Unexpected error while handling popups: " + e.getMessage());
        }
    }

    private void closePopup(By closeLocator, By popupLocator) {
        try {
            // Move to element
            actionDriver.moveToElement(closeLocator);
            logger.info("Moved to close button: " + closeLocator);

            // Click using JS
            actionDriver.clickUsingJS(closeLocator);
            logger.info("Clicked close button via JS: " + closeLocator);

            // Wait until popup disappears
            WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(5));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(popupLocator));
            logger.info("Confirmed popup disappeared: " + popupLocator);

            // Final confirmation
            logger.info("Closed popup successfully: " + popupLocator);
        }
        catch (TimeoutException e) {
            logger.warn("Popup did not disappear after clicking: " + popupLocator);
        }
        catch (Exception e) {
            logger.error("Error closing popup " + popupLocator + ": " + e.getMessage());
        }
    }

    //Homepage Locators
    public By clientDropdown = By.xpath("//a[@class=\"dropdown-toggle\" and text()=\"Clients \"]");
    public  By customers     = By.xpath("(//a[normalize-space()='Customers'])");


}