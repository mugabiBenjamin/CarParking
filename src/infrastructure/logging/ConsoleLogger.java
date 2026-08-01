package infrastructure.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class ConsoleLogger implements AppLogger {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void info(String message) {
        write("INFO", sanitize(message), false);
    }

    @Override
    public void warn(String message) {
        write("WARN", sanitize(message), false);
    }

    @Override
    public void error(String message) {
        write("ERROR", sanitize(message), true);
    }

    @Override
    public void error(String message, Throwable throwable) {
        if (throwable == null) {
            error(message);
            return;
        }

        write(
                "ERROR",
                sanitize(message) + " Cause: " + sanitize(throwable.getMessage()),
                true
        );
    }

    private void write(String level, String message, boolean errorOutput) {
        String logMessage = "[" + LocalDateTime.now().format(FORMATTER) + "] " + level + ": " + message;

        if (errorOutput) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }
    }

    private String sanitize(String message) {
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