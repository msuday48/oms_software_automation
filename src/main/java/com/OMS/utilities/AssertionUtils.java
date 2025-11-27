package com.oms.utilities;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

// Utility class for managing TestNG assertions.
public class AssertionUtils {

    // Provides thread-safe instances of SoftAssert and wrappers for hard assertions.
    private static final ThreadLocal<SoftAssert> softAssertThreadLocal = ThreadLocal.withInitial(SoftAssert::new);

    // Retrieves the thread-safe SoftAssert instance for the current thread.
    public static SoftAssert getSoftAssert() {
        return softAssertThreadLocal.get();
    }

    // Performs a soft assertion that two objects are equal.
    public static void softAssertEquals(Object actual, Object expected, String message) {
        getSoftAssert().assertEquals(actual, expected, message);
    }

    // Performs a soft assertion that a condition is true.
    public static void softAssertTrue(boolean condition, String message) {
        getSoftAssert().assertTrue(condition, message);
    }

    // Performs a soft assertion that a condition is false.
    public static void softAssertFalse(boolean condition, String message) {
        getSoftAssert().assertFalse(condition, message);
    }

    // Collects and asserts all soft assertions made in the current thread.
    // This method should be called at the end of a test method to report any failures.
    public static void assertAll() {
        getSoftAssert().assertAll();
        softAssertThreadLocal.remove();
    }

    // Performs a hard assertion that two objects are equal.
    // Execution stops immediately if this assertion fails.
    public static void hardAssertEquals(Object actual, Object expected, String message) {
        Assert.assertEquals(actual, expected, message);
    }

    // Performs a hard assertion that a condition is true.
    // Execution stops immediately if this assertion fails.
    public static void hardAssertTrue(boolean condition, String message) {
        Assert.assertTrue(condition, message);
    }

    // Performs a hard assertion that a condition is false.
    // Execution stops immediately if this assertion fails.
    public static void hardAssertFalse(boolean condition, String message) {
        Assert.assertFalse(condition, message);
    }
}