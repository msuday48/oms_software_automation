package com.oms.pages.UserDetails;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import com.oms.utilities.AssertionUtils;
import com.oms.utilities.ExceptionUtility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

//Page class to manage actions related to User Account profile.
public class UserAccountPage {

    private static final Logger logger = LogManager.getLogger(UserAccountPage.class);
    private final ActionDriver actionDriver;

    // Constructor initializes ActionDriver from BaseClass
    public UserAccountPage(WebDriver driver)
    {
        this.actionDriver = BaseClass.getActionDriver();
    }

    //Locators
    private final By username = By.xpath("//span[@class='profileText']");
    private final By btnLogout = By.xpath("//a[normalize-space()='Logout']");

    //action Methods

     //Validates displayed username and opens account actions.
    public void verifyUsername()
    {
        ExceptionUtility.clickSafe(username);
        logger.info("Account menu opened.");
    }

    //Verifies existence of user account section on page.
    public boolean userAccountExists()
    {
        return actionDriver.isDisplayed(username);
    }

     // Performs logout action from user account page
    public void clickLogout()
    {
        actionDriver.clickUsingJS(btnLogout);
        logger.info("Logout action completed.");
    }
}