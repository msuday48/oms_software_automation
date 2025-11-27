package com.oms.listeners;

import com.oms.base.BaseClass;
import com.oms.utilities.ExtentManager;
import com.oms.utilities.RetryAnalyzer;
import org.testng.IAnnotationTransformer;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class TestListener implements ITestListener, IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        annotation.setRetryAnalyzer(RetryAnalyzer.class);
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentManager.startTest(testName);
        ExtentManager.logStep("Test Started: " + testName);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        if (!result.getTestClass().getName().toLowerCase().contains("api")) {
            ExtentManager.logStepWithScreenshot(BaseClass.getDriver(), "Test Passed Successfully!",
                    "Test End: " + testName + " - ✔ Test Passed");
        } else {
            ExtentManager.logStepValidationForAPI("Test End: " + testName + " - ✔ Test Passed");
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String failureMessage = result.getThrowable().getMessage();
        ExtentManager.logStep(failureMessage);
        if(!result.getTestClass().getName().toLowerCase().contains("api")) {
            ExtentManager.logFailure(BaseClass.getDriver(), "Test Failed!", "Test End: " + testName + " - ❌ Test Failed");
        } else {
            ExtentManager.logFailureAPI("Test End: " + testName + " - ❌ Test Failed");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        ExtentManager.logSkip("Test Skipped " + testName);
    }

    // Triggered when a suite Starts
    @Override
    public void onStart(ITestContext context) {
        // Pass the context to ExtentManager to capture XML parameters (Browser, Groups)
        ExtentManager.setupReports(context);
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.endTest();
    }
}