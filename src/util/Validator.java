package util;

import domain.exceptions.InvalidLicensePlateException;
import domain.valueobjects.LicensePlate;

public final class Validator {
    private Validator() {
        throw new UnsupportedOperationException("Validator class cannot be instantiated");
    }

    public static boolean isValidPlate(String plate) {
        return LicensePlate.isValid(plate);
    }

    public static PlateValidationMessage getPlateValidationMessage(String plate) {
        try {
            LicensePlate licensePlate = LicensePlate.of(plate);
            return PlateValidationMessage.valid("Valid license plate: " + licensePlate.getValue());
        } catch (InvalidLicensePlateException exception) {
            return PlateValidationMessage.invalid(exception.getMessage());
        }
    }

    public static String normalizePlate(String plate) {
        return LicensePlate.normalize(plate);
    }

    public static final class PlateValidationMessage {
        private final boolean valid;
        private final String message;

        private PlateValidationMessage(boolean valid, String message) {
            this.valid = valid;
            this.message = normalizeMessage(message);
        }

        public static PlateValidationMessage valid(String message) {
            return new PlateValidationMessage(true, message);
        }

        public static PlateValidationMessage invalid(String message) {
            return new PlateValidationMessage(false, message);
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }

        private static String normalizeMessage(String message) {
            if (message == null || message.trim().isEmpty()) {
                return "No validation message provided";
            }

            return message.trim();
        }
    }
}