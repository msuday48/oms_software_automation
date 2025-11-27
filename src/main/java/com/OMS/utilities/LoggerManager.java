package com.oms.utilities;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Utility class for managing Log4j2 Logger instances.
public class LoggerManager {

    // Retrieves a Logger instance from Log4j's LogManager.
    public static Logger getLogger(Class<?> clazz) {
        // Note: The provided class parameter is ignored in the current implementation
        // as LogManager.getLogger() without arguments automatically infers the calling class.
        return LogManager.getLogger();
    }
}