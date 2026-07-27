package domain.exceptions;

import domain.valueobjects.LicensePlate;

public final class DuplicateParkedCarException extends DomainException {
    public DuplicateParkedCarException(LicensePlate licensePlate) {
        super("Car with license plate " + safePlate(licensePlate) + " is already parked");
    }

    public DuplicateParkedCarException(String message) {
        super(message);
    }

    private static String safePlate(LicensePlate licensePlate) {
        if (licensePlate == null) {
            return "unknown";
        }

        return licensePlate.getValue();
    }
}