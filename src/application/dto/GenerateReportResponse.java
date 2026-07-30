package application.dto;

public final class GenerateReportResponse {
    private final String filePath;

    public GenerateReportResponse(String filePath) {
        this.filePath = normalize(filePath);
    }

    public String getFilePath() {
        return filePath;
    }

    private String normalize(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}