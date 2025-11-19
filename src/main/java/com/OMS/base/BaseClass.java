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
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;
import com.oms.actiondriver.ActionDriver;
import com.oms.utilities.ExtentManager;
import com.oms.utilities.LoggerManager;


public class BaseClass {

    public static Properties prop;
    //protected static WebDriver driver;

    // static ActionDriver kept as per your requirement
    //private static ActionDriver actionDriver;

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static ThreadLocal<ActionDriver> actionDriver = new ThreadLocal<>();
    protected ThreadLocal<SoftAssert> SoftAssert = ThreadLocal.withInitial(SoftAssert::new);
    public static final Logger logger = LoggerManager.getLogger(BaseClass.class);

  /*  public SoftAssert getSoftAssert() {
        return SoftAssert.get();
    } */

    @BeforeSuite
    public void loadConfig() throws IOException {

        prop = new Properties();
        FileInputStream fis = new FileInputStream("src/main/resources/config.properties");
        prop.load(fis);
       // ExtentManager.getReporter();

        //Start the Extent Report
      //  ExtentManager.getReporter(); implemented in itest listener
        logger.info("Configuration properties loaded and Extent Report initialized");
    }

    @BeforeClass(alwaysRun = true)
    public  synchronized void  setup() {
        System.out.println("Setting up WebDriver for: " + this.getClass().getSimpleName());
        launchBrowser();
        configureBrowser();
        staticWait(1);

        logger.info("WebDriver instance initialized and Browser maximized for");
        // Sample logger message
        logger.info("WebDriver Initialized and Browser Maximized");
        logger.trace("This is a Trace message");
        logger.error("This is a error message");
        logger.debug("This is a debug message");
        logger.fatal("This is a fatal message");
        logger.warn("This is a warm message");


        // Initialize ActionDriver once per test
      /*  if (actionDriver == null) {
            actionDriver = new ActionDriver(driver);
            System.out.println("Action driver instance created");
            logger.info("ActtionDriver instance is crested"+Thread.currentThread().getId());
        }*/


        actionDriver.set(new ActionDriver(driver.get()));
        logger.info("ActionDriver initialized for thrread"+Thread.currentThread());
    }

    private void launchBrowser() {
        String browser = prop.getProperty("browser");

        if (browser.equalsIgnoreCase("chrome")) {

            // Create ChromeOptions
            ChromeOptions options = new ChromeOptions();
         //   options.addArguments("--headless"); // Run Chrome in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU for headless mode
            //options.addArguments("--window-size=1920,1080"); // Set window size
            options.addArguments("--disable-notifications"); // Disable browser notifications
            options.addArguments("--no-sandbox"); // Required for some CI environments like Jenkins
            options.addArguments("--disable-dev-shm-usage"); // Resolve issues in resource-limited environments

            driver.set(new ChromeDriver(options));
            ExtentManager.registerDriver(getDriver());
            logger.info("Chrome Browser intialized  Launched");
        }

        else if (browser.equalsIgnoreCase("firefox")) {

            // Create FirefoxOptions
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments("--headless"); // Run Firefox in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU rendering (useful for headless mode)
            options.addArguments("--width=1920"); // Set browser width
            options.addArguments("--height=1080"); // Set browser height
            options.addArguments("--disable-notifications"); // Disable browser notifications
            options.addArguments("--no-sandbox"); // Needed for CI/CD environments
            options.addArguments("--disable-dev-shm-usage"); // Prevent crashes in low-resource environments

            driver.set(new FirefoxDriver(options));
            ExtentManager.registerDriver(getDriver());
            logger.info("FirefoxDriver Instance is created");
        }

        else if (browser.equalsIgnoreCase("edge")) {

            EdgeOptions options = new EdgeOptions();
            options.addArguments("--headless"); // Run Edge in headless mode
            options.addArguments("--disable-gpu"); // Disable GPU acceleration
            options.addArguments("--window-size=1920,1080"); // Set window size
            options.addArguments("--disable-notifications"); // Disable pop-up notifications
            options.addArguments("--no-sandbox"); // Needed for CI/CD
            options.addArguments("--disable-dev-shm-usage"); // Prevent resource-limited crashes

            driver.set(new EdgeDriver(options));
            ExtentManager.registerDriver(getDriver());
            logger.info("EdgeDriver Instance is created");
        } else {
            throw new IllegalArgumentException("Browser not supported: " + browser);
        }
    }

    private void configureBrowser() {

        getDriver().manage()
                .timeouts()
                .implicitlyWait(Duration.ofSeconds(Integer.parseInt(prop.getProperty("implicitWait"))));

        getDriver().manage().window().maximize();

        try {
            getDriver().get(prop.getProperty("url"));
        } catch (Exception e) {
            System.out.println("Failed to Navigate to URL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @AfterClass(alwaysRun = true)
    public synchronized void  tearDown() {
        if (getDriver() != null) {
            try {
                getDriver().quit();
                // No need for Thread.sleep(2000) - WebDriver.quit() is synchronous
            } catch (Exception e) {
                System.out.println("Unable to quit driver: " + e.getMessage());
                logger.error("Unable to quit driver", e);
            }
        }
        System.out.println("WebDriver instance closed ");

        // IMPORTANT: Removing ThreadLocal references is crucial for multi-threading
        driver.remove();
        actionDriver.remove();
    }

    public static Properties getProp() {
        return prop;
    }

    public static ActionDriver getActionDriver() {
        if (actionDriver.get() == null) {
            throw new IllegalStateException("ActionDriver not initialized");
        }
        return actionDriver.get();
    }

    public void setDriver(ThreadLocal<WebDriver> driver) {
        this.driver = driver;
    }

    public void staticWait(int seconds) {
        LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(seconds));
    }

    public static  WebDriver getDriver() {
        if (driver.get() == null) {
            throw new IllegalStateException("WebDriver is not initialized");
        }
        return driver.get();
    }
}