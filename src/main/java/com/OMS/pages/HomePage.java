package com.oms.pages;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {

    private ActionDriver actionDriver;

    // Define locators using By class
    private By adminTab = By.xpath("//span[text()='Admin']");
    private By userIDButton = By.className("oxd-userdropdown-name");
    private By logoutButton = By.xpath("//a[text()='Logout']");
    private By oranageHRMlogo = By.xpath("//div[@class='oxd-brand-banner']//img");

    private By pimTab = By.xpath("//span[text()='PIM']");
    private By employeeSearch = By.xpath("//label[text()='Employee Name']/parent::div/following-sibling::div/div/div/input");
    private By searchButton = By.xpath("//button[@type='submit']");
    private By emplFirstAndMiddleName = By.xpath("//div[@class='oxd-table-card']/div/div[3]");
    private By emplLastName = By.xpath("//div[@class='oxd-table-card']/div/div[4]");

    // Initialize the ActionDriver object by passing WebDriver instance

    public HomePage(WebDriver driver) {
        this.actionDriver = BaseClass.getActionDriver();
    }

    // Method to verify if Admin tab is visible
    public boolean isAdminTabVisible() {
        return actionDriver.isDisplayed(adminTab);
    }

    public boolean verifyOrangeHRMlogo() {
        return actionDriver.isDisplayed(oranageHRMlogo);
    }

    public void logout() {
        actionDriver.click(adminTab);
        actionDriver.click(logoutButton);
    }
}
