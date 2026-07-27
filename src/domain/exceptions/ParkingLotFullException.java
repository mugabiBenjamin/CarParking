package domain.exceptions;

public final class ParkingLotFullException extends DomainException {
    public ParkingLotFullException() {
        super("Parking lot is full");
    }

    public ParkingLotFullException(String message) {
        super(message);
    }
}