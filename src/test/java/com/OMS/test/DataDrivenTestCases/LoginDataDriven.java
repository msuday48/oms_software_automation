package com.OMS.test.DataDrivenTestCases;

import com.oms.base.BaseClass;
import com.oms.pages.Oms_Login;
import com.oms.pages.UserDetails.HomePage;
import com.oms.pages.UserDetails.UserAccountPage;
import com.oms.utilities.DataProviders;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class LoginDataDriven extends BaseClass {

    private Oms_Login loginpage;
    private UserAccountPage userpage;
    private HomePage homepage;

    @BeforeMethod(groups = "Regression")
    public void setupAndLogin() {
        loginpage = new Oms_Login(getDriver());
        homepage = new HomePage(getDriver());
        userpage = new UserAccountPage(getDriver());
    }

    @Test(dataProvider = "LoginData", dataProviderClass = DataProviders.class, groups = "Regression")
    public void verifyLoginDDT(String Login_id, String password, String Expected_Result) {

        logger.info("**** Starting TC_LoginDDT *****");


            // Perform login using credentials from Excel
            loginpage.loginDDT(Login_id, password);
            loginpage.closeNoticePopupIfPresent();

            boolean homeDisplayed = loginpage.HomepageisDisplayed();

            if (homeDisplayed) {
                homepage.handlePopups(true);
            }

            boolean accountExists = userpage.userAccountExists();

            //  Case 1: Valid credentials
            if (Expected_Result.equalsIgnoreCase("Valid username and password")) {
                if (accountExists)
                {
                    userpage.verifyUsername();
                    userpage.clickLogout();
                    Assert.assertTrue(true, "Valid login succeeded as expected.");
                }
                else
                {
                    Assert.fail("Expected valid login, but account not found.");
                }
            }

            //  Case 2: Invalid credentials
            else if (Expected_Result.equalsIgnoreCase("Incorrect username or password")) {
                if (accountExists)
                {
                    userpage.verifyUsername();
                    userpage.clickLogout();
                    Assert.fail("Expected invalid login, but user logged in successfully.");
                }
                else
                {
                    Assert.assertTrue(true, "Invalid login rejected as expected.");
                }
            }
        logger.info("**** Finished TC_LoginDDT *****");
    }
}