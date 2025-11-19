package com.oms.pages;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {

    private ActionDriver actionDriver;

    public LoginPage(WebDriver driver)
    {
        this.actionDriver = BaseClass.getActionDriver();
    }

  /*  public LoginPage(WebDriver driver) {
        this.actionDriver = new ActionDriver(driver);
    }*/

    // Locators
    private By userNameField = By.name("username");
    private By passwordField = By.cssSelector("input[type='password']");
    private By loginButton = By.xpath("//button[text()=' Login ']");
    private By errorMessage = By.xpath("//p[text()='Invalid credentials']");

    // Method to perform login
    public void login(String userName, String password)
    {
        actionDriver.enterText(userNameField, userName);
        actionDriver.enterText(passwordField, password);
        actionDriver.click(loginButton);
    }

    // Check if error message displayed
    public boolean isErrorMessageDisplayed()
    {
        return actionDriver.isDisplayed(errorMessage);
    }

    // Get error message text
    public String getErrorMessageText()
    {
        return actionDriver.getText(errorMessage);
    }

    // Verify error message content
    public boolean verifyErrorMessage(String expectedMessage) {
        return actionDriver.compareText(errorMessage, expectedMessage);
    }
}