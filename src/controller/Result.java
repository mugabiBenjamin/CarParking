package controller;

import model.ParkingSlot;

public class Result {
    private final boolean success;
    private final ParkingSlot slot;
    private final String message;
    private final int unparkedCount;

    // For ParkResult, UnparkResult, ReportResult, LoadDataResult
    public Result(boolean success, String message) {
        this(success, null, message, 0);
    }

    // For FindCarResult
    public Result(boolean success, ParkingSlot slot, String message) {
        this(success, slot, message, 0);
    }

    // For BatchUnparkResult
    public Result(int unparkedCount, String message) {
        this(unparkedCount > 0, null, message, unparkedCount);
    }

    private Result(boolean success, ParkingSlot slot, String message, int unparkedCount) {
        this.success = success;
        this.slot = slot;
        this.message = message;
        this.unparkedCount = unparkedCount;
    }

    public boolean isSuccess() {
        return success;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public String getMessage() {
        return message;
    }

    public int getUnparkedCount() {
        return unparkedCount;
    }
}