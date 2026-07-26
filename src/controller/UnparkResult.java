package controller;

public final class UnparkResult {
    private final boolean success;
    private final String message;
    private final int slotNumber;
    private final String licensePlate;

    public UnparkResult(boolean success, String message, int slotNumber, String licensePlate) {
        if (success && slotNumber <= 0) {
            throw new IllegalArgumentException("Successful unpark result must include a valid slot number");
        }

        this.success = success;
        this.message = normalizeMessage(message);
        this.slotNumber = slotNumber;
        this.licensePlate = normalizeOptionalText(licensePlate);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    private String normalizeMessage(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "No unpark result message provided";
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