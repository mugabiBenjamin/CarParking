package util;

public class Validator {
    public static boolean isValidPlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }

        // Normalize input: trim and convert to uppercase for case-insensitive
        // validation
        String normalizedPlate = plate.trim().toUpperCase();

        // Check if plate starts with 'U'
        if (!normalizedPlate.startsWith("U")) {
            return false;
        }

        // 1. Normal plate: UAA 123B (U, two letters, space, three digits, one letter)
        if (normalizedPlate.matches("^U[A-Z]{2}\\s[0-9]{3}[A-Z]$")) {
            return true;
        }

        // 2. Government plate: UG 123B (must start with UG, space, three digits, one
        // letter)
        if (normalizedPlate.matches("^UG\\s[0-9]{3}[A-Z]$")) {
            return true;
        }

        // 3. Personalized plate: Starts with U, 2-7 total characters,
        // letters/digits/spaces after U
        if (normalizedPlate.length() >= 2 && normalizedPlate.length() <= 7) {
            String afterU = normalizedPlate.substring(1);
            if (afterU.matches("[A-Z0-9\\s]*")) {
                return true;
            }
        }

        return false;
    }
}