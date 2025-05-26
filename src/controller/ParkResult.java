package controller;

import model.ParkingSlot;

public class ParkResult {
    private final boolean success;
    private final String message;
    private final ParkingSlot slot;
    private final String licensePlate;

    public ParkResult(boolean success, String message, ParkingSlot slot, String licensePlate) {
        this.success = success;
        this.message = message;
        this.slot = slot;
        this.licensePlate = licensePlate;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public String getLicensePlate() {
        return licensePlate;
    }
}