package domain.exceptions;

public final class ParkingSlotNotFoundException extends DomainException {
    public ParkingSlotNotFoundException(int slotNumber) {
        super("Parking slot not found: " + slotNumber);
    }

    public ParkingSlotNotFoundException(String message) {
        super(message);
    }
}