package com.oms.pages.UserDetails;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
    private By Revisedpolicy            = By.xpath("//h4[normalize-space()='A new / revised policy has been implemented.']");
    private By Revisedpolicyclose         = By.xpath("(//button[normalize-space()='Remind later'])[1]");
    private By BannerMessagePopup       = By.xpath("//h4[normalize-space()='Important message']");
    private By BannerMessageclose        = By.xpath("//button[@id='companyInfoFooterBtn' ] [text() ='Understood']");
    private By TestReportAproval         = By.xpath("//h4[normalize-space()='Test report approval notification']");
    private By TestReportApprovalclose   = By.xpath("//button[@id='testerApprovalModalClose']");
    private By NewApproveReuest          =By.xpath("//h4[text()= \"New approval request received\"]");
    private By NewApproveReuestclose     =By.xpath("//button[@onclick='closePendingApprovalModalAndSnooze()' and text()='Close']");
    private By HoldReports               =By.xpath("//h4[normalize-space()='Hold reports']");
    private By HoldreportsClose          =By.xpath("//button[@id='rohPopUpCloseBtn']");
    private By IncompleteReports         =By.xpath("//h4[normalize-space()='Incomplete reports']");
    private By IncompleteClose           =By.xpath("//button[@id='roiPopUpCloseBtn']");
    private By chatbotframe              =By.xpath("///iframe[@frameborder=\"0\" and @title=\"chat widget\" and starts-with(@id,'skbmt')]");
    private By chat                      =By.xpath("//div[@isroundwidget='true']//div//*[name()='svg']");
    private  By chatbotcustomer          = By.xpath("//p[@class='tawk-toolbar-agent-name tawk-text-truncate' and text()=' Customer Support ']");
    private By chatotclose               =By.xpath("//div[@isroundwidget='true']//div//*[name()='svg']");

     // @param shouldCloseBannerPopup If true, closes the banner popup; if false, leaves it open for verification
/*
    public void handlePopups(boolean shouldCloseBannerPopup) {
        try {
            // 1. Handle Banner Message Popup
            if (actionDriver.isDisplayed(BannerMessageclose)) {
                if (shouldCloseBannerPopup) {
                    actionDriver.moveToElement(BannerMessageclose);
                    actionDriver.clickUsingJS(BannerMessageclose);
                    logger.info("Banner Message popup closed successfully.");
                } else {
                    logger.info("Banner Message popup left open intentionally for verification.");
                }
            } else {
                logger.info("Banner Message popup not displayed.");
            }

            // 2. Handle Revised Policy Popup (only if banner was closed or not present)
            if (shouldCloseBannerPopup && actionDriver.isDisplayed(Revisedpolicy)) {
                actionDriver.moveToElement(Revisedpolicy);
                actionDriver.clickUsingJS(Revisedpolicy);
                logger.info("Revised Policy popup closed successfully.");
            } else if (shouldCloseBannerPopup) {
                logger.info("Revised Policy popup not displayed.");
            }
        }
        catch (Exception e) {
            logger.warn("Unexpected error while handling popups: " + e.getMessage());
            // Don't throw exception - allow test to continue
        }
    }
*/// public By chatbotframe              =By.xpath("///iframe[@frameborder=\"0\" and @title=\"chat widget\" and starts-with(@id,'skbmt')]");
     public void handleChatbotIfPresent() {
         try {
             WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));

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

    // Map of popup header → close button
    private final Map<By, By> popups = Map.of(
            Revisedpolicy, Revisedpolicyclose,
            BannerMessagePopup, BannerMessageclose,
            TestReportAproval, TestReportApprovalclose,
            NewApproveReuest, NewApproveReuestclose,
            HoldReports, HoldreportsClose,
            IncompleteReports, IncompleteClose
    );

    // Overload: Close or leave ALL popups (no target specified)
    public void handlePopups(boolean shouldClose) {
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
        } catch (Exception e) {
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

        } catch (TimeoutException e) {
            logger.warn("Popup did not disappear after clicking: " + popupLocator);
        } catch (Exception e) {
            logger.error("Error closing popup " + popupLocator + ": " + e.getMessage());
        }
    }
}