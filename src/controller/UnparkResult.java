package controller;

public class UnparkResult {
    private final boolean success;
    private final String message;
    private final int slotNumber;
    private final String licensePlate;

    public UnparkResult(boolean success, String message, int slotNumber, String licensePlate) {
        this.success = success;
        this.message = message;
        this.slotNumber = slotNumber;
        this.licensePlate = licensePlate;
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
}