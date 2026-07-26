package controller;

import model.ParkingSlot;

import java.util.Optional;

public final class FindCarResult {
    private final boolean found;
    private final String message;
    private final ParkingSlot slot;

    public FindCarResult(boolean found, String message, ParkingSlot slot) {
        if (found && slot == null) {
            throw new IllegalArgumentException("Found result must include a parking slot");
        }

        this.found = found;
        this.message = normalizeMessage(message);
        this.slot = slot;
    }

    public boolean isFound() {
        return found;
    }

    public boolean isSuccess() {
        return found;
    }

    public String getMessage() {
        return message;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public Optional<ParkingSlot> findSlot() {
        return Optional.ofNullable(slot);
    }

    private String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No find car result message provided";
        }

        return value.trim();
    }
}