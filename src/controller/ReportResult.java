package controller;

public class ReportResult {
    private final boolean success;
    private final String message;
    private final String filePath;

    public ReportResult(boolean success, String message, String filePath) {
        this.success = success;
        this.message = message;
        this.filePath = filePath;
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
}