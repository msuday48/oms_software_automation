package com.OMS.test;

import com.oms.base.BaseClass;
import com.oms.pages.HomePage;
import com.oms.pages.LoginPage;
import com.oms.pages.Oms_Login;
import com.oms.utilities.RetryAnalyzer;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

public class Oms_LoginPageTest extends BaseClass {

    private Oms_Login login;
    private com.oms.pages.HomePage homePage;

    @BeforeMethod
    public void setupPages()
    {
        login = new Oms_Login(getDriver());
    }

@Test
    public void verifyLoginTest() {
        login.login();
        login.HomepageisDisplayed();
    }
}
