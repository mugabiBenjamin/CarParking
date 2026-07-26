package util;

import java.util.Locale;
import java.util.regex.Pattern;

public final class Validator {
    private static final Pattern ORDINARY_PRIVATE_PLATE = Pattern.compile("^UA\\s\\d{3}[A-Z]{2}$");
    private static final Pattern LEGACY_PRIVATE_PLATE = Pattern.compile("^U[A-Z]{2}\\s\\d{3}[A-Z]$");
    private static final Pattern GOVERNMENT_PLATE = Pattern.compile("^UG\\s\\d{2}\\s\\d{5}$");
    private static final Pattern LEGACY_GOVERNMENT_PLATE = Pattern.compile("^UG\\s\\d{3}[A-Z]$");
    private static final Pattern DIPLOMATIC_PLATE = Pattern.compile("^CD\\s\\d{2}\\s\\d{2}\\s[A-Z]$");
    private static final Pattern MOTORCYCLE_PLATE = Pattern.compile("^UM[A-Z]\\s\\d{3}[A-Z]{2}$");
    private static final Pattern PERSONALIZED_PLATE = Pattern.compile("^[A-Z][A-Z0-9\\s]{1,7}$");

    private Validator() {
        throw new UnsupportedOperationException("Validator class cannot be instantiated");
    }

    public static boolean isValidPlate(String plate) {
        return getPlateValidationMessage(plate).isValid();
    }

    public static PlateValidationMessage getPlateValidationMessage(String plate) {
        String normalizedPlate = normalizePlate(plate);

        if (normalizedPlate.isEmpty()) {
            return PlateValidationMessage.invalid("License plate is required");
        }

        if (containsUnsafeCharacters(normalizedPlate)) {
            return PlateValidationMessage.invalid("License plate contains unsupported characters");
        }

        if (ORDINARY_PRIVATE_PLATE.matcher(normalizedPlate).matches()) {
            return PlateValidationMessage.valid("Valid ordinary private plate");
        }

        if (LEGACY_PRIVATE_PLATE.matcher(normalizedPlate).matches()) {
            return PlateValidationMessage.valid("Valid legacy private plate");
        }

        if (GOVERNMENT_PLATE.matcher(normalizedPlate).matches()) {
            return PlateValidationMessage.valid("Valid government plate");
        }

        if (LEGACY_GOVERNMENT_PLATE.matcher(normalizedPlate).matches()) {
            return PlateValidationMessage.valid("Valid legacy government plate");
        }

        if (DIPLOMATIC_PLATE.matcher(normalizedPlate).matches()) {
            return PlateValidationMessage.valid("Valid diplomatic plate");
        }

        if (MOTORCYCLE_PLATE.matcher(normalizedPlate).matches()) {
            return PlateValidationMessage.valid("Valid motorcycle plate");
        }

        if (isPersonalizedPlate(normalizedPlate)) {
            return PlateValidationMessage.valid("Valid personalized plate");
        }

        return PlateValidationMessage.invalid(
                "Invalid plate format. Use formats like UA 001AA, UG 32 00042, CD 01 02 U, UMA 001AA, or a valid personalized plate"
        );
    }

    public static String normalizePlate(String plate) {
        if (plate == null) {
            return "";
        }

        return plate
                .trim()
                .replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    private static boolean isPersonalizedPlate(String plate) {
        if (!PERSONALIZED_PLATE.matcher(plate).matches()) {
            return false;
        }

        return plate.length() >= 2 && plate.length() <= 8;
    }

    private static boolean containsUnsafeCharacters(String plate) {
        return !plate.matches("[A-Z0-9\\s]+");
    }

    public static final class PlateValidationMessage {
        private final boolean valid;
        private final String message;

        private PlateValidationMessage(boolean valid, String message) {
            this.valid = valid;
            this.message = message;
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
    }
}