package com.OMS.test;

import com.oms.base.BaseClass;
import com.oms.pages.LoginPage;
import com.oms.utilities.ExtentManager;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class HomePage extends BaseClass
{

    private LoginPage loginPage;
    private com.oms.pages.HomePage homePage;

    @BeforeMethod
    public void setupPages()
    {
        loginPage = new LoginPage(getDriver());
        homePage = new com.oms.pages.HomePage(getDriver());
    }

    @Test
    public void verifyOrangeHRMLogo() {
      //  ExtentManager.startTest("Homepage Logo Test");
        String title = getDriver().getTitle();
      //  ExtentManager.logStep("Navigating to login page entering username and password");
        loginPage.login("admin", "admin123");

        Assert.assertTrue(homePage.verifyOrangeHRMlogo(),"Logo is not visible");

        homePage.logout();
    }
}
