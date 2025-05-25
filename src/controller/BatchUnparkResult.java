package controller;

public class BatchUnparkResult {
    private final int unparkedCount;
    private final String message;

    public BatchUnparkResult(int unparkedCount, String message) {
        this.unparkedCount = unparkedCount;
        this.message = message;
    }

    public int getUnparkedCount() {
        return unparkedCount;
    }

    public String getMessage() {
        return message;
    }
}