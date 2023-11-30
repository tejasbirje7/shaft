package org.shaft.administration.obligatory.tokens.utils;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppLogger {
    private static final String INFO_LOG_LEVEL = "info";

    private static final String DEBUG_LOG_LEVEL = "debug";

    private static final String ERROR_LOG_LEVEL = "error";

    private static final String WARN_LOG_LEVEL = "warn";

    private static Logger logger = LoggerFactory.getLogger(AppLogger.class);

    public static void info(String message) {
        log(message, "info");
    }

    public static void warn(String message) {
        log(message, "warn");
    }

    public static void debug(String message) {
        log(message, "debug");
    }

    public static void error(String message) {
        log(message, "error");
    }

    private static void log(String message, String logLevel) {
        if (logger != null) {
            if (logLevel.equalsIgnoreCase("debug")) {
                logger.debug(message);
            } else if (logLevel.equalsIgnoreCase("warn")) {
                logger.warn(message);
            } else if (logLevel.equalsIgnoreCase("error")) {
                logger.error(message);
            } else {
                logger.info(message);
            }
        } else {
            System.out.println(message);
        }
    }
}
