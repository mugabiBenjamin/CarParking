package application.dto;

public final class ErrorResponse {
    private final String code;
    private final String message;
    private final String recoveryStep;

    public ErrorResponse(String code, String message, String recoveryStep) {
        this.code = normalize(code, "UNKNOWN_ERROR");
        this.message = normalize(message, "An unexpected error occurred");
        this.recoveryStep = normalize(recoveryStep, "Try again. If the issue continues, contact support.");
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getRecoveryStep() {
        return recoveryStep;
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }

        return value.trim();
    }
}