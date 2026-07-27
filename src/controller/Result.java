package controller;

import domain.entities.ParkingSlot;

import java.util.Optional;

public final class Result {
    private final boolean success;
    private final ParkingSlot slot;
    private final String message;
    private final int unparkedCount;

    public Result(boolean success, String message) {
        this(success, null, message, 0);
    }

    public Result(boolean success, ParkingSlot slot, String message) {
        this(success, slot, message, 0);
    }

    public Result(int unparkedCount, String message) {
        this(unparkedCount > 0, null, message, validateUnparkedCount(unparkedCount));
    }

    private Result(boolean success, ParkingSlot slot, String message, int unparkedCount) {
        this.success = success;
        this.slot = slot;
        this.message = normalizeMessage(message);
        this.unparkedCount = validateUnparkedCount(unparkedCount);
    }

    public static Result success(String message) {
        return new Result(true, message);
    }

    public static Result failure(String message) {
        return new Result(false, message);
    }

    public static Result found(ParkingSlot slot, String message) {
        if (slot == null) {
            throw new IllegalArgumentException("Found result must include a parking slot");
        }

        return new Result(true, slot, message);
    }

    public static Result notFound(String message) {
        return new Result(false, null, message);
    }

    public static Result batchUnparked(int unparkedCount, String message) {
        return new Result(unparkedCount, message);
    }

    public boolean isSuccess() {
        return success;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public Optional<ParkingSlot> findSlot() {
        return Optional.ofNullable(slot);
    }

    public String getMessage() {
        return message;
    }

    public int getUnparkedCount() {
        return unparkedCount;
    }

    private static int validateUnparkedCount(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Unparked count cannot be negative");
        }

        return value;
    }

    private static String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No result message provided";
        }

        return value.trim();
    }
}