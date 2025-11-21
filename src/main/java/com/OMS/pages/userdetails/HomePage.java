package com.oms.pages.UserDetails;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    private static final Logger logger = LogManager.getLogger(HomePage.class);
    private final ActionDriver actionDriver;

    public HomePage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    private By RevisedPolicyPopup = By.xpath("(//button[normalize-space()='Remind later'])[1]");
    private By BannerMessagePopup = By.xpath("(//button[@id='companyInfoFooterBtn'])[1]");

    /**
     * Handles application popups.
     * @param shouldCloseBannerPopup If true, closes the banner message. If false, leaves it open (for validation tests).
     */
    public void handlePopups(boolean shouldCloseBannerPopup) {

        // 1. Always handle Revised Policy Popup
        if (actionDriver.isDisplayed(RevisedPolicyPopup)) {
            actionDriver.moveToElement(RevisedPolicyPopup);
            actionDriver.clickUsingJS(RevisedPolicyPopup);
            logger.info("Revised Policy Popup was displayed and clicked 'Remind Later'");
        }

        // 2. Conditionally handle Banner Message Popup
        if (actionDriver.isDisplayed(BannerMessagePopup)) {
            if (shouldCloseBannerPopup) {
                actionDriver.moveToElement(BannerMessagePopup);
                actionDriver.clickUsingJS(BannerMessagePopup);
                logger.info("Banner Message Popup was displayed and clicked 'Understood'");
            } else {
                logger.info("Banner Message Popup displayed but LEFT OPEN for verification testing.");
            }
        } else {
            logger.info("No popup displayed on screen.");
        }
    }
}