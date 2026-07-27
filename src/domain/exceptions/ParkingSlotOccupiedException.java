package domain.exceptions;

public final class ParkingSlotOccupiedException extends DomainException {
    public ParkingSlotOccupiedException(int slotNumber) {
        super("Slot " + slotNumber + " is already occupied");
    }

    public ParkingSlotOccupiedException(String message) {
        super(message);
    }
}