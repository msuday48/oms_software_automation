package com.OMS.test;

import com.oms.base.BaseClass;
import com.oms.pages.HomePage;
import com.oms.pages.LoginPage;
import com.oms.utilities.AssertionUtils;
import com.oms.utilities.DataProviders;
import com.oms.utilities.RetryAnalyzer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class LoginPageTest extends BaseClass {

    private LoginPage loginPage;
    private HomePage homePage;

    @BeforeMethod
    public void setupPages()
    {
        loginPage = new LoginPage(getDriver());
        homePage = new HomePage(getDriver());
    }

    SoftAssert softassert = AssertionUtils.getSoftAssert();


    // com.OMS.test.LoginPageTest.java

    // ... inside verifyLoginTest
    @Test(dataProvider="validLoginData" , dataProviderClass = DataProviders.class)
    public void verifyLoginTest(String username , String password) {
        System.out.println("Running testMethod1 on thread" + Thread.currentThread().getId());
        loginPage.login(username, password);

        // --- CRITICAL CHANGE: Use Hard Assert for successful login check ---
        // If the admin tab is not visible (login failed), the test stops here.
        AssertionUtils.hardAssertTrue(homePage.isAdminTabVisible(), "Admin tab should be visible successfully after login");

        // If the hard assertion passes, execution continues
        homePage.logout();
        staticWait(2);
        // softassert.assertAll(); // Removed as there is only one assertion now.
    }

// ... other methods

    @Test(dataProvider="inValidLoginData" , dataProviderClass = DataProviders.class)
    public void invalidLoginTest() {
       // ExtentManager.startTest("valid Login Test");
        System.out.println("Running testMethod2 on thread" + Thread.currentThread().getId());
        loginPage.login("admin", "admin");
        String expectedErrorMessage = "Invalid credentials";
        softassert.assertTrue(loginPage.verifyErrorMessage(expectedErrorMessage), "Error message should be displayed as");
        softassert.assertAll();
    }
}