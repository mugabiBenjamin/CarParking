package controller;

public final class BatchUnparkResult {
    private final int unparkedCount;
    private final String message;

    public BatchUnparkResult(int unparkedCount, String message) {
        if (unparkedCount < 0) {
            throw new IllegalArgumentException("Unparked count cannot be negative");
        }

        this.unparkedCount = unparkedCount;
        this.message = normalizeMessage(message);
    }

    public boolean isSuccess() {
        return unparkedCount > 0;
    }

    public int getUnparkedCount() {
        return unparkedCount;
    }

    public String getMessage() {
        return message;
    }

    private String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No batch unpark result message provided";
        }

        return value.trim();
    }
}