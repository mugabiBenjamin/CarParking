package controller;

import java.util.Optional;

public final class ReportResult {
    private final boolean success;
    private final String message;
    private final String filePath;

    public ReportResult(boolean success, String message, String filePath) {
        this.success = success;
        this.message = normalizeMessage(message);
        this.filePath = normalizeOptionalText(filePath);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getFilePath() {
        return filePath;
    }

    public Optional<String> findFilePath() {
        if (filePath.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(filePath);
    }

    private String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No report result message provided";
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}