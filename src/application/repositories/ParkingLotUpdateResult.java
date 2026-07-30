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
        return new ParkingLotUpdateResult<>(Status.COMMITTED, data, "Parking lot update committed successfully");
    }

    public static <T> ParkingLotUpdateResult<T> committed(T data, String message) {
        return new ParkingLotUpdateResult<>(Status.COMMITTED, data, message);
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

    private static String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Parking lot update failed";
        }

        return value.trim();
    }
}