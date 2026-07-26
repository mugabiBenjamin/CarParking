package util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Logger() {
        throw new UnsupportedOperationException("Logger class cannot be instantiated");
    }

    public static void info(String message) {
        write("INFO", sanitize(message), false);
    }

    public static void log(String message) {
        info(message);
    }

    public static void warn(String message) {
        write("WARN", sanitize(message), false);
    }

    public static void error(String message) {
        write("ERROR", sanitize(message), true);
    }

    public static void error(String message, Throwable throwable) {
        String safeMessage = sanitize(message);

        if (throwable == null) {
            error(safeMessage);
            return;
        }

        write("ERROR", safeMessage + " Cause: " + sanitize(throwable.getMessage()), true);
    }

    private static void write(String level, String message, boolean errorOutput) {
        String logMessage = "[" + LocalDateTime.now().format(FORMATTER) + "] " + level + ": " + message;

        if (errorOutput) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }
    }

    private static String sanitize(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "No message provided";
        }

        return message
                .replaceAll("(?i)(password\\s*=\\s*)[^\\s]+", "$1[REDACTED]")
                .replaceAll("(?i)(token\\s*=\\s*)[^\\s]+", "$1[REDACTED]")
                .replaceAll("(?i)(secret\\s*=\\s*)[^\\s]+", "$1[REDACTED]")
                .replaceAll("(?i)(key\\s*=\\s*)[^\\s]+", "$1[REDACTED]");
    }
}