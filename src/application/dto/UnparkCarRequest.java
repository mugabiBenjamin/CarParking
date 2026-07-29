package application.dto;

public final class UnparkCarRequest {
    private final int slotNumber;

    public UnparkCarRequest(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }
}