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

    public void handlePopups() {

        if (actionDriver.isDisplayed(RevisedPolicyPopup)) {

            logger.info("Revised Policy Popup detected. Attempting to close...");

            actionDriver.moveToElement(RevisedPolicyPopup);
            actionDriver.click(RevisedPolicyPopup);

        }
        else {
            logger.info("Revised Policy Popup was not displayed. Continuing...");
        }
    }
}