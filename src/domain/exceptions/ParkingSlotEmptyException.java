package domain.exceptions;

public final class ParkingSlotEmptyException extends DomainException {
    public ParkingSlotEmptyException(int slotNumber) {
        super("Slot " + slotNumber + " is already empty");
    }

    public ParkingSlotEmptyException(String message) {
        super(message);
    }
}