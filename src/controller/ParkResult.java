package controller;

import domain.entities.ParkingSlot;

import java.util.Optional;

public final class ParkResult {
    private final boolean success;
    private final String message;
    private final ParkingSlot slot;
    private final String licensePlate;

    public ParkResult(boolean success, String message, ParkingSlot slot, String licensePlate) {
        if (success && slot == null) {
            throw new IllegalArgumentException("Successful parking result must include a parking slot");
        }

        this.success = success;
        this.message = normalizeMessage(message);
        this.slot = slot;
        this.licensePlate = normalizeOptionalText(licensePlate);
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

    public Optional<ParkingSlot> findSlot() {
        return Optional.ofNullable(slot);
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    private String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No parking result message provided";
        }

        return value.trim();
    }

    private String normalizeOptionalText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}