package application.repositories;

import java.util.Optional;

public final class ParkingLotUpdateResult<T> {
    public enum Status {
        COMMITTED,
        CONFLICT,
        FAILED
    }

    private final Status status;
    private final T data;
    private final String message;

    private ParkingLotUpdateResult(Status status, T data, String message) {
        this.status = status == null ? Status.FAILED : status;
        this.data = data;
        this.message = normalizeMessage(message);
    }

    public static <T> ParkingLotUpdateResult<T> committed(T data) {
        return committed(data, "Parking lot update committed successfully");
    }

    public static <T> ParkingLotUpdateResult<T> committed(T data, String message) {
        if (data == null) {
            throw new IllegalArgumentException("Committed parking lot update result must include data");
        }

        return new ParkingLotUpdateResult<>(Status.COMMITTED, data, message);
    }

    public static ParkingLotUpdateResult<Void> committedWithoutData() {
        return committedWithoutData("Parking lot update committed successfully");
    }

    public static ParkingLotUpdateResult<Void> committedWithoutData(String message) {
        return new ParkingLotUpdateResult<>(Status.COMMITTED, null, message);
    }

    public static <T> ParkingLotUpdateResult<T> conflict(String message) {
        return new ParkingLotUpdateResult<>(Status.CONFLICT, null, message);
    }

    public static <T> ParkingLotUpdateResult<T> failed(String message) {
        return new ParkingLotUpdateResult<>(Status.FAILED, null, message);
    }

    public Status getStatus() {
        return status;
    }

    public T getData() {
        if (isCommitted() && data == null) {
            throw new IllegalStateException("Committed result does not contain data. Use findData() for optional access.");
        }

        return data;
    }

    public Optional<T> findData() {
        return Optional.ofNullable(data);
    }

    public String getMessage() {
        return message;
    }

    public boolean isCommitted() {
        return status == Status.COMMITTED;
    }

    public boolean isConflict() {
        return status == Status.CONFLICT;
    }

    public boolean isFailed() {
        return status == Status.FAILED;
    }

    public boolean hasData() {
        return data != null;
    }

    private static String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Parking lot update failed";
        }

        return value.trim();
    }
}