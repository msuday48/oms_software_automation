package com.OMS.test;

import com.oms.base.BaseClass;
import com.oms.pages.Oms_Login;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Oms_LoginPageTest extends BaseClass {

    private Oms_Login login;

    // Initializes the Oms_Login page object before each test method.
    @BeforeMethod
    public void setupPages()
    {
        login = new Oms_Login(getDriver());
    }

    // Verifies the login functionality by performing a login action and checking if the home page is displayed.
    @Test
    public void verifyLoginTest() {
        login.login();
        login.HomepageisDisplayed();
    }
}