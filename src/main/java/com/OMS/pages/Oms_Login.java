package com.oms.pages;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page class for handling Login page interactions.
 */
public class Oms_Login {

    private final ActionDriver actionDriver;

    // Constructor initializes ActionDriver from BaseClass
    public Oms_Login(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    // Locators
    private final By txtEmailAddress = By.xpath("//form[@id='userLoginform']//input[@id='userId']");
    private final By txtPassword = By.xpath("//form[@id='userLoginform']//input[@name='userPassword']");
    private final By dropdown = By.xpath("//form[@id='userLoginform']//select[@id='location']");
    private final By btnLogin = By.xpath("//div[@id='userLogin']//button[1]");
    private final By homepage_oms_logo = By.xpath("(//h4[normalize-space()='Jobs Dashboard'])[1]");
    private final By noticepopup = By.xpath("(//button[normalize-space()='Remind later'])[1]");

    //**************Action methods*************
     // Performs login into OMS using credentials from configuration file.
    public void login() {
        String username = BaseClass.getTestEnv().getProperty("emailid");
        String password = BaseClass.getTestEnv().getProperty("password");

        actionDriver.enterText(txtEmailAddress, username);
        actionDriver.enterText(txtPassword, password);
        actionDriver.getDropdownOptions(dropdown);
        actionDriver.selectByVisibleText(dropdown, "Corporate");
        actionDriver.click(btnLogin);
    }

     // Closes notice popup if displayed immediately after login.
    public void closeNoticePopupIfPresent() {
        if (actionDriver.isDisplayed(noticepopup)) {
            actionDriver.click(noticepopup);
        }
    }

    //Confirms if Home page is displayed after login.
    public boolean HomepageisDisplayed() {
        return actionDriver.isDisplayed(homepage_oms_logo);
    }

    // Overloaded login method to support Data-Driven Testing
    public void loginDDT(String username, String password) {
        actionDriver.enterText(txtEmailAddress, username);
        actionDriver.enterText(txtPassword, password);
        actionDriver.getDropdownOptions(dropdown);
        actionDriver.selectByVisibleText(dropdown, "Corporate");
        actionDriver.click(btnLogin);
    }


}











