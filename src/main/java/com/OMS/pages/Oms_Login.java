package com.oms.pages;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class Oms_Login {

    private ActionDriver actionDriver;

    public Oms_Login(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }
    // Locators
    private By txtEmailAddress = By.xpath("//form[@id='userLoginform']//input[@id='userId']");
    private By txtPassword = By.xpath("//form[@id='userLoginform']//input[@name='userPassword']");
    private By dropdown = By.xpath("//form[@id='userLoginform']//select[@id='location']");
    private By btnLogin = By.xpath("//div[@id='userLogin']//button[1]");
    private By homepage_oms_logo = By.xpath("(//h4[normalize-space()='Jobs Dashboard'])[1]");

    private By noticepopup  = By.xpath(" (//button[normalize-space()='Remind later'])[1]");

    public void login()
    {
        // Read username and password from config.properties
        String username = BaseClass.getProp().getProperty("emailid");
        String password = BaseClass.getProp().getProperty("pwd");

        actionDriver.enterText(txtEmailAddress,username);
        actionDriver.enterText(txtPassword,password);
        actionDriver.getDropdownOptions(dropdown);
        actionDriver.selectByVisibleText(dropdown,"Corporate");
        actionDriver.click(btnLogin);
    }

    public void closeNoticePopupIfPresent() {
        if (actionDriver.isDisplayed(noticepopup)) {
            actionDriver.click(noticepopup);
        }
    }

    public boolean HomepageisDisplayed() {
        return actionDriver.isDisplayed(homepage_oms_logo);
    }
}
