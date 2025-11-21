package com.OMS.test.Melbourne;

import com.oms.base.BaseClass;
import com.oms.pages.UserDetails.HomePage;
import com.oms.pages.Melbourne.BannerMessagePage;
import com.oms.pages.Oms_Login;
import com.oms.pages.UserDetails.UserAccountPage;
import org.testng.annotations.*;
import java.lang.reflect.Method;

public class BannerMessages extends BaseClass {

    private Oms_Login loginpage;
    private UserAccountPage userpage;
    private BannerMessagePage bm;
    private HomePage homepage;

    // ---------- LOGIN BEFORE EVERY TEST ----------
    @BeforeMethod(alwaysRun = true)
    public void setupAndLogin(Method method) { // 1. Inject Method parameter

        // Initialize Pages
        loginpage = new Oms_Login(getDriver());
        bm = new BannerMessagePage(getDriver());
        homepage = new HomePage(getDriver());
        userpage = new UserAccountPage(getDriver());

        // Perform Login
        logger.info("====== LOGIN before Test Method: " + method.getName() + " ======");
        loginpage.login();

        // 2. DYNAMIC POPUP LOGIC
        // Check the name of the test about to run.
        // If it is 'test04', we pass FALSE (Do NOT close).
        // For all other tests (TC_001, TC_002, etc.), we pass TRUE (Close it).

        boolean shouldCloseBanner = !method.getName().equals("test04_verifyBannerPopupVisibilityToAllUsers");

        homepage.handlePopups(shouldCloseBanner);

        // 3. Navigation Logic
        // If we kept the popup OPEN (for test04), navigation might fail if the popup blocks the menu.
        // So we use a try-catch to ensure setup doesn't crash.
        try {
            // If the banner is open, this might not work depending on UI overlay
            if (shouldCloseBanner) {
                bm.clickMelbourneDropdown();
                bm.clickBannerMessagesButton();
            } else {
                logger.info("Skipping menu navigation in Setup because Banner Popup is intentionally open.");
            }
        } catch (Exception e) {
            logger.warn("Navigation failed in setup (likely due to open popup). Continuing execution...");
        }
    }

    // ---------- LOGOUT AFTER EVERY TEST ----------
    @AfterMethod
    public void logoutAfterTest() {
        logger.info("====== LOGOUT after Test Method ======");
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
    @Test(priority = 1, groups = "Regression" )
    public void TC_001_verifyBannerMessageMenuVisibility() {
        logger.info("=== TC_001 - Verifying Banner Messages submenu visibility ===");

        bm.BannerMessagePageForAllUsers();

    }

    // --------------------------- TEST CASE 2 --------------------------- //
    @Test(priority = 10, groups = "Regression")
    public void TC_002_verifyCreateEditAccessForUnauthorizedUsers()
    {
        logger.info("=== TC_002 - Verifying Create/Edit access for Unauthorised Users ===");

        bm.BannerMessagePageDisplayedForUnauthorizedUsers();
    }

    // --------------------------- TEST CASE 3 --------------------------- //
    @Test(priority = 3, groups = "Regression")
    public void test03_verifyAuthorisedUserCanCreateBannerMessage()
    {

        bm.BannerMessagePageForAllUsers();
        bm.deleteActiveBannerMessage();
        bm.clickNewButton();
        bm.setBannerMessageTitle();
        bm.setBannerMessageInformation();
        bm.selectBannerMessageCategory();
        bm.clickPublishButton();
    }

   // TC 004 - Popup was LEFT OPEN in setup, so we can verify it here
    @Test(priority = 4, groups = "Regression")
    public void test04_verifyBannerPopupVisibilityToAllUsers() {
        logger.info("=== Starting TC_004 ===");

        // 1. Verify the popup (It is open because setup passed 'false')
        bm.verifyPopupDisplayed();
        bm.verifyPopupTitle();
        bm.verifyPopupInformation();

        // 2. Manually close it (This handles the popup for this specific test)
        bm.clickUnderstoodButton();

        logger.info("=== Completed TC_004 ===");
    }

    // --------------------------- TEST CASE 5 --------------------------- //
    @Test(priority = 5, groups = "Regression")
    public void test05_verifyInactiveBannerNotDisplayedToUsers() {
        logger.info("=== TC_005 - Verifying Inactive Banner Messages not displayed to users ===");

        bm.clickInactiveButton();
        bm.clickPopupNoButton();
        bm.clickPopupYesButton();

        userpage.verifyUsername();
        userpage.clickLogout();

        loginpage.login();
        homepage.handlePopups(false);
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
        bm.BannerMessagePageForAllUsers();
    }

    // --------------------------- TEST CASE 6 --------------------------- //
    @Test(priority = 6, groups = "Regression")
    public void test06_verifyInactiveMessagesListedInInactiveList() {
        logger.info("=== TC_006 - Verifying Inactive Banner Messages are listed under Inactive List ===");

        bm.verifyInactiveBannerMessageTitle();
        bm.verifyInactiveBannerMessageInfo();
        bm.InactiveBannerMessageActiveButton();
        bm.InactiveBannerPopup();
        bm.InactiveBannerPopupYesButton();
    }


    // --------------------------- TEST CASE 7 --------------------------- //
    @Test(priority = 7, groups = "Regression")
    public void test07_verifyOnlyOneBannerMessageCanBeActive() {
        logger.info("=== TC_007 - Verify only one Banner Message can be active at a time ===");

        // create 1st banner
        bm.BannerMessagetab();
        bm.createBannerMessage();

    }
/*
    // --------------------------- TEST CASE 8 --------------------------- //
    @Test(priority = 8, groups = "Regression")
    public void test08_verifyAuditHistoryLogsForBannerMessages() {
        logger.info("=== TC_008 - Verify audit history logs for Banner Messages ===");

        loginToOMS();

        Banner_Messages_Page bm = new Banner_Messages_Page(driver, p);

        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
        bm.BannerMessagePageDisplayed_To_All_Users();
        bm.delete_banner_message();

        // create 1st banner
        bm.clickNewButton();
        bm.setBannerMessageTitle(p.getProperty("Banner_Message_Title"));
        bm.setBannerMessageInformation(p.getProperty("Banner_Message_Information"));
        bm.selectBannerMessageCategory();
        bm.Click_publish_Button();

        // create 2nd banner
        bm.clickNewButton();
        bm.setBannerMessageTitle(p.getProperty("Banner_Message_Title"));
        bm.setBannerMessageInformation(p.getProperty("Banner_Message_Information"));
        bm.selectBannerMessageCategory();
        bm.Click_publish_Button();
        bm.confirm_Save_Deactivating_Previous_BannerMessage_popup();

        // NOTE: Audit History verification step assumed to be inside Page Object
        bm.verifyAuditHistoryLogs();
    }

 */
}