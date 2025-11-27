package com.oms.utilities;

import com.oms.actiondriver.ActionDriver;
import com.oms.base.BaseClass;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.interactions.MoveTargetOutOfBoundsException;
import org.openqa.selenium.support.ui.Select;
import org.apache.logging.log4j.Logger;
import java.util.function.Supplier;

public class SeleniumActionSafeExecutor {

    private final ActionDriver action;
    private static final Logger logger = BaseClass.logger;

    // GLOBAL RETRY CONFIG
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY_MS = 700;

    public SeleniumActionSafeExecutor(ActionDriver action) {
        this.action = action;
        logger.info("Safe Selenium Action Executor Initialized.");
    }

    // UTILITY HELPERS
    private String element(By by) { return action.getElementDescription(by); }

    private void green(By by) { action.applyBorder(by, "green"); }

    private void red(By by) { action.applyBorder(by, "red"); }

    // ========================================================================
    // UNIVERSAL RETRY HANDLER - COMPREHENSIVE EXCEPTION COVERAGE
    // ========================================================================
    private <T> T retryOperation(By by, Supplier<T> actionLogic, String actionName) {
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                logger.info("Attempt " + attempt + "/" + MAX_RETRIES + " → " + actionName +
                        " | Element: " + element(by));
                return actionLogic.get();
            }

            // ===================== RETRYABLE EXCEPTIONS ==========================
            catch (StaleElementReferenceException e) {
                logger.warn("StaleElement on attempt " + attempt + ". Retrying → " + element(by));
                sleep(RETRY_DELAY_MS);
            }
            catch (ElementClickInterceptedException e) {
                logger.warn("Click Intercepted: Trying again... Attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (ElementNotInteractableException e) {
                logger.warn("Element not interactable on attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (NoSuchElementException e) {
                logger.warn("Element missing on attempt " + attempt + ": " + element(by));
                sleep(RETRY_DELAY_MS);
            }
            catch (TimeoutException e) {
                logger.warn("Timeout waiting for element. Attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (UnhandledAlertException e) {
                logger.warn("Unhandled alert during attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (MoveTargetOutOfBoundsException e) {
                logger.warn("Move target out of bounds on attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (NoSuchFrameException e) {
                logger.warn("Frame not found on attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (NoSuchWindowException e) {
                logger.warn("Window not found on attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (InvalidArgumentException e) {
                logger.warn("Invalid argument on attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }
            catch (JavascriptException e) {
                logger.warn("JavaScript error on attempt " + attempt);
                sleep(RETRY_DELAY_MS);
            }

            // ================= NON-RETRYABLE ERRORS ==========================
            catch (InvalidSelectorException e) {
                throw new InvalidSelectorException("Invalid locator used → " + element(by));
            }
            catch (WebDriverException e) {
                if (e.getMessage().contains("target frame detached") ||
                        e.getMessage().contains("disconnected")) {
                    logger.warn("WebDriver frame/session issue – retrying: " + attempt);
                    sleep(RETRY_DELAY_MS);
                } else {
                    throw new RuntimeException("WebDriver fatal issue during: " + actionName, e);
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Session not created during: " + actionName, e);
            }
        }

        red(by);
        throw new RuntimeException("FAILURE: " + actionName +
                " failed after " + MAX_RETRIES + " attempts → " + element(by));
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }

    // ========================================================================
    // CORE ELEMENT ACTIONS
    // ========================================================================
    public void clickSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeClickable(by);
            BaseClass.getDriver().findElement(by).click();
            green(by);
            logger.info("CLICK SUCCESS → " + element(by));
            return null;
        }, "Click Action");
    }

    public void enterTextSafe(By by, String value) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            WebElement el = BaseClass.getDriver().findElement(by);
            el.clear();
            el.sendKeys(value);
            green(by);
            logger.info("ENTER TEXT SUCCESS → '" + value + "' into " + element(by));
            return null;
        }, "EnterText");
    }

    public String getTextSafe(By by) {
        return retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            String text = BaseClass.getDriver().findElement(by).getText();
            green(by);
            logger.info("GET TEXT SUCCESS → '" + text + "' from " + element(by));
            return text;
        }, "GetText");
    }

    public String getAttributeSafe(By by, String attributeName) {
        return retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            String value = BaseClass.getDriver().findElement(by).getAttribute(attributeName);
            green(by);
            logger.info("GET ATTRIBUTE SUCCESS → '" + attributeName + "' from " + element(by));
            return value;
        }, "GetAttribute");
    }

    public boolean isDisplayedSafe(By by) {
        return retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            boolean displayed = BaseClass.getDriver().findElement(by).isDisplayed();
            green(by);
            logger.info("IS DISPLAYED → " + displayed + " for " + element(by));
            return displayed;
        }, "IsDisplayed");
    }

    public boolean isEnabledSafe(By by) {
        return retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            boolean enabled = BaseClass.getDriver().findElement(by).isEnabled();
            green(by);
            logger.info("IS ENABLED → " + enabled + " for " + element(by));
            return enabled;
        }, "IsEnabled");
    }

    public void clearTextSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            BaseClass.getDriver().findElement(by).clear();
            green(by);
            logger.info("CLEAR TEXT SUCCESS → " + element(by));
            return null;
        }, "ClearText");
    }

    // ========================================================================
    // DROPDOWN ACTIONS
    // ========================================================================
    public void selectByTextSafe(By by, String value) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            action.selectByVisibleText(by, value);
            green(by);
            logger.info("DROPDOWN SELECT BY TEXT SUCCESS → '" + value + "' from " + element(by));
            return null;
        }, "SelectByText");
    }

    public void selectByValueSafe(By by, String value) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            new Select(BaseClass.getDriver().findElement(by)).selectByValue(value);
            green(by);
            logger.info("DROPDOWN SELECT BY VALUE SUCCESS → '" + value + "' from " + element(by));
            return null;
        }, "SelectByValue");
    }

    public void selectByIndexSafe(By by, int index) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            new Select(BaseClass.getDriver().findElement(by)).selectByIndex(index);
            green(by);
            logger.info("DROPDOWN SELECT BY INDEX SUCCESS → index " + index + " from " + element(by));
            return null;
        }, "SelectByIndex");
    }

    // ========================================================================
    // MOUSE ACTIONS
    // ========================================================================
    public void moveToElementSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            action.moveToElement(by);
            green(by);
            logger.info("MOVE TO ELEMENT SUCCESS → " + element(by));
            return null;
        }, "MoveToElement");
    }

    public void doubleClickSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeClickable(by);
            action.doubleClick(by);
            green(by);
            logger.info("DOUBLE CLICK SUCCESS → " + element(by));
            return null;
        }, "DoubleClick");
    }

    public void rightClickSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeClickable(by);
            action.rightClick(by);
            green(by);
            logger.info("RIGHT CLICK SUCCESS → " + element(by));
            return null;
        }, "RightClick");
    }

    public void dragAndDropSafe(By source, By target) {
        retryOperation(source, () -> {
            action.waitForElementToBeVisible(source);
            action.waitForElementToBeVisible(target);
            action.dragAndDrop(source, target);
            green(source);
            green(target);
            logger.info("DRAG & DROP SUCCESS → " + element(source) + " to " + element(target));
            return null;
        }, "DragAndDrop");
    }

    public void clickAndHoldSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeClickable(by);
            new Actions(BaseClass.getDriver()).clickAndHold(BaseClass.getDriver().findElement(by)).perform();
            green(by);
            logger.info("CLICK AND HOLD SUCCESS → " + element(by));
            return null;
        }, "ClickAndHold");
    }

    public void releaseSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            new Actions(BaseClass.getDriver()).release(BaseClass.getDriver().findElement(by)).perform();
            green(by);
            logger.info("MOUSE RELEASE SUCCESS → " + element(by));
            return null;
        }, "Release");
    }

    // ========================================================================
    // SCROLL ACTIONS
    // ========================================================================
    public void scrollToElementSafe(By by) {
        retryOperation(by, () -> {
            action.scrollToElement(by);
            green(by);
            logger.info("SCROLL SUCCESS → " + element(by));
            return null;
        }, "ScrollToElement");
    }

    public void scrollIntoViewSafe(By by) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            JavascriptExecutor js = (JavascriptExecutor) BaseClass.getDriver();
            js.executeScript("arguments[0].scrollIntoView(true);", BaseClass.getDriver().findElement(by));
            green(by);
            logger.info("SCROLL INTO VIEW SUCCESS → " + element(by));
            return null;
        }, "ScrollIntoView");
    }

    // ========================================================================
    // FILE OPERATIONS
    // ========================================================================
    public void uploadFileSafe(By by, String filePath) {
        retryOperation(by, () -> {
            action.waitForElementToBeVisible(by);
            BaseClass.getDriver().findElement(by).sendKeys(filePath);
            green(by);
            logger.info("FILE UPLOAD SUCCESS → " + filePath + " to " + element(by));
            return null;
        }, "FileUpload");
    }

    // ========================================================================
    // WINDOW & FRAME OPERATIONS
    // ========================================================================
    public void switchWindowSafe(String title) {
        try {
            action.switchToWindow(title);
            logger.info("WINDOW SWITCH SUCCESS → " + title);
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch window: " + title, e);
        }
    }

    public void switchFrameSafe(By by) {
        retryOperation(by, () -> {
            action.switchToFrame(by);
            logger.info("FRAME SWITCH SUCCESS → " + element(by));
            return null;
        }, "SwitchFrame");
    }

    public void switchToDefaultContentSafe() {
        try {
            BaseClass.getDriver().switchTo().defaultContent();
            logger.info("SWITCH TO DEFAULT CONTENT SUCCESS");
        } catch (Exception e) {
            throw new RuntimeException("Failed to switch to default content", e);
        }
    }

    // ========================================================================
    // ALERT OPERATIONS (NO RETRY - ALERTS ARE ONE-TIME)
    // ========================================================================
    public void alertAcceptSafe() {
        try {
            BaseClass.getDriver().switchTo().alert().accept();
            logger.info("ALERT ACCEPT SUCCESS");
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present to accept.");
        }
    }

    public void alertDismissSafe() {
        try {
            BaseClass.getDriver().switchTo().alert().dismiss();
            logger.info("ALERT DISMISS SUCCESS");
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present to dismiss.");
        }
    }

    public String alertGetTextSafe() {
        try {
            String text = BaseClass.getDriver().switchTo().alert().getText();
            logger.info("ALERT TEXT → " + text);
            return text;
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present.");
        }
    }

    public void alertSendKeysSafe(String keys) {
        try {
            BaseClass.getDriver().switchTo().alert().sendKeys(keys);
            logger.info("ALERT SEND KEYS SUCCESS → " + keys);
        } catch (NoAlertPresentException e) {
            throw new NoAlertPresentException("No alert present.");
        }
    }

    // ========================================================================
    // KEYBOARD ACTIONS
    // ========================================================================
    public void sendKeysSafe(Keys key) {
        try {
            new Actions(BaseClass.getDriver()).sendKeys(key).perform();
            logger.info("KEYBOARD SEND KEYS SUCCESS → " + key);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send keys: " + key, e);
        }
    }

    // ========================================================================
    // JAVASCRIPT ACTIONS
    // ========================================================================
    public void executeScriptSafe(String script, Object... args) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) BaseClass.getDriver();
            js.executeScript(script, args);
            logger.info("JAVASCRIPT EXECUTE SUCCESS → " + script);
        } catch (Exception e) {
            throw new RuntimeException("JavaScript execution failed: " + script, e);
        }
    }

    public Object executeAsyncScriptSafe(String script, Object... args) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) BaseClass.getDriver();
            Object result = js.executeAsyncScript(script, args);
            logger.info("JAVASCRIPT ASYNC EXECUTE SUCCESS → " + script);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("JavaScript async execution failed: " + script, e);
        }
    }

    // ========================================================================
    // WAIT UTILITIES
    // ========================================================================
    public void waitForElementToDisappearSafe(By by) {
        try {
            action.waitForElementToDisappear(by);
            logger.info("WAIT FOR DISAPPEAR SUCCESS → " + element(by));
        } catch (Exception e) {
            throw new RuntimeException("Failed waiting for element to disappear: " + element(by), e);
        }
    }
}
