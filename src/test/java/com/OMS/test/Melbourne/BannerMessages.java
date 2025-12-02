package com.OMS.test.Melbourne;

import com.oms.base.BaseClass;
import com.oms.pages.UserDetails.HomePage;
import com.oms.pages.Melbourne.BannerMessagePage;
import com.oms.pages.Oms_Login;
import com.oms.pages.UserDetails.UserAccountPage;
import org.testng.annotations.*;

public class BannerMessages extends BaseClass {

//    WebDriver driver = getDriver();

    private Oms_Login loginpage;
    private UserAccountPage userpage;
    private BannerMessagePage bm;
    private HomePage homepage;

    // ---------- LOGIN ONCE BEFORE CLASS ----------
    @BeforeClass(alwaysRun = true)
    public void classSetup() {
        loginpage = new Oms_Login(getDriver());
        bm = new BannerMessagePage(getDriver());
        homepage = new HomePage(getDriver());
        userpage = new UserAccountPage(getDriver());

        logger.info("====== LOGIN before executing BannerMessages test class ======");

        loginpage.login();
      //  homepage.handleChatbotIfPresent();
        homepage.timesheet();
        homepage.handlePopups(true,HomePage.Revisedpolicy);
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
    }

    // ---------- LOGOUT ONCE AFTER CLASS ----------
    @AfterClass(alwaysRun = true)
    public void classTeardown() {
        logger.info("====== LOGOUT after completing BannerMessages test class ======");
        try {
            if (userpage != null && userpage.userAccountExists()) {
                userpage.verifyUsername();
                userpage.clickLogout();
            }
        } catch (Exception e) {
            logger.error("Logout failed: " + e.getMessage());
        }
    }

    // --------------------------- TEST CASE 1 --------------------------- //
    @Test(priority = 1, groups = "Regression")
    public void TC_001_verifyBannerMessageMenuVisibility() {
        logger.info("=== TC_001 - Verifying Banner Messages submenu visibility ===");
        bm.BannerMessagePageForAllUsers();
    }

    // --------------------------- TEST CASE 2 --------------------------- //
    @Test(priority = 10, groups = "Regression")
    public void TC_002_verifyCreateEditAccessForUnauthorizedUsers() {
        logger.info("=== TC_002 - Verifying Create/Edit access for Unauthorised Users ===");
        bm.BannerMessagePageDisplayedForUnauthorizedUsers();
    }

    @Test(priority = 3, groups = "Regression")
    public void TC_003_verifyAuthorisedUserCanCreateBannerMessage() {
        logger.info("=== TC_003 - Authorised User creates Banner Message ===");
        bm.createBannerMessage();

        // Example 1: Close ALL popups
       // homepage.handlePopups(true, HomePage.BannerMessagePopup);

        // Example 2: Leave ALL popups open
        // homepage.handlePopups(false);

        // Example 3: Close all except Banner Message popup (keep Banner Message open)
        // homepage.handlePopups(true, HomePage.BannerMessagePopup);

        // EXTRA LOGOUT after TC_003
        logger.info("=== EXTRA LOGOUT after TC_003 ===");
        userpage.verifyUsername();
        userpage.clickLogout();
    }

    // --------------------------- TEST CASE 4 --------------------------- //
    @Test(priority = 4, groups = "Regression")
    public void TC_004_verifyBannerPopupVisibilityToAllUsers() {
        logger.info("=== Starting TC_004 ===");

        // FRESH LOGIN before TC_004
        logger.info("=== FRESH LOGIN before TC_004 ===");
        loginpage.login();
        bm.verifyPopupDisplayed();
        bm.verifyPopupTitle();
        bm.verifyPopupInformation();
        bm.clickUnderstoodButton();

        logger.info("=== Completed TC_004 ===");
    }
/*
    // --------------------------- TEST CASE 5 --------------------------- //
    @Test(priority = 5, groups = "Regression")
    public void TC_005_verifyInactiveBannerNotDisplayedToUsers() {
        logger.info("=== TC_005 - Verifying Inactive Banner Messages not displayed to users ===");

        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();

        bm.ActiveBannerMessageActiveButton();

        userpage.verifyUsername();
        userpage.clickLogout();

        loginpage.login();
        homepage.handlePopups(true, HomePage.BannerMessagePopup);
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
        bm.BannerMessagePageForAllUsers();
    }

    // --------------------------- TEST CASE 6 --------------------------- //
    @Test(priority = 6, groups = "Regression")
    public void TC_006_verifyInactiveMessagesListedInInactiveList() {
        logger.info("=== TC_006 - Verifying Inactive Banner Messages are listed under Inactive List ===");
        bm.verifyInactiveBannerMessage();
    }

    // --------------------------- TEST CASE 7 --------------------------- //
    @Test(priority = 7, groups = "Regression")
    public void TC_007_verifyOnlyOneBannerMessageCanBeActive() {
        bm.onebannermessageactive();
        logger.info("=== TC_007 - Verify only one Banner Message can be active at a time ===");

    }*/
}
