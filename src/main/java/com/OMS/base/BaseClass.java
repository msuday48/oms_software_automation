package com.oms.base;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;

import com.oms.actiondriver.ActionDriver;
import com.oms.utilities.ExtentManager;
import com.oms.utilities.LoggerManager;

public class BaseClass {

    public static Properties prop;
    public static Properties qa;
    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
    public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

    // Loads configuration files before the suite execution starts
    @BeforeSuite(alwaysRun = true)
    public void loadConfig() throws IOException {
        prop = new Properties();
        FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
        prop.load(fis);

        qa = new Properties();
        FileInputStream TestEnv = new FileInputStream("src/main/resources/TestEnvironment.properties");
        qa.load(TestEnv);

        logger.info("Configuration properties loaded and Extent Report initialized");
    }

    // Initializes WebDriver and ActionDriver before every test method
    // Updates: Added @Parameters and logic to handle browser selection
    @BeforeClass(alwaysRun = true)
    @Parameters("browser")
    public synchronized void setup(@Optional String browser) {

        // If browser param is missing (running from IDE), fall back to config file
        if (browser == null) {
            browser = prop.getProperty("browser");
        }

        launchBrowser(browser);
        configureBrowser();
        staticWait(1);
        actionDriver.set(new ActionDriver(driver.get()));
        logger.info("ActionDriver initialized for thread " + Thread.currentThread().getId());
    }
    // Launches browser based on configuration (Grid vs Local)
    private void launchBrowser(String browser) {
        // Use System property to allow command line override, fallback to config
        boolean seleniumGrid = Boolean.parseBoolean(System.getProperty("seleniumGrid", prop.getProperty("seleniumGrid")));
        String gridURL = prop.getProperty("gridURL");

        if (seleniumGrid) {
            // --- SELENIUM GRID / REMOTE EXECUTION ---
            try {
                if (browser.equalsIgnoreCase("chrome")) {
                    ChromeOptions options = new ChromeOptions();
                    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080", "--no-sandbox", "--disable-dev-shm-usage");
                    driver.set(new RemoteWebDriver(new URL(gridURL), options));
                } else if (browser.equalsIgnoreCase("firefox")) {
                    FirefoxOptions options = new FirefoxOptions();
                    options.addArguments("--headless", "--disable-gpu", "--window-size=1920,1080");
                    driver.set(new RemoteWebDriver(new URL(gridURL), options));
                } else if (browser.equalsIgnoreCase("edge")) {
                    EdgeOptions options = new EdgeOptions();
                    options.addArguments("--headless=new", "--disable-gpu", "--no-sandbox", "--disable-dev-shm-usage");
                    driver.set(new RemoteWebDriver(new URL(gridURL), options));
                } else {
                    throw new IllegalArgumentException("Grid Browser Not Supported: " + browser);
                }
                logger.info("RemoteWebDriver instance created for Grid execution: " + browser);
            } catch (MalformedURLException e) {
                throw new RuntimeException("Invalid Grid URL: " + gridURL, e);
            }
        } else {
            // --- LOCAL EXECUTION ---

            // Get the project path dynamically
            String projectPath = System.getProperty("user.dir");

            if (browser.equalsIgnoreCase("chrome")) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--disable-gpu", "--disable-notifications", "--no-sandbox", "--disable-dev-shm-usage");
                driver.set(new ChromeDriver(options));
                logger.info("Local Chrome Browser initialized");
            }
            else if (browser.equalsIgnoreCase("firefox")) {
                FirefoxOptions options = new FirefoxOptions();
                options.addArguments("--disable-gpu", "--disable-notifications", "--no-sandbox", "--disable-dev-shm-usage");
                driver.set(new FirefoxDriver(options));
                logger.info("Local Firefox Browser initialized");
            }
            else if (browser.equalsIgnoreCase("edge")) {
               //  Pointing to the driver inside src/main/resources/drivers
                System.setProperty("webdriver.edge.driver", projectPath + "/src/main/resources/drivers/msedgedriver.exe");

                EdgeOptions options = new EdgeOptions();
                options.addArguments("--disable-gpu", "--disable-notifications", "--no-sandbox", "--disable-dev-shm-usage");
                options.addArguments("--remote-allow-origins=*");

                driver.set(new EdgeDriver(options));
                logger.info("Local Edge Browser initialized");
            }
            else {
                throw new IllegalArgumentException("Local Browser Unsupported: " + browser);
            }
        }
        // Register the driver with ExtentManager
        ExtentManager.registerDriver(getDriver());
    }
    // Configures browser settings and Navigates to specific URL based on Grid/Local
    private void configureBrowser() {
        // Implicit Wait
        int implicitWait = Integer.parseInt(prop.getProperty("implicitWait"));

        // Logic to check system property first, then config property
        boolean seleniumGrid = Boolean.parseBoolean(System.getProperty("seleniumGrid", prop.getProperty("seleniumGrid")));

        getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWait));

        // maximize the browser
        getDriver().manage().window().maximize();

        // Navigate to URL logic
        /*try {
            getDriver().get(prop.getProperty("url"));
        } catch (Exception e) {
            System.out.println("Failed to Navigate to the URL:" + e.getMessage());
        } */

        if (seleniumGrid) {
            getDriver().get(prop.getProperty("url_grid"));
            logger.info("Navigated to Grid URL: " + prop.getProperty("url_grid"));
        } else {
            getDriver().get(prop.getProperty("url_local"));
            logger.info("Navigated to Local URL: " + prop.getProperty("url_local"));
        }
    }

    // Closes the browser instance and clears thread-local driver references after each test
    @AfterClass(alwaysRun = true)
    public synchronized void tearDown() {
        if (getDriver() != null) {
            try {
                getDriver().quit();
            } catch (Exception e) {
                logger.error("Unable to quit the driver: " + e.getMessage());
            }
        }

        driver.remove();
        actionDriver.remove();
        logger.info("WebDriver instance closed and ThreadLocal cleared");
    }

    // Returns config properties
    public static Properties getProp() {
        return prop;
    }

    // Returns Test environment properties file values
    public static Properties getTestEnv() {
        return qa;
    }

    // Returns ActionDriver instance for current thread
    public static ActionDriver getActionDriver() {
        if (actionDriver.get() == null) {
            throw new IllegalStateException("ActionDriver not initialized");
        }
        return actionDriver.get();
    }

    // Static wait method used to resolve short wait requirements without Thread.sleep
    public void staticWait(int seconds) {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }

    // Returns WebDriver instance for the current thread
    public static WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException("WebDriver is not initialized");
        }
        return driver.get();
    }
}