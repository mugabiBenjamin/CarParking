package util;

public class Validator {
    public static boolean isValidPlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        plate = plate.trim().toUpperCase();

        // Normal plate: UAA 123B (U, two letters, space, three digits, letter)
        if (plate.matches("U[A-Z]{2}\\s\\d{3}[A-Z]")) {
            return true;
        }

        // Government plate: UG 123B (UG, space, three digits, letter)
        if (plate.matches("UG\\s\\d{3}[A-Z]")) {
            return true;
        }

        // Personalized plate: Starts with any letter, 2-8 chars, letters/numbers/spaces
        if (plate.matches("[A-Z][A-Z0-9\\s]{1,7}")) {
            return plate.length() >= 2 && plate.length() <= 8;
        }

        return false;
    }
}