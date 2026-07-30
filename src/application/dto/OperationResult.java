package application.dto;

import java.util.Optional;

public final class OperationResult<T> {
    private final boolean success;
    private final String message;
    private final T data;
    private final ErrorResponse error;

    private OperationResult(boolean success, String message, T data, ErrorResponse error) {
        this.success = success;
        this.message = normalizeMessage(message);
        this.data = data;
        this.error = error;
    }

    public static <T> OperationResult<T> success(String message, T data) {
        return new OperationResult<>(true, message, data, null);
    }

    public static <T> OperationResult<T> success(String message) {
        return new OperationResult<>(true, message, null, null);
    }

    public static <T> OperationResult<T> failure(String message, ErrorResponse error) {
        return new OperationResult<>(false, message, null, error);
    }

    public static <T> OperationResult<T> failure(String code, String message, String recoveryStep) {
        return failure(message, new ErrorResponse(code, message, recoveryStep));
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Optional<T> findData() {
        return Optional.ofNullable(data);
    }

    public ErrorResponse getError() {
        return error;
    }

    public Optional<ErrorResponse> findError() {
        return Optional.ofNullable(error);
    }

    private static String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Operation completed";
        }

        return value.trim();
    }
}