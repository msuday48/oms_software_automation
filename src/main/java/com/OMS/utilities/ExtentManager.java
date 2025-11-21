package com.oms.utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentManager {

    private static ExtentReports extent;
    private static ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static Map<Long, WebDriver> driverMap = new HashMap<>();

    // Initialize the Extent Report
    public synchronized static ExtentReports getReporter() {
        if (extent == null) {
            String reportPath = System.getProperty("user.dir") + "/src/test/resources/ExtentReport/ExtentReport.html";
            ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
            spark.config().setReportName("Automation Test Report");
            spark.config().setDocumentTitle("OrangeHRM Report");
            spark.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            // Adding system information
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));
            extent.setSystemInfo("User Name", System.getProperty("user.name"));
        }

        return extent;
    }

    // Start the Test
    public synchronized static ExtentTest startTest(String testName) {
        ExtentTest extentTest = getReporter().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    // End a Test
    public synchronized static void endTest() {
        getReporter().flush();
    }

    // Get Current Thread's test
    public synchronized static ExtentTest getTest() {
        return test.get();
    }

    // Method to get the name of the current test
    public static String getTestName() {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            return currentTest.getModel().getName();
        } else {
            return "No test is currently active for this thread";
        }
    }

    // ==================== SAFE LOGGING METHODS ====================

    // Log a step (Safe)
    public static void logStep(String logMessage) {
        if (getTest() != null) {
            getTest().info(logMessage);
        }
    }

    // Log a step validation with screenshot (Safe)
    public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenShotMessage) {
        if (getTest() != null) {
            getTest().pass(logMessage);
            attachScreenshot(driver, screenShotMessage);
        }
    }

    // Log a step validation for API (Safe)
    public static void logStepValidationForAPI(String logMessage) {
        if (getTest() != null) {
            getTest().pass(logMessage);
        }
    }

    // Log a Failure (Safe)
    public static void logFailure(WebDriver driver, String logMessage, String screenShotMessage) {
        if (getTest() != null) {
            String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
            getTest().fail(colorMessage);
            attachScreenshot(driver, screenShotMessage);
        } else {
            // Fallback console print if failure happens during setup/teardown
            System.err.println("FAILURE (No Extent Report Active): " + logMessage);
        }
    }

    // Log a Failure for API (Safe)
    public static void logFailureAPI(String logMessage) {
        if (getTest() != null) {
            String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
            getTest().fail(colorMessage);
        }
    }

    // Log a skip (Safe)
    public static void logSkip(String logMessage) {
        if (getTest() != null) {
            String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);
            getTest().skip(colorMessage);
        }
    }

    // ==================== SCREENSHOT UTILITIES ====================

    // Take a screenshot with date and time in the file
    public synchronized static String takeScreenshot(WebDriver driver, String screenshotName) {
        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);

        // Format date and Time for file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        // Saving the screenshot to a file
        String destPath = System.getProperty("user.dir") + "/src/test/resources/screenshots/" + screenshotName + "_" + timeStamp + ".png";

        File finalPath = new File(destPath);
        try {
            FileUtils.copyFile(src, finalPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Convert screenshot to Base64 for embedding in the Report
        return convertToBase64(src);
    }

    // Convert screenshot to Base64 format
    public static String convertToBase64(File screenShotFile) {
        String base64Format = "";
        try {
            byte[] fileContent = FileUtils.readFileToByteArray(screenShotFile);
            base64Format = Base64.getEncoder().encodeToString(fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64Format;
    }

    // Attach screenshot to report using Base64 (Safe)
    public synchronized static void attachScreenshot(WebDriver driver, String message) {
        if (getTest() != null) {
            try {
                String screenShotBase64 = takeScreenshot(driver, getTestName());
                getTest().info(message, com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(screenShotBase64).build());
            } catch (Exception e) {
                getTest().fail("Failed to attach screenshot:" + message);
                e.printStackTrace();
            }
        }
    }

    // Register WebDriver for current Thread
    public static void registerDriver(WebDriver driver) {
        driverMap.put(Thread.currentThread().getId(), driver);
    }
}