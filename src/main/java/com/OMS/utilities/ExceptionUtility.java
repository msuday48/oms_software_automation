package com.oms.utilities;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public final class ExceptionUtility {

    private ExceptionUtility() { /* utility class - prevent instantiation */ }

    private static final Logger logger = BaseClass.logger;

    // GLOBAL RETRY CONFIG (defaults)
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final int DEFAULT_RETRY_DELAY_MS = 700;

    // simple metrics for retries per action
    private static final ConcurrentHashMap<String, AtomicInteger> retryCounters = new ConcurrentHashMap<>();

    // Random for jitter
    private static final Random jitterRandom = new Random();

    // timestamp formatter for screenshots
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS")
            .withZone(ZoneId.systemDefault());

    // Always fetch the thread-local ActionDriver (ensures thread-safety)
    private static ActionDriver action() {
        return BaseClass.getActionDriver();
    }

    // --------------------- Small helpers ---------------------
    private static String elementDesc(By by) {
        if (by == null) return "NULL_LOCATOR";
        try {
            return action().getElementDescription(by);
        } catch (Exception e) {
            return by.toString();
        }
    }

    private static void green(By by) {
        try { if (by != null) action().applyBorder(by, "green"); } catch (Exception ignored) {}
    }

    private static void red(By by) {
        try { if (by != null) action().applyBorder(by, "red"); } catch (Exception ignored) {}
    }

    private static By nullSafeBy(By by) {
        return by == null ? By.xpath("//*") : by;
    }

    private static void sleepWithInterruptRestore(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
    }

    private static long computeDelayWithJitter(int baseMs, int attempt) {
        // baseMs * attempt (increasing/backoff) + jitter (-150..+150 ms)
        int jitter = jitterRandom.nextInt(301) - 150;
        long delay = (long) baseMs * attempt + jitter;
        return Math.max(50, delay); // don't go below 50ms
    }

    private static void incrementRetryMetric(String actionName) {
        retryCounters.computeIfAbsent(actionName, k -> new AtomicInteger(0)).incrementAndGet();
    }

    private static String takeScreenshot(String actionName) {
        try {
            WebDriver driver = BaseClass.getDriver();
            if (!(driver instanceof TakesScreenshot)) return null;
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String ts = TS.format(Instant.now());
            Path dir = Paths.get("screenshots");
            Files.createDirectories(dir);
            Path dest = dir.resolve(actionName.replaceAll("\\\\W+", "_") + "_" + ts + ".png");
            Files.copy(src.toPath(), dest);
            return dest.toAbsolutePath().toString();
        } catch (IOException | WebDriverException e) {
            logger.warn("Failed to capture screenshot: " + e.getMessage());
            return null;
        }
    }

    // --------------------- Retry handler (private, improved) ---------------------
    private static <T> T retryOperation(By by, Supplier<T> supplier, String actionName,
                                        int maxRetries, int baseDelayMs, boolean markSuccess) {

        By safeBy = nullSafeBy(by);
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.info("Attempt " + attempt + "/" + maxRetries + " → " + actionName + " | Element: " + elementDesc(safeBy));
                T result = supplier.get();
                // optional visual success mark
                if (markSuccess) {
                    try { green(by); } catch (Exception ignored) {}
                }
                return result;
            }

            // Retryable exceptions
            catch (StaleElementReferenceException e) {
                logger.warn("StaleElement on attempt " + attempt + ". Retrying → " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (ElementClickInterceptedException e) {
                logger.warn("Click intercepted on attempt " + attempt + ". Retrying → " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (ElementNotInteractableException e) {
                logger.warn("Element not interactable on attempt " + attempt + ". Retrying → " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (NoSuchElementException e) {
                logger.warn("Element missing on attempt " + attempt + ": " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (TimeoutException e) {
                logger.warn("Timeout waiting for element on attempt " + attempt + ": " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (UnhandledAlertException e) {
                logger.warn("Unhandled alert during attempt " + attempt + " for " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (MoveTargetOutOfBoundsException e) {
                logger.warn("Move target out of bounds on attempt " + attempt + " for " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (NoSuchFrameException e) {
                logger.warn("Frame not found on attempt " + attempt + " for " + elementDesc(safeBy));
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (NoSuchWindowException e) {
                logger.warn("Window not found on attempt " + attempt + " while doing " + actionName);
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            // NOTE: InvalidArgumentException moved to fatal (no retry) - refine as requested

            catch (JavascriptException e) {
                logger.warn("JavaScript error on attempt " + attempt + " while doing " + actionName);
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (org.openqa.selenium.NoSuchSessionException e) {
                logger.warn("NoSuchSessionException on attempt " + attempt + ". Retrying...");
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }
            catch (org.openqa.selenium.SessionNotCreatedException e) {
                logger.warn("SessionNotCreatedException on attempt " + attempt + ". Retrying...");
                incrementRetryMetric(actionName);
                sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
            }

            // Non-retryable / fatal -> rethrow or wrap
            catch (InvalidSelectorException e) {
                throw new InvalidSelectorException("Invalid locator used → " + elementDesc(safeBy));
            }
            catch (InvalidArgumentException e) {
                // treated as fatal by default - adjust if you have transient cases
                throw new RuntimeException("Invalid argument while performing " + actionName + " on " + elementDesc(safeBy), e);
            }
            catch (SecurityException e) {
                throw new RuntimeException("Security issue while performing " + actionName + " on " + elementDesc(safeBy), e);
            }
            catch (WebDriverException e) {
                String msg = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
                if (msg.contains("target frame detached") || msg.contains("disconnected") || msg.contains("session not created")) {
                    logger.warn("WebDriver frame/session issue – retrying: " + attempt + " | msg: " + e.getMessage());
                    incrementRetryMetric(actionName);
                    sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
                } else {
                    throw new RuntimeException("WebDriver fatal issue during: " + actionName + " on " + elementDesc(safeBy), e);
                }
            }
            catch (Exception e) {
                if (attempt == maxRetries) {
                    throw new RuntimeException("Unhandled exception during: " + actionName + " on " + elementDesc(safeBy), e);
                } else {
                    logger.warn("Unexpected exception on attempt " + attempt + " for " + elementDesc(safeBy) + ": " + e.getMessage());
                    incrementRetryMetric(actionName);
                    sleepWithInterruptRestore(computeDelayWithJitter(baseDelayMs, attempt));
                }
            }
        }

        // final failure visual & exception — capture screenshot too
        red(nullSafeBy(by));
        String screenshotPath = takeScreenshot(actionName);
        String msg = "FAILURE: " + actionName + " failed after " + maxRetries + " attempts → " + elementDesc(nullSafeBy(by));
        if (screenshotPath != null) msg += " | screenshot: " + screenshotPath;
        throw new RuntimeException(msg);
    }

    // Overloads that keep old simple signature but route to new implementation
    private static <T> T retryOperation(By by, Supplier<T> supplier, String actionName) {
        return retryOperation(by, supplier, actionName, DEFAULT_MAX_RETRIES, DEFAULT_RETRY_DELAY_MS, false);
    }

    // public variant which allows callers to override retries/delay and mark success visually
    private static <T> T retryOperation(By by, Supplier<T> supplier, String actionName,
                                        int maxRetries, int baseDelayMs) {
        return retryOperation(by, supplier, actionName, maxRetries, baseDelayMs, true);
    }

    // --------------------- Public static API (safe wrappers) ---------------------

    // CORE ACTIONS
    public static void clickSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeClickable(by);
            action().click(by);
            logger.info("CLICK SUCCESS → " + elementDesc(by));
            return null;
        }, "Click Action");
    }

    // Example of exposing per-call override (simple usage)
    public static void clickSafe(By by, int maxRetries, int retryDelayMs) {
        retryOperation(by, () -> {
            action().waitForElementToBeClickable(by);
            action().click(by);
            logger.info("CLICK SUCCESS → " + elementDesc(by));
            return null;
        }, "Click Action", maxRetries, retryDelayMs);
    }

    public static void clickUsingJSSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().clickUsingJS(by);
            logger.info("CLICK (JS) SUCCESS → " + elementDesc(by));
            return null;
        }, "ClickUsingJS");
    }

    public static void enterTextSafe(By by, String value) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().enterText(by, value);
            logger.info("ENTER TEXT SUCCESS → '" + value + "' into " + elementDesc(by));
            return null;
        }, "EnterText");
    }

    public static void sendKeysWithActionsSafe(By by, String value) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().sendKeysWithActions(by, value);
            logger.info("SENT KEYS (ACTIONS) SUCCESS → '" + value + "' into " + elementDesc(by));
            return null;
        }, "SendKeysWithActions");
    }

    public static String getTextSafe(By by) {
        return retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            String text = action().getText(by);
            logger.info("GET TEXT SUCCESS → '" + text + "' from " + elementDesc(by));
            return text;
        }, "GetText");
    }

    public static String getAttributeSafe(By by, String attributeName) {
        return retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            String value = BaseClass.getDriver().findElement(by).getAttribute(attributeName);
            logger.info("GET ATTRIBUTE SUCCESS → '" + attributeName + "' from " + elementDesc(by));
            return value;
        }, "GetAttribute");
    }

    public static boolean isDisplayedSafe(By by) {
        return retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            boolean displayed = action().isDisplayed(by);
            logger.info("IS DISPLAYED → " + displayed + " for " + elementDesc(by));
            return displayed;
        }, "IsDisplayed");
    }

    public static boolean isEnabledSafe(By by) {
        return retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            boolean enabled = BaseClass.getDriver().findElement(by).isEnabled();
            logger.info("IS ENABLED → " + enabled + " for " + elementDesc(by));
            return enabled;
        }, "IsEnabled");
    }

    public static void clearTextSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().clearText(by);
            logger.info("CLEAR TEXT SUCCESS → " + elementDesc(by));
            return null;
        }, "ClearText");
    }

    // DROPDOWNS
    public static void selectByTextSafe(By by, String value) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().selectByVisibleText(by, value);
            logger.info("DROPDOWN SELECT BY TEXT SUCCESS → '" + value + "' from " + elementDesc(by));
            return null;
        }, "SelectByText");
    }

    public static void selectByValueSafe(By by, String value) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().selectByValue(by, value);
            logger.info("DROPDOWN SELECT BY VALUE SUCCESS → '" + value + "' from " + elementDesc(by));
            return null;
        }, "SelectByValue");
    }

    public static void selectByIndexSafe(By by, int index) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().selectByIndex(by, index);
            logger.info("DROPDOWN SELECT BY INDEX SUCCESS → index " + index + " from " + elementDesc(by));
            return null;
        }, "SelectByIndex");
    }

    public static List<String> getDropdownOptionsSafe(By by) {
        return retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            List<String> options = action().getDropdownOptions(by);
            logger.info("GET DROPDOWN OPTIONS SUCCESS → " + elementDesc(by) + " -> " + (options == null ? 0 : options.size()) + " options");
            return options;
        }, "GetDropdownOptions");
    }

    public static void selectBootstrapOptionSafe(By openDropdownLocator, By optionsListLocator, String valueToSelect) {
        retryOperation(openDropdownLocator, () -> {
            action().selectBootstrapDropdownOption(openDropdownLocator, optionsListLocator, valueToSelect);
            logger.info("BOOTSTRAP SELECT SUCCESS → '" + valueToSelect + "'");
            return null;
        }, "SelectBootstrapOption");
    }

    // MOUSE
    public static void moveToElementSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().moveToElement(by);
            logger.info("MOVE TO ELEMENT SUCCESS → " + elementDesc(by));
            return null;
        }, "MoveToElement");
    }

    public static void doubleClickSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeClickable(by);
            action().doubleClick(by);
            logger.info("DOUBLE CLICK SUCCESS → " + elementDesc(by));
            return null;
        }, "DoubleClick");
    }

    public static void rightClickSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeClickable(by);
            action().rightClick(by);
            logger.info("RIGHT CLICK SUCCESS → " + elementDesc(by));
            return null;
        }, "RightClick");
    }

    public static void dragAndDropSafe(By source, By target) {
        retryOperation(source, () -> {
            action().waitForElementToBeVisible(source);
            action().waitForElementToBeVisible(target);
            action().dragAndDrop(source, target);
            logger.info("DRAG & DROP SUCCESS → " + elementDesc(source) + " to " + elementDesc(target));
            return null;
        }, "DragAndDrop");
    }

    public static void clickAndHoldSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeClickable(by);
            new Actions(BaseClass.getDriver()).clickAndHold(BaseClass.getDriver().findElement(by)).perform();
            logger.info("CLICK AND HOLD SUCCESS → " + elementDesc(by));
            return null;
        }, "ClickAndHold");
    }

    public static void releaseSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            new Actions(BaseClass.getDriver()).release(BaseClass.getDriver().findElement(by)).perform();
            logger.info("MOUSE RELEASE SUCCESS → " + elementDesc(by));
            return null;
        }, "Release");
    }

    // SCROLL
    public static void scrollToElementSafe(By by) {
        retryOperation(by, () -> {
            action().scrollToElement(by);
            logger.info("SCROLL SUCCESS → " + elementDesc(by));
            return null;
        }, "ScrollToElement");
    }

    public static void scrollIntoViewSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            ((JavascriptExecutor) BaseClass.getDriver()).executeScript("arguments[0].scrollIntoView(true);", BaseClass.getDriver().findElement(by));
            logger.info("SCROLL INTO VIEW SUCCESS → " + elementDesc(by));
            return null;
        }, "ScrollIntoView");
    }

    public static void scrollToBottomSafe() {
        retryOperation(By.xpath("//*"), () -> {
            action().scrollToBottom();
            logger.info("SCROLL TO BOTTOM SUCCESS");
            return null;
        }, "ScrollToBottom");
    }

    // FILE
    public static void uploadFileSafe(By by, String filePath) {
        retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            action().uploadFile(by, filePath);
            logger.info("FILE UPLOAD SUCCESS → " + filePath + " to " + elementDesc(by));
            return null;
        }, "FileUpload");
    }

    // WINDOW / FRAME
    public static void switchWindowSafe(String title) {
        try {
            action().switchToWindow(title);
            logger.info("WINDOW SWITCH SUCCESS → " + title);
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch window: " + title, e);
        }
    }

    public static void switchFrameSafe(By by) {
        retryOperation(by, () -> {
            action().switchToFrame(by);
            logger.info("FRAME SWITCH SUCCESS → " + elementDesc(by));
            return null;
        }, "SwitchFrame");
    }

    public static void switchToDefaultContentSafe() {
        try {
            BaseClass.getDriver().switchTo().defaultContent();
            logger.info("SWITCH TO DEFAULT CONTENT SUCCESS");
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch to default content", e);
        }
    }

    // ALERTS (one-shot)
    public static void alertAcceptSafe() {
        try {
            BaseClass.getDriver().switchTo().alert().accept();
            logger.info("ALERT ACCEPT SUCCESS");
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present to accept.");
        }
    }

    public static void alertDismissSafe() {
        try {
            BaseClass.getDriver().switchTo().alert().dismiss();
            logger.info("ALERT DISMISS SUCCESS");
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present to dismiss.");
        }
    }

    public static String alertGetTextSafe() {
        try {
            String text = BaseClass.getDriver().switchTo().alert().getText();
            logger.info("ALERT TEXT → " + text);
            return text;
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present.");
        }
    }

    public static void alertSendKeysSafe(String keys) {
        try {
            BaseClass.getDriver().switchTo().alert().sendKeys(keys);
            logger.info("ALERT SEND KEYS SUCCESS → " + keys);
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present.");
        }
    }

    // KEYBOARD / JS / WAIT / MISC
    public static void sendKeysSafe(Keys key) {
        retryOperation(By.xpath("//*"), () -> {
            new Actions(BaseClass.getDriver()).sendKeys(key).perform();
            logger.info("KEYBOARD SEND KEYS SUCCESS → " + key);
            return null;
        }, "SendKeys");
    }

    public static void executeScriptSafe(String script, Object... args) {
        retryOperation(By.xpath("//*"), () -> {
            ((JavascriptExecutor) BaseClass.getDriver()).executeScript(script, args);
            logger.info("JAVASCRIPT EXECUTE SUCCESS → " + script);
            return null;
        }, "ExecuteScript");
    }

    public static Object executeAsyncScriptSafe(String script, Object... args) {
        return retryOperation(By.xpath("//*"), () -> {
            Object result = ((JavascriptExecutor) BaseClass.getDriver()).executeAsyncScript(script, args);
            logger.info("JAVASCRIPT ASYNC EXECUTE SUCCESS → " + script);
            return result;
        }, "ExecuteAsyncScript");
    }

    public static void waitForElementToDisappearSafe(By by) {
        retryOperation(by, () -> {
            action().waitForElementToDisappear(by);
            logger.info("WAIT FOR DISAPPEAR SUCCESS → " + elementDesc(by));
            return null;
        }, "WaitForElementToDisappear");
    }

    public static void waitForPageLoadSafe(int seconds) {
        retryOperation(By.xpath("//*"), () -> {
            action().waitForPageLoad(seconds);
            logger.info("WAIT FOR PAGE LOAD SUCCESS → " + seconds + "s");
            return null;
        }, "WaitForPageLoad");
    }

    public static void refreshPageSafe() {
        retryOperation(By.xpath("//*"), () -> {
            action().refreshPage();
            logger.info("REFRESH PAGE SUCCESS");
            return null;
        }, "RefreshPage");
    }

    public static String getCurrentURLSafe() {
        return retryOperation(By.xpath("//*"), () -> {
            String url = action().getCurrentURL();
            logger.info("GET CURRENT URL → " + url);
            return url;
        }, "GetCurrentURL");
    }

    public static void maximizeWindowSafe() {
        retryOperation(By.xpath("//*"), () -> {
            action().maximizeWindow();
            logger.info("MAXIMIZE WINDOW SUCCESS");
            return null;
        }, "MaximizeWindow");
    }

    public static void highlightElementSafe(By by) {
        retryOperation(by, () -> {
            action().highlightElementJS(by);
            logger.info("HIGHLIGHT ELEMENT SUCCESS → " + elementDesc(by));
            return null;
        }, "HighlightElement");
    }

    public static boolean compareTextSafe(By by, String expectedText) {
        return retryOperation(by, () -> {
            action().waitForElementToBeVisible(by);
            boolean result = action().compareText(by, expectedText);
            logger.info("COMPARE TEXT RESULT → " + result + " for " + elementDesc(by));
            return result;
        }, "CompareText");
    }

    // utility: expose retry metrics (simple snapshot)
    public static int getRetryCount(String actionName) {
        AtomicInteger ai = retryCounters.get(actionName);
        return ai == null ? 0 : ai.get();
    }
}
