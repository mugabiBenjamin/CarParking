package controller;

import model.ParkingSlot;

public class FindCarResult {
    private final boolean found;
    private final String message;
    private final ParkingSlot slot;

    public FindCarResult(boolean found, String message, ParkingSlot slot) {
        this.found = found;
        this.message = message;
        this.slot = slot;
    }

    public boolean isFound() {
        return found;
    }

    public String getMessage() {
        return message;
    }

    public ParkingSlot getSlot() {
        return slot;
    }
}