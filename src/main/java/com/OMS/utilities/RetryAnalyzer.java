package com.oms.utilities;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

// Implementation of TestNG's IRetryAnalyzer to retry failed test cases automatically.
public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int maxRetryCount = 2;

    // Determines whether a failed test result should be retried.
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }
}