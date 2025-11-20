package com.OMS.test.Melbourne;

import com.oms.base.BaseClass;
import com.oms.pages.UserDetails.HomePage;
import com.oms.pages.Melbourne.BannerMessagePage;
import com.oms.pages.Oms_Login;
import com.oms.pages.UserDetails.UserAccountPage;
import com.oms.utilities.AssertionUtils;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

public class BannerMessages extends BaseClass {

    private Oms_Login loginpage;
    private UserAccountPage userpage;
    private BannerMessagePage bm;
    private HomePage homepage;

    // ---------- LOGIN BEFORE EVERY TEST ----------
    @BeforeMethod(alwaysRun = true)
    public void setupAndLogin() {

        // 1. Initialize page objects with the correct driver
        loginpage = new Oms_Login(getDriver());
        bm = new BannerMessagePage(getDriver());
        homepage = new HomePage(getDriver());
        userpage = new UserAccountPage(getDriver());

        // 2. Perform Login
        logger.info("====== LOGIN before Test Method ======");
        loginpage.login();
        homepage.handlePopups();
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
    }

    // ---------- LOGOUT AFTER EVERY TEST ----------
    @AfterMethod(alwaysRun = true)
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
    @Test(priority = 1, groups = "Regression" , enabled = false)
    public void TC_001_verifyBannerMessageMenuVisibility() {
        logger.info("=== TC_001 - Verifying Banner Messages submenu visibility ===");
        loginpage.closeNoticePopupIfPresent();
        bm.BannerMessagePageForAllUsers();
    }

    // --------------------------- TEST CASE 2 --------------------------- //
    @Test(priority = 2, groups = "Regression", enabled = false)
    public void TC_002_verifyCreateEditAccessForUnauthorizedUsers()
    {
        logger.info("=== TC_002 - Verifying Create/Edit access for Unauthorised Users ===");
        loginpage.closeNoticePopupIfPresent();
        bm.BannerMessagePageDisplayedForUnauthorizedUsers();
    }

    // --------------------------- TEST CASE 3 --------------------------- //
    @Test(priority = 3, groups = "Regression")
    public void test03_verifyAuthorisedUserCanCreateBannerMessage() {
        logger.info("=== TC_003 - Verifying Authorised User Can Create Banner Message ===");

        bm.BannerMessagePageForAllUsers();
        bm.clickNewButton();
        bm.setBannerMessageTitle();
        bm.setBannerMessageInformation();
        bm.selectBannerMessageCategory();
        bm.clickPublishButton();
    }

/*
    // --------------------------- TEST CASE 4 --------------------------- //
    @Test(priority = 4, groups = "Regression")
    public void test04_verifyBannerPopupVisibilityToAllUsers() {
        logger.info("=== TC_004 - Verifying Banner Message Popup visibility after login ===");

        loginToOMS();

        Banner_Messages_Page bm = new Banner_Messages_Page(driver, p);
        bm.popupDisplayedConfirmation();
        bm.bannerMessageTitleConfirmationInPopup();
        bm.bannerMessageInformationConfirmationInPopup();
        bm.clickUnderstoodButton();
    }

//
    // --------------------------- TEST CASE 5 --------------------------- //
    @Test(priority = 5, groups = "Regression")
    public void test05_verifyInactiveBannerNotDisplayedToUsers() throws InterruptedException {
        logger.info("=== TC_005 - Verifying Inactive Banner Messages not displayed to users ===");

        loginToOMS();

        Banner_Messages_Page bm = new Banner_Messages_Page(driver, p);

        // set existing banner to inactive
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
        bm.banner_message_active_button();
        bm.banner_message_active_popup();
        bm.banner_message_active_popup_no_button();
        bm.banner_message_active_button();
        bm.banner_message_active_popup_yes_button();
        Thread.sleep(3000);

        // logout
        User_Account_Page user = new User_Account_Page(driver);
        user.GetUsername();
        user.clickLogout();

        // login again
        loginToOMS();

        // verify inactive message not visible
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
        bm.BannerMessagePageDisplayed_To_All_Users();
    }


    // --------------------------- TEST CASE 6 --------------------------- //
    @Test(priority = 6, groups = "Regression")
    public void test06_verifyInactiveMessagesListedInInactiveList() {
        logger.info("=== TC_006 - Verifying Inactive Banner Messages are listed under Inactive List ===");

        loginToOMS();

        Banner_Messages_Page bm = new Banner_Messages_Page(driver, p);
        bm.clickMelbourneDropdown();
        bm.clickBannerMessagesButton();
        bm.Inactive_bannermessage_title();
        bm.Inactive_bannermessage_Information();
        bm.Inactive_bannermessage_Active_button();
        bm.banner_message_inactive_popup();
        bm.banner_message_inactive_popup_no_button();
        bm.banner_message_inactive_popup_yes_button();
    }


    // --------------------------- TEST CASE 7 --------------------------- //
    @Test(priority = 7, groups = "Regression")
    public void test07_verifyOnlyOneBannerMessageCanBeActive() {
        logger.info("=== TC_007 - Verify only one Banner Message can be active at a time ===");

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

        // confirm deactivation popup
        bm.confirm_Save_Deactivating_Previous_BannerMessage_popup();
    }


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