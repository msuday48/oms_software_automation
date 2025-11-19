package com.oms.utilities;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class AssertionUtils {

    // Thread-safe SoftAssert instance
    private static final ThreadLocal<SoftAssert> softAssertThreadLocal = ThreadLocal.withInitial(SoftAssert::new);

    /**
     * Returns the SoftAssert instance for the current thread.
     */
    public static SoftAssert getSoftAssert() {
        return softAssertThreadLocal.get();
    }

    /**
     * Performs a soft assertion for equality.
     *
     * @param actual   actual value
     * @param expected expected value
     * @param message  failure message
     */
    public static void softAssertEquals(Object actual, Object expected, String message) {
        getSoftAssert().assertEquals(actual, expected, message);
    }

    /**
     * Performs a soft assertion for truthiness.
     *
     * @param condition condition to check
     * @param message   failure message
     */
    public static void softAssertTrue(boolean condition, String message) {
        getSoftAssert().assertTrue(condition, message);
    }

    /**
     * Performs a soft assertion for falseness.
     *
     * @param condition condition to check
     * @param message   failure message
     */
    public static void softAssertFalse(boolean condition, String message) {
        getSoftAssert().assertFalse(condition, message);
    }

    /**
     * Asserts all soft assertions for the current thread.
     * Call this at the end of the test to report failures.
     */
    public static void assertAll() {
        getSoftAssert().assertAll();
        // Reset after reporting
        softAssertThreadLocal.remove();
    }

    // ---------------- Hard Assertions ----------------

    /**
     * Hard assertion for equality.
     *
     * @param actual   actual value
     * @param expected expected value
     * @param message  failure message
     */
    public static void hardAssertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    /**
     * Hard assertion for truthiness.
     *
     * @param condition condition to check
     * @param message   failure message
     */
    public static void hardAssertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    /**
     * Hard assertion for falseness.
     *
     * @param condition condition to check
     * @param message   failure message
     */
    public static void hardAssertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);
    }
}

