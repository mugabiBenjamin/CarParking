package util;

public class Validator {
    public static boolean isValidPlate(String plate) {
        return plate != null && plate.matches("[A-Z]{3} \\d{3}[A-Z]");
    }
}
