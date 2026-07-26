package controller;

public final class LoadDataResult {
    private final boolean success;
    private final String message;

    public LoadDataResult(boolean success, String message) {
        this.success = success;
        this.message = normalizeMessage(message);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    private String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No load data result message provided";
        }

        return value.trim();
    }
}