package com.oms.utilities;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;


import static com.oms.base.BaseClass.getDriver;

public class ExtentManager {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();
    private static final Map<Long, WebDriver> driverMap = new HashMap<>();

    // Store the report name globally for Email attachment
    public static String dynamicReportName;
    public static String dynamicReportPath;

    // Base path for screenshots
    private static final String SCREENSHOT_PATH = System.getProperty("user.dir") + "/src/test/resources/screenshots/";

    // This method is called by the TestListener onStart to initialize reports with Context
    public synchronized static ExtentReports setupReports(ITestContext context) {
        if (extent == null) {
            try {
                File screenshotDir = new File(SCREENSHOT_PATH);
                if (screenshotDir.exists()) {
                    System.out.println("Cleaning existing screenshots directory...");
                    FileUtils.deleteDirectory(screenshotDir);
                }
                screenshotDir.mkdirs();
            } catch (IOException e) {
                System.err.println("Failed to clean screenshot directory: " + e.getMessage());
            }

            // --- NEW LOGIC: Dynamic Timestamped Report Name ---
            String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
            dynamicReportName = "Test-Report-" + timeStamp + ".html";

            // You can adjust this path. I combined your original path structure with the new naming convention
            // or use ".\\Reports\\" + dynamicReportName as per your second snippet.
            // Below uses a "Reports" folder in the project root to match your second snippet's logic.
            String reportFolder = System.getProperty("user.dir") + "/Reports/";
            File reportDir = new File(reportFolder);
            if(!reportDir.exists()) {
                reportDir.mkdirs();
            }

            dynamicReportPath = reportFolder + dynamicReportName;

            ExtentSparkReporter spark = new ExtentSparkReporter(dynamicReportPath);
            spark.config().setReportName("OMS Functional Report");
            spark.config().setDocumentTitle("OMS Automation Report");
            spark.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(spark);

            // Default System Info
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("QA Name", "UDAY MS");
            extent.setSystemInfo("Web Application", "OMS SOFTWARE");
            extent.setSystemInfo("Module", "Home Melbourne");
            extent.setSystemInfo("Sub Module", "Banner Messages");
            extent.setSystemInfo("Operating System", System.getProperty("os.name"));
            extent.setSystemInfo("Java Version", System.getProperty("java.version"));

            // --- Dynamic System Info from TestNG XML Context ---
            if (context != null) {
                // Set Browser Name from XML parameter
                String browser = context.getCurrentXmlTest().getParameter("browser");
                if (browser != null && !browser.isEmpty()) {
                    extent.setSystemInfo("Browser", browser);
                } else {
                    extent.setSystemInfo("Browser", "Not Specified");
                }

                // Set OS if provided in XML
                String os = context.getCurrentXmlTest().getParameter("os");
                if (os != null && !os.isEmpty()) {
                    extent.setSystemInfo("Operating System (XML)", os);
                }

                // Set Groups if available
                List<String> includedGroups = context.getCurrentXmlTest().getIncludedGroups();
                if (includedGroups != null && !includedGroups.isEmpty()) {
                    extent.setSystemInfo("Groups", includedGroups.toString());
                }
            }
        }
        return extent;
    }

    // Getter for the reporter. If setupReports wasn't called, it initializes without context.
    public synchronized static ExtentReports getReporter() {
        if (extent == null) {
            // Fallback initialization if TestListener.onStart didn't trigger
            return setupReports(null);
        }
        return extent;
    }

    public synchronized static ExtentTest startTest(String testName) {
        ExtentTest extentTest = getReporter().createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    // --- NEW METHOD: To assign groups/categories to the current test ---
    public synchronized static void assignGroups(String[] groups) {
        if (getTest() != null && groups != null && groups.length > 0) {
            getTest().assignCategory(groups);
        }
    }

    public synchronized static void endTest() {
        if(extent != null) {
            extent.flush();
        }
    }

    // --- NEW METHOD: Open Report on Desktop after execution ---
    public static void openReport() {
        try {
            File extentReport = new File(dynamicReportPath);
            if (extentReport.exists()) {
                Desktop.getDesktop().browse(extentReport.toURI());
            } else {
                System.err.println("Report file not found at: " + dynamicReportPath);
            }
        } catch (IOException e) {
            System.err.println("Failed to open report file: " + e.getMessage());
        }
    }

    public synchronized static ExtentTest getTest() {
        return test.get();
    }

    public static String getTestName() {
        ExtentTest currentTest = getTest();
        if (currentTest != null) {
            return currentTest.getModel().getName();
        } else {
            return "No_Active_Test_Thread_" + Thread.currentThread().getId();
        }
    }

    public static void logStep(String logMessage) {
        if (getTest() != null) {
            getTest().info(logMessage);
        }
    }

    public static void logStepWithScreenshot(WebDriver driver, String logMessage, String screenShotMessage) {
        if (getTest() != null) {
            getTest().pass(logMessage);
            attachScreenshot(driver, screenShotMessage, false);
        }
    }

    public static void logStepValidationForAPI(String logMessage) {
        if (getTest() != null) {
            getTest().pass(logMessage);
        }
    }

    public static void logFailure(WebDriver driver, String logMessage, String screenShotMessage) {
        if (getTest() != null) {
            String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
            getTest().fail(colorMessage);
            attachScreenshot(getDriver(), screenShotMessage, true);
        } else {
            System.err.println("FAILURE (No Extent Report Active): " + logMessage);
        }
    }

    public static void logFailureAPI(String logMessage) {
        if (getTest() != null) {
            String colorMessage = String.format("<span style='color:red;'>%s</span>", logMessage);
            getTest().fail(colorMessage);
        }
    }

    public static void logSkip(String logMessage) {
        if (getTest() != null) {
            String colorMessage = String.format("<span style='color:orange;'>%s</span>", logMessage);
            getTest().skip(colorMessage);
        }
    }

    public synchronized static String takeScreenshot(WebDriver driver, String screenshotName, boolean saveToDisk) {
        if (driver == null) return "";

        TakesScreenshot ts = (TakesScreenshot) driver;
        File src = ts.getScreenshotAs(OutputType.FILE);

        if (saveToDisk) {
            String timeStamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String cleanScreenshotName = screenshotName.replaceAll("[^a-zA-Z0-9.-]", "_");
            String destPath = SCREENSHOT_PATH + cleanScreenshotName + "_" + timeStamp + ".png";
            File finalPath = new File(destPath);
            try {
                FileUtils.copyFile(src, finalPath);
            } catch (IOException e) {
                System.err.println("Failed to save screenshot to disk: " + e.getMessage());
            }
        }

        return convertToBase64(src);
    }

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

    public synchronized static void attachScreenshot(WebDriver driver, String message, boolean isFailure) {
        if (getTest() != null)
        {
            try {
                String screenShotBase64 = takeScreenshot(driver, getTestName(), isFailure);
                getTest().info(message,
                        com.aventstack.extentreports.MediaEntityBuilder.createScreenCaptureFromBase64String(screenShotBase64).build());
            } catch (Exception e) {
                getTest().fail("Failed to attach screenshot due to exception: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static void registerDriver(WebDriver driver) {
        driverMap.put(Thread.currentThread().getId(), driver);
    }
}