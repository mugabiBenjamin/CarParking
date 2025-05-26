package controller;

public class LoadDataResult {
    private final boolean success;
    private final String message;

    public LoadDataResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}