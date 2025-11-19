package com.oms.pages.UserDetails;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import com.oms.utilities.AssertionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class UserAccountPage {

    private static final Logger logger = LogManager.getLogger(UserAccountPage.class);

    private final ActionDriver actionDriver;

    public UserAccountPage(WebDriver driver) {
        // Get thread-safe ActionDriver instance from BaseClass
        this.actionDriver = BaseClass.getActionDriver();
    }

    private final By username = By.xpath("//span[@class='profileText']");
    private final By btnLogout = By.xpath("//a[normalize-space()='Logout']");

    /*
     * Get the username text and perform soft assertion.
     */
    public void verifyUsername() {
        String user = actionDriver.getText(username);

        // Using AssertionUtils for thread-safe soft assertion
        AssertionUtils.softAssertEquals(user, "UDAY MS", "Username mismatch!");
        logger.info("Verified username successfully: '{}'", user);

        actionDriver.click(username);
        logger.info("Clicked on username to open account options.");
    }

    /*
     * Check if the user account element exists.
     */
    public boolean userAccountExists() {
        return actionDriver.isDisplayed(username);
    }

    /*
     * Click logout button.
     */
    public void clickLogout() {
        actionDriver.click(btnLogout);
        logger.info("Clicked on Logout button successfully.");
    }
}